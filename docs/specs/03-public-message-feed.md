---
UseCase ID : UC-003
Title: Public Message Feed
Status: COMPLETED
Depends On: [UC-001]
---

## Actors
- Anonymous visitor
- Authenticated user

## Preconditions
- The application is available.

## UseCase Details
1. The actor opens the application's main entry point and reaches the public message feed.
2. The application displays the first group of messages, newest first.
3. Each message displays its title, author name, creation time in UTC, and content with line breaks preserved.
4. Authenticated users can access the message composer above the feed once message publishing is available.
5. If no messages exist, the application displays “No messages yet. Share the first one!”.

## Business Rules
- Reading the feed does not require authentication.
- Messages are ordered by creation time descending, with a consistent descending message-identifier order when creation times are equal.
- Message content is displayed as text; embedded markup must not execute or alter the surrounding presentation.
- Anonymous visitors do not see the message composer.
- Publishing requires authentication, as specified in UC-006.

## Acceptance Criteria
- Given an anonymous visitor, when they open the application, they can read the message feed without signing in.
- Given multiple messages, when the feed is displayed, messages follow the defined order and include the required details.
- Given content with line breaks or markup, when displayed, line breaks are preserved and markup is treated as text.
- Given no messages, when the feed is opened, the empty-feed message is displayed.
- Given an authenticated user and publishing is available, when they open the feed, the composer is available above it.
- Given an anonymous visitor, when they open the feed, the composer is not displayed.

## In Scope
- Public access to the feed.
- Message ordering, display, and empty-feed behavior.
- Visibility of the message composer based on authentication.

## Out of scope
- Loading additional groups of messages, covered by UC-004.
- Authentication and publishing workflows, covered by UC-005 and UC-006.
- Editing, deleting, searching, or filtering messages.
