## Project Context
- Name: gimmegift-auth
- Purpose: Authentication backend service for the gimmegift platform
- Stack: Java 25 LTS (Temurin), Spring Boot 4.0.6, Spring Security 7, Spring Cloud GCP, Maven 3.9
- Persistence: Firestore (Native mode) accessed through ports and adapters
- Architecture: Clean Architecture (Domain, Application, Infrastructure, Interfaces)
- Target Audience: Users of the gimmegift platform requiring secure and reliable authentication.
- Development Focus: Scalability, maintainability, and security through rigorous architectural standards.

## Code Conventions (Non-Negotiable)
- Rule #1: All code in English. Class names, method names, variable names, package names, file names, branch names, commit messages, log messages, exception messages, configuration keys, Firestore collection names, REST endpoints, JSON payload keys, enum values. Everything. No exceptions.
- Rule #2: No code comments. Code must be self-explanatory through expressive naming. If a piece of code seems to need a comment, refactor it or extract a method. Documentation belongs in /docs/*.md files. Allowed exceptions: @SuppressWarnings, @Deprecated, TODO/FIXME marking real tracked technical debt, legal or regulatory notices when mandatory.
- Naming: Classes PascalCase. Methods and variables camelCase. Constants UPPER_SNAKE_CASE. Packages lowercase.dotted. Files named after the public class.
- No Lombok. Java 25 records and explicit accessors are preferred.
- No wildcard imports. No star imports.

## Architecture
- Strict Clean Architecture layering. Domain depends on nothing. Application depends only on Domain. Infrastructure implements ports defined in Domain. Interfaces (REST controllers, DTOs, exception handlers) depend on Application use cases.
- Persistence is accessed through ports (Java interfaces) declared in the Domain layer. Adapters live in the Infrastructure layer. Swapping Firestore for another store must require changes only in Infrastructure.
- ArchUnit tests will enforce these dependency rules in a later phase.
- Core business logic remains pure and isolated from technical details like web frameworks or database drivers.
- Use cases in the Application layer orchestrate the flow of data to and from the Domain layer.
- Domain layer contains entities, value objects, and domain services that encapsulate the core business logic.
- Application layer handles execution of use cases, coordinating domain objects and infrastructure ports.
- Infrastructure layer provides implementations for persistence, messaging, and other external services.
- Interfaces layer exposes the application through RESTful endpoints and handles DTO mapping.

## Persistence
- Firestore is the primary store. Local development uses the Firestore Emulator via Docker Compose or Testcontainers.
- Domain entities and value objects must never reference Firestore SDK types directly.
- The Hybrid Pragmatic approach applies: Firestore today, swappable adapters tomorrow.
- Data mapping between Domain entities and Firestore documents happens in the Infrastructure layer.
- Ensure that Firestore indexes are properly managed and not hardcoded in the application logic.

## Testing
- JUnit 5 with AssertJ and Mockito.
- Domain layer: pure unit tests, zero Spring context.
- Application layer: unit tests with fakes or stubs of ports.
- Infrastructure layer: integration tests against Firestore Emulator via Testcontainers.
- Interface layer: RestTestClient (new in Spring Boot 4.0) plus Spring Security Test.
- Minimum 80 percent coverage enforced via JaCoCo in CI.
- Tests should be expressive and follow the Given-When-Then pattern.
- Avoid mocking everything; use real objects for Domain and Value Objects.

## Security
- Spring Security 7 with stateless session, JWT bearer tokens.
- Passwords hashed with BCrypt (cost 12) or Argon2 (configurable).
- Secrets must never be committed. They live in environment variables or Google Secret Manager.
- Default-deny: all endpoints require authentication except /actuator/health and explicitly public auth endpoints.
- Rate limiting on sensitive endpoints via Bucket4j.
- Secure headers and CSRF protection must be configured according to modern best practices.
- Input validation is mandatory for all public endpoints to prevent common vulnerabilities.
- JWT tokens must have a limited lifespan and support refresh token rotation for enhanced security.
- All network communication should be encrypted using TLS 1.3 or higher where applicable.
- Regular security audits and dependency vulnerability scanning are part of the development lifecycle.

## Git Workflow
- Conventional Commits in English: feat, fix, chore, docs, test, refactor, perf, ci, build, style.
- Feature branches only. Direct pushes to main are blocked by branch protection and a local pre-push hook.
- All commits must be signed (SSH signing enforced by branch protection on main).
- One PR per feature, self-reviewed, squash-merged with branch deletion.
- Branch names should follow the pattern: feature/feature-name or fix/bug-name.
- PR descriptions should clearly explain the changes and the reasoning behind them.

## AI Agent Behavior
- Show a diff before applying any file change. Wait for explicit approval.
- Never overwrite uncommitted changes without confirmation.
- Respect the .gitignore at all times. Never propose committing files inside ignored paths.
- When creating new code, place it in the correct Clean Architecture layer.
- When unsure about a domain decision, ask a clarifying question instead of guessing.
- Never invent dependency versions. Verify against Maven Central or official documentation.
- When operating autonomously across multiple steps, give a brief progress confirmation after each step.
- Always check the project structure and existing patterns before proposing new code.

## Forbidden
- Code comments (see Rule #2).
- Lombok and any annotation processor that hides logic from the reader.
- Hardcoded credentials, API keys, JWT secrets, database URLs with passwords.
- Direct push to main.
- Mixing concerns across Clean Architecture layers (e.g., REST annotations in Domain, JPA annotations anywhere since we use Firestore).
- Generating @Configuration classes outside Infrastructure or Interfaces layers.
- Wildcard imports.
- Using deprecated libraries or frameworks without a strong justification.

## Documentation
- All documentation in /docs/ in English.
- Use Markdown. No emojis. No badges. No HTML tags. No images unless strictly necessary for architecture diagrams.
- Architecture diagrams as ASCII art or Mermaid.
- Documentation should be kept up to date with the latest architectural decisions.
- README.md at the root should provide a high-level overview and quickstart instructions.

## Verification and Compliance
- Every AI agent must verify their output against these rules before submission.
- Compliance with Rule #1 and Rule #2 is mandatory and non-negotiable.
- The use of Maven for dependency management and build processes is required.
- The project must always be buildable with Java 25 and Maven 3.9+.
- Security best practices must be applied at every level of the application.
- The Clean Architecture boundaries must be strictly respected.
- Any deviation from these guidelines must be discussed and approved by the project maintainers.
- Automated tools may be used to verify compliance with these rules in the future.
- Continuous Integration pipelines will enforce many of these rules automatically.
- Every contribution, whether by human or agent, is expected to maintain these high standards.
