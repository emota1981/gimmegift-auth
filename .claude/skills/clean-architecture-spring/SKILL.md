# Clean Architecture for Spring Boot in gimmegift-auth

## When to use this skill
Apply this skill whenever generating, refactoring, or reviewing Java code that creates classes, ports, use cases, controllers, adapters, or tests in the gimmegift-auth project. This skill is the source of truth for how Clean Architecture is materialized here.

## Layer responsibilities
- domain: pure business logic. Entities, value objects, domain services, ports (interfaces), domain exceptions. Zero framework imports. Zero Spring annotations. Zero persistence library imports.
- application: orchestration. Use cases (one class per use case), command and query DTOs, ports usage. Depends on domain only. May use jakarta.transaction or framework-agnostic patterns. No Spring annotations except possibly @Service on the use case class itself.
- infrastructure: adapters that implement domain ports. Firestore repositories, JWT issuer, password hashers, email senders, clock providers. Depends on domain (to know the port contracts) and may depend on Spring, Spring Cloud GCP, JJWT, etc.
- interfaces: REST controllers, request/response DTOs, exception handlers (@ControllerAdvice), springdoc configuration. Depends on application use cases. Never depends on domain entities directly.

## Package structure
Base package: br.com.gimmegift.auth
- br.com.gimmegift.auth.domain
  - .user (User, UserId, Email, Password)
  - .credential (Credential, RefreshToken)
  - .verification (EmailVerificationToken, PasswordResetToken)
  - .port (UserRepository, RefreshTokenRepository, PasswordHasher, TokenIssuer, Clock, EmailSender)
  - .exception (DomainException and subclasses)
- br.com.gimmegift.auth.application
  - .signup (SignUpUseCase, SignUpCommand)
  - .login (LoginUseCase, LoginCommand)
  - .refresh (RefreshTokenUseCase, RefreshTokenCommand)
  - .password (RequestPasswordResetUseCase, ConfirmPasswordResetUseCase)
  - .email (RequestEmailVerificationUseCase, ConfirmEmailUseCase)
  - .logout (LogoutUseCase)
- br.com.gimmegift.auth.infrastructure
  - .persistence.firestore (FirestoreUserRepository, FirestoreRefreshTokenRepository, document classes)
  - .security (JwtTokenIssuer, BCryptPasswordHasher, SecurityFilterChain)
  - .email (SmtpEmailSender or similar)
  - .clock (SystemClock)
- br.com.gimmegift.auth.interfaces
  - .rest (AuthController, ProblemDetailsAdvice)
  - .rest.dto (SignUpRequest, LoginRequest, TokenResponse, etc.)
  - .openapi (OpenApiConfig)

## Use case pattern
Each use case is a single class with a single public method. Example shape:
- public final class SignUpUseCase
- private final UserRepository users (port from domain)
- private final PasswordHasher hasher (port from domain)
- private final Clock clock (port from domain)
- public TokenResponse execute(SignUpCommand command)
- All dependencies injected via constructor.
- No Spring imports inside the use case if it can be avoided. The class can be annotated @Service ONLY if Spring constructor injection is used; even then, no Spring annotations on parameters.

## Port pattern
Ports are interfaces in the domain layer.
- Naming: noun-based for repositories (UserRepository), action-based for utilities (PasswordHasher, TokenIssuer, EmailSender).
- Methods return domain types only. No Optional<DocumentReference>, no Mono, no Flux unless we explicitly choose Reactor as a domain primitive (we are not).
- Methods throw domain exceptions only.

## Adapter pattern
Adapters live in infrastructure and implement domain ports.
- Class name suffixed with the technology: FirestoreUserRepository, JwtTokenIssuer, BCryptPasswordHasher.
- Adapter classes are annotated @Component or @Repository.
- Never expose adapter-specific types in method signatures.

## REST controller pattern
Controllers in interfaces.rest depend on use case classes from application.
- Naming: AuthController for grouped endpoints.
- Map domain exceptions to RFC 7807 Problem Details via @RestControllerAdvice.
- Validate input with jakarta.validation annotations on DTOs.
- Never accept or return domain entities directly. Always DTO.

## Forbidden patterns
- @Entity, @Table, @Column anywhere (we use Firestore, not JPA).
- Lombok of any kind.
- Field injection (no @Autowired on fields).
- Throwing RuntimeException directly. Always domain exceptions in domain/application layers.
- Returning null from use cases. Use Optional or throw a domain exception.
- Accessing UserRepository directly from a controller. Always go through a use case.

## ArchUnit rules to enforce in tests
- domain may not depend on application, infrastructure, interfaces, or any Spring/Jakarta package.
- application may depend on domain only.
- infrastructure may depend on domain only (not on application or interfaces).
- interfaces may depend on application only (not on domain or infrastructure).

## Quick checklist when generating code
1. Which layer does this belong to?
2. If it crosses layers, is the dependency direction correct (outer to inner only)?
3. Are all public APIs in English? Self-explanatory names? Zero comments?
4. Are domain types leaking into outer layers? Or framework types leaking into inner layers?
5. Did we add a corresponding test in the same package under src/test/java?
