# gimmegift-auth

Authentication backend service for the gimmegift platform.

## Overview

The gimmegift-auth service provides a comprehensive suite of authentication and identity management features, including email and password-based signup, login, refresh token handling, password reset workflows, and email verification. The system is designed following Clean Architecture principles, ensuring it remains persistence-agnostic through the use of ports and adapters, which decouples the core business logic from external infrastructure.

## Tech Stack

- Java 25 LTS (Temurin)
- Spring Boot 4.0.x
- Spring Security 7
- Spring Cloud GCP Firestore
- Maven 3.9+
- Docker & Docker Compose
- JUnit 5
- Testcontainers
- RestTestClient

## Architecture

This project implements Clean Architecture to maintain a separation of concerns and ensure testability and flexibility. The business logic is isolated from external frameworks and drivers, allowing the system to evolve without affecting the core domain.

- Domain: Core entities and business rules.
- Application: Use cases and input/output ports.
- Infrastructure: Persistence implementations and external service adapters.
- Interfaces: Web controllers and API definitions.

The ports and adapters pattern allows the application to remain agnostic of the underlying persistence layer, facilitating easier testing and potential migrations between different storage technologies.

## Quickstart

Prerequisites: Java 25, Docker, and Maven 3.9+ installed.

```bash
git clone git@github.com:emota1981/gimmegift-auth.git
cd gimmegift-auth
./mvnw spring-boot:run
```

The Firestore Emulator is provided via docker-compose for local development and testing.

## Documentation

Detailed technical information can be found in the /docs folder:

- docs/architecture.md: Comprehensive architectural overview and design decisions.
- docs/agents/: Guidelines and resources for AI-assisted development.

## Contributing

We welcome contributions that follow our established development standards:

- All code, identifiers, branches, commits, and logs must be in English.
- No code comments are allowed; use self-explanatory naming and document logic in /docs.
- Follow Conventional Commits (feat, fix, chore, test, docs, refactor).
- Signed commits are required for all contributions.

Please refer to AGENTS.md and .junie/guidelines.md for further details on our contribution workflow.

## License

Apache License 2.0. See LICENSE.
