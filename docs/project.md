# Musings

## Project Overview

Musings is a small web application for sharing what's on your mind. Visitors can read a public feed of short messages ("musings"). Signed-in users can post their own.

It is a server-rendered monolith:

| Concern | Technology |
|---|---|
| Language / runtime | Java 25 |
| Framework | Spring Boot (Spring MVC), Spring Modulith |
| Views | Thymeleaf with layout dialect and Spring Security extras |
| Interactivity | HTMX (progressive "Load more"), Font Awesome, Tailwind-style utility CSS |
| Security | Spring Security form login, BCrypt password hashing, CSRF protection |
| Persistence | PostgreSQL, Spring Data JPA (Hibernate), Flyway migrations |
| Ops | Spring Boot Actuator, Docker / Docker Compose |
| Testing | JUnit, Testcontainers, Spring Modulith verification, ArchUnit / Taikai |

---

## Features

Features are listed in the order in which they can be implemented. Each one builds only on the ones before it.

| # | Feature | Access |
|---|---|---|
| 1 | [Schema migrations and seed data](#1-schema-migrations-and-seed-data) | Internal |
| 2 | [Public message feed](#2-public-message-feed) | Anyone |
| 3 | [Paginated "Load more" loading](#3-paginated-load-more-loading) | Anyone |
| 4 | [Login and logout](#4-login-and-logout) | Anyone / authenticated users |
| 5 | [Post a message](#5-post-a-message) | Authenticated users |
| 6 | [Health and operations endpoints](#6-health-and-operations-endpoints) | Public (health/info) |

---

## Use Cases

### 1. Schema migrations and seed data

The database schema is versioned and reproducible. This comes first because every other feature depends on the `users` and `posts` tables.

**Actors:** developers, deployment pipeline.

**Behaviour**
1. A fresh database ends up with the schema, an admin user, and sample posts, so the feed is not empty on first run.

**Local development**
- docker-compose starts the Postgres service from `compose.yml`.
- Tests use Testcontainers with PostgreSQL.

---

### 2. Public message feed

The home page (`GET /`) lists the latest messages, newest first, with no login required.

**Actors:** anonymous visitor, authenticated user.

**Main flow**
1. The user opens `/` and redirected to `/posts`.
2. The app loads the first page of posts, sorted by `created_at` descending and then `id` descending.
3. Each post shows its title, author name, a UTC timestamp, and its content. Line breaks in the content are preserved.
4. If the user is authenticated, the "Post a message" composer appears above the feed. Anonymous visitors don't see it. (Added once features 4 and 5 are in place.)

**Alternate flows**
- *No posts exist:* the page shows "No messages yet. Share the first one!".
- *Content contains HTML:* View template engine escapes it, so posts cannot inject markup.

**Rules**
- `GET /` and `GET /posts` are public. Every other endpoint requires authentication.

---

### 3. Paginated "Load more" loading

Long feeds load a page at a time without a full page reload.

**Actors:** anonymous visitor, authenticated user.

**Main flow**
1. The feed shows the first `feed-page-size` posts (10 by default).
2. If more posts exist (`hasNextPage`), a "Load more" button is shown.
3. Clicking it sends an HTMX request, `GET /posts?page=N+1`.
4. The server returns only the feed fragment.
5. The response replaces the button with the new posts and, if needed, a new button, so the feed grows in place.

**Alternate flows**
- *Page parameter is missing:* it defaults to `1`.
- *Page parameter is below 1:* it is clamped to `1`.
- *Last page reached:* no button is rendered.

---

### 4. Login and logout

Users authenticate with their email address and password.

**Actors:** registered user.

**Login flow**
1. The user opens `/login`, which is served as a view controller.
2. The user enters email and password and submits the form to `POST /login`.
3. The email and the password are verified against the BCrypt hash.
4. On success, the user is redirected to "/" and can use authenticated features.

**Logout flow**
1. The user triggers `/logout`.
2. The session ends and the user is redirected to `/`.

**Alternate flows**
- *Bad credentials:* the login page shows "Invalid email or password."
- *Logged out:* the login page can show "You have been logged out."

**Notes**
- There is no self-registration. Accounts are created through database migrations or direct inserts.

---

### 5. Post a message

A signed-in user publishes a titled message to the feed.

**Actors:** authenticated user.

**Preconditions:** the user is logged in.

**Main flow**
1. The user expands the "Post a message" panel on the home page.
2. The user enters a title (up to 200 characters) and content (up to 1,000 characters).
3. The browser submits `POST /posts`.
4. The server validates the input, trims leading and trailing whitespace, and saves the post with the current user as author.
5. The server redirects to `/` and shows the flash message "Your message has been posted". The new post appears at the top of the feed.

**Alternate flows**
- *Validation fails:* the home page is re-rendered with the composer open, inline errors, and the user's input preserved. Nothing is saved. Errors:
  - "Enter a title"
  - "Title must be at most 200 characters"
  - "Enter your message"
  - "Content must be at most 1,000 characters"
- *User is not authenticated:* the request is rejected, and an anonymous request is redirected to the login page.

---

### 6. Health and operations endpoints

Platform tooling can check that the application is running. This comes last because the endpoints must be opened up in the security rules established by the earlier features.

**Actors:** operators, container orchestrators.

**Behaviour**
- Exposes `/health` and `/info`, publicly.
