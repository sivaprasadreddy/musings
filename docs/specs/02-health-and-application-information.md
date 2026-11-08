---
UseCase ID : UC-002
Title: Health and Application Information
Status: COMPLETED
Depends On: [UC-001]
---

## Actors
- Operator
- Automated monitoring or deployment system

## Preconditions
- The application has been started.

## UseCase Details
1. The actor requests the application's health status or application information through a dedicated access point.
2. The application provides the requested health status or application information without requiring login.
3. The actor uses the health response to check whether the application is running.

## Business Rules
- Health status and application information are publicly accessible.
- These access points do not require authentication.

## Acceptance Criteria
- Given a running application and an unauthenticated actor, when health status is requested, the application responds with its health status.
- Given a running application and an unauthenticated actor, when application information is requested, the application provides it.
- Given an automated monitoring system, it can check application health without an interactive login.

## In Scope
- Public access to application health status.
- Public access to application information.
- Automated health checks.

## Out of scope
- Specific monitoring products, protocols, or access-point addresses.
- Metrics, alerting, dashboards, and automatic recovery.
- Definitions of individual dependency checks or application-information fields.
