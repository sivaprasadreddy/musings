---
UseCase ID : UC-001
Title: Initial Project Setup
Status: COMPLETED
Depends On: []
---

## Actors
- Developer
- Continuous integration system

## Preconditions
- The required technology stack is defined in [Tech Stack](../tech-stack.md).
- A project repository is available on GitHub.
- The developer can install the required tools and run Docker Compose.

## UseCase Details
1. The developer initializes the project using the technologies specified in [Tech Stack](../tech-stack.md).
2. The developer specifies the required tool versions using mise and configures mise tasks for project-specific operations, including building, testing, and running the application.
3. The developer configures Docker Compose for the external services required by the selected stack, such as databases and message brokers.
4. If the project uses a SQL database, the developer configures automated database migrations using the migration technology specified in the tech stack.
5. The developer verifies that the initialized project builds successfully and that all tests pass.
6. The developer configures a GitHub Actions CI pipeline to build and test the application on pushes and pull requests.

## Business Rules
- The project must follow the technology choices in the tech stack document.
- Required tool versions must be declared in mise configuration so developers and CI use consistent versions.
- Project-specific tasks must be available through mise, including build, test, application startup, and external-service lifecycle operations.
- Docker Compose must define all external services required to run the application locally; services not required by the project need not be added.
- Projects using a SQL database must support automated, versioned database migrations without requiring manual schema changes.
- A successful build alone is insufficient: all tests must pass before CI reports success.
- The CI pipeline must provision the tools and services needed to build and test the application.

## Acceptance Criteria
- Given the initialized project, when its dependencies and configuration are inspected, it uses the stack defined in [Tech Stack](../tech-stack.md).
- Given a fresh checkout and the documented prerequisites, when the developer installs tools through mise and runs the configured build and test tasks, the build succeeds and all tests pass.
- Given the mise configuration, the required tool versions and tasks for building, testing, running the application, and managing external services are declared.
- Given a local environment, when the configured external-service startup task runs, Docker Compose starts the services required by the application.
- Given running external services, when the configured shutdown task runs, those services stop.
- Given a SQL database with no application schema, when automated migrations run, the required schema is created without manual intervention.
- Given a SQL database with applied migrations, when migrations run again, applied migrations are not reapplied and existing data is preserved.
- Given a push or pull request, when GitHub Actions runs, it builds the application and executes all tests.
- Given a build failure or a failing test, the CI pipeline reports failure; given a successful build and all tests passing, it reports success.

## In Scope
- Project initialization using the specified tech stack.
- A successful build and passing test suite.
- GitHub Actions CI configuration for building and testing.
- Docker Compose configuration for required external services.
- Automated database migrations when a SQL database is used.
- Tool version declarations and project-specific tasks using mise.

## Out of scope
- Application features covered by other use cases.
- Production deployment and continuous delivery.
- Production infrastructure provisioning.
- Backup and disaster recovery procedures.
