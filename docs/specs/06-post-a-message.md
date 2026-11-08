---
UseCase ID : UC-006
Title: Post a Message
Status: COMPLETED
Depends On: [UC-003, UC-005]
---

## Actors
- Authenticated user
- Anonymous visitor attempting to publish

## Preconditions
- The user must be authenticated to publish a message.
- The public feed is available.

## UseCase Details
1. The authenticated user expands the “Post a message” composer above the feed.
2. The user enters a title and message content and submits them.
3. The application trims leading and trailing whitespace and validates the input.
4. For valid input, the application saves the message with the authenticated user as its author and records its creation time.
5. The application returns the user to the feed and displays “Your message has been posted”. The new message appears first according to the feed ordering.
6. For invalid input, the composer remains open with the user's input preserved and errors beside the relevant fields. No message is saved.
7. If an anonymous visitor attempts to publish, the application requires login and does not save a message.

## Business Rules
- Only authenticated users may publish.
- The author is always the authenticated user.
- The title is required and must contain at most 200 characters after trimming.
- Content is required and must contain at most 1,000 characters after trimming.
- Whitespace-only titles and content are invalid.
- Validation errors are:
  - Missing title: “Enter a title”.
  - Title too long: “Title must be at most 200 characters”.
  - Missing content: “Enter your message”.
  - Content too long: “Content must be at most 1,000 characters”.

## Acceptance Criteria
- Given an authenticated user and valid input, when they publish, one message is saved with that user as author, appears in the feed, and the success confirmation is displayed.
- Given leading or trailing whitespace, when valid input is saved, that whitespace is removed and internal line breaks are preserved.
- Given a 200-character title and 1,000-character content after trimming, when submitted, the message is accepted.
- Given a missing, whitespace-only, or overlength field, when submitted, no message is saved and the corresponding error is displayed beside the field.
- Given validation failure, the composer remains open and the user's input is preserved for correction.
- Given an anonymous visitor, when they attempt to publish, login is required and no message is saved.

## In Scope
- Message composer and submission.
- Input trimming, validation, and error feedback.
- Author attribution, creation time, and publication confirmation.

## Out of scope
- Editing or deleting messages.
- Drafts, attachments, or rich-text editing.
- Moderation and scheduled publication.
