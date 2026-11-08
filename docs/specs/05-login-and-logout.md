---
Title: Login and Logout
Status: COMPLETED
Depends On: [UC-001, UC-003]
---

## Actors
- Registered user
- Authenticated user

## Preconditions
- The application is available.
- For successful login, the user has an existing account.
- For logout, the user is authenticated.

## UseCase Details
### Login
1. The registered user opens the login experience.
2. The user submits their email address and password.
3. The application verifies the credentials.
4. On success, the application establishes an authenticated state and takes the user to the public feed, with authenticated features available.
5. If credentials are invalid, the user remains unauthenticated and sees “Invalid email or password.”.

### Logout
1. The authenticated user chooses to log out.
2. The application ends the user's authenticated state and takes them to the public feed.
3. The login experience may show “You have been logged out.” as confirmation.

## Business Rules
- Users authenticate using an email address and password.
- Password verification must protect stored credentials from disclosure; passwords must not be stored in readable form.
- Invalid credentials must not grant access to authenticated features.
- Logout removes access to authenticated features until the user signs in again.
- Accounts are provisioned administratively; self-registration is not available.

## Acceptance Criteria
- Given valid credentials for an existing account, when the user signs in, they reach the public feed and can access authenticated features.
- Given invalid credentials, when the user attempts to sign in, authentication fails and “Invalid email or password.” is displayed.
- Given an authenticated user, when they log out, they reach the public feed and can no longer use authenticated features without signing in again.
- Given a visitor without an account, no self-registration workflow is available.

## In Scope
- Email and password login.
- Invalid-credential feedback.
- Logout and return to the public feed.

## Out of scope
- Self-registration and account provisioning workflows.
- Password reset or recovery.
- Alternative sign-in methods or multi-factor authentication.
