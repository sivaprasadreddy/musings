---
UseCase ID : UC-007
Title: Like and Unlike Posts
Depends On: [UC-003, UC-004, UC-005]
Status: COMPLETED
---

## Actors
- Authenticated user expressing or withdrawing appreciation for a post.
- Anonymous visitor viewing total like counts.

## Preconditions
- The public feed is available.
- A user must be authenticated to like or unlike a post.

## UseCase Details
1. Each post in the feed, including posts loaded through “Load more”, displays its total like count to everyone when the count is greater than zero.
2. Authenticated users see an outline heart for Like and a filled red heart for Unlike, with accessible labels and tooltips. A nonzero count appears immediately to the right of the heart. Anonymous visitors see only nonzero counts without either control.
3. Selecting “Like” records the authenticated user's like. Selecting “Unlike” removes that user's like.
4. On success, HTMX updates the affected post's button and count in place, preserving loaded posts and scroll position.
5. Likes persist across page reloads and login sessions; the feed reflects the stored count and the current user's like state when loaded.
6. Repeating a Like or Unlike request succeeds without adding duplicate likes or removing another user's like.
7. If a request fails, the displayed button and count remain unchanged. An error appears beside the button and the user can retry the action.
8. Only authenticated users may change likes. For an unauthenticated HTMX request, including after session expiry, the library’s recommended Spring Security handlers navigate to the originating page or refresh it when no originating URL is available. The public feed then shows its anonymous view with Login available; likes remain unchanged. Other unauthenticated requests use the normal login redirect.
9. If the requested post no longer exists, likes remain unchanged and “This post is no longer available” appears beside the button.

## Business Rules
- Each authenticated user may have at most one like per post, including their own posts.
- A user may remove only their own like.
- Like and Unlike are idempotent: liking an already-liked post or unliking an unliked post leaves that user's contribution unchanged.
- Repeated or concurrent Like requests from the same user must not create duplicate likes.
- The total count equals the number of users who currently like the post and cannot be negative.
- The acting user is the authenticated user.
- Authentication failures and requests for missing posts must not change likes.

## Acceptance Criteria
- Given a post with no likes, no count is displayed; authenticated users can still select the outline heart to like it.
- Given an anonymous visitor, posts display nonzero counts without Like or Unlike controls.
- Given an authenticated user who has not liked a post, when they select Like, their like is saved, the count increases by one, and the control becomes Unlike.
- Given an authenticated user who has liked a post, when they select Unlike, their like is removed, the count decreases by one, and the control becomes Like.
- Given a user's own post, the same Like and Unlike behavior applies.
- Given repeated or concurrent Like requests from one user, at most one like from that user exists for the post.
- Given repeated Unlike requests, that user's like remains absent and other users' likes remain unchanged.
- Given multiple users liking a post, each contributes independently to the total; one user's Unlike removes only their contribution.
- Given a successful action on any loaded post, HTMX updates its button and count without reloading the page, removing loaded posts, or changing the scroll position.
- Given saved likes, reloading the feed or signing in again displays the stored totals and the current user's corresponding Like or Unlike controls.
- Given a failed request, the displayed button and count remain unchanged, an error appears beside the button, and the action can be retried.
- Given an unauthenticated HTMX request or an expired session, the browser returns to the originating page (or refreshes without an originating URL), the public feed shows its anonymous view with Login available, and likes remain unchanged. Non-HTMX unauthenticated requests require login.
- Given a missing post, the action changes no likes and displays “This post is no longer available” beside the button.

## In Scope
- Persistent per-user likes and removal of those likes.
- Public total like counts and authenticated Like/Unlike controls throughout the feed.
- In-place HTMX updates, idempotency, and failure feedback.

## Out of scope
- Live updates when other users like or unlike a post.
- Lists of users who liked a post.
- Like notifications.
