---
UseCase ID : UC-004
Title: Load More Messages
Status: COMPLETED
Depends On: [UC-003]
---

## Actors
- Anonymous visitor
- Authenticated user

## Preconditions
- The actor has opened the public message feed.

## UseCase Details
1. The application displays the first group of messages, containing up to the configured group size, which defaults to 10.
2. If additional messages exist, the application presents a “Load more” action after the displayed messages.
3. The actor selects “Load more”.
4. The application appends the next group to the existing feed without disrupting the already displayed messages or refreshing the entire view.
5. The action remains available while further messages exist and disappears after the final group.

## Business Rules
- Groups follow the message ordering defined in UC-003.
- The default group size is 10 messages and may be configured.
- If no group number is supplied, the first group is selected.
- A group number below 1 is treated as 1.
- The “Load more” action is shown only when additional messages exist.

## Acceptance Criteria
- Given more than 10 messages and the default group size, when the feed opens, the first 10 messages and a “Load more” action are displayed.
- Given additional messages, when the actor selects “Load more”, the next group is appended in order and previously displayed messages remain visible.
- Given an unchanged set of messages, when successive groups are loaded, messages are neither skipped nor duplicated.
- Given the final group has been displayed, no “Load more” action remains.
- Given no group number or a number below 1, the first group is selected.
- Given a configured group size, each group contains no more than that number of messages.

## In Scope
- Incremental loading of the public feed.
- Group size and first-group defaults.
- Visibility of the “Load more” action.

## Out of scope
- Selection of transport protocols or rendering technologies.
- Automatic loading while scrolling.
- Live updates or consistency guarantees when messages change during browsing.
