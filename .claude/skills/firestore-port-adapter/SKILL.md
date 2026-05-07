# Firestore Port Adapter in gimmegift-auth

## When to use this skill
Apply this skill whenever generating or modifying any class in the br.com.gimmegift.auth.infrastructure.persistence.firestore package, or whenever defining a new persistence port in br.com.gimmegift.auth.domain.port.

## Core principle
The Domain layer never imports Firestore SDK types. Adapters in infrastructure translate between Domain entities and Firestore documents.

## Document mapping
- Each Firestore collection corresponds to a single Domain entity.
- Document IDs are the Domain entity IDs as strings (UserId.value(), RefreshTokenId.value()).
- Document fields use camelCase matching Domain field names.
- Nested value objects (e.g., Email) are flattened into primitive fields when possible (e.g., user.emailValue, user.emailVerifiedAt).
- Timestamps stored as com.google.cloud.Timestamp; the adapter converts to/from java.time.Instant.

## Collection naming
- One collection per aggregate root.
- snake_case for collection names: users, refresh_tokens, password_reset_tokens, email_verification_tokens.
- This is the only place snake_case is allowed; Java code remains camelCase.

## Adapter class pattern
- Class implements the domain port (e.g., FirestoreUserRepository implements UserRepository).
- Annotated @Repository.
- Constructor injection of com.google.cloud.firestore.Firestore.
- Private helper methods toDocument(User) and fromDocument(DocumentSnapshot) for mapping.
- Never expose Firestore types in public method signatures.

## Local development with the Firestore Emulator
- The emulator is started by docker-compose at startup (compose.yaml will be filled in a later PR).
- Spring Cloud GCP autoconfigures the Firestore client to honor the FIRESTORE_EMULATOR_HOST environment variable.
- For local runs without the emulator, set spring.cloud.gcp.firestore.emulator.enabled=true and spring.cloud.gcp.firestore.host-port=localhost:8080.

## Testcontainers pattern (used in tests)
- Use FirestoreEmulatorContainer from org.testcontainers:testcontainers-gcloud.
- Start the container with @Container in TestcontainersConfiguration.
- Bind the emulator host-port to spring.cloud.gcp.firestore.host-port via @DynamicPropertySource.
- Each integration test class extends a base or imports TestcontainersConfiguration so the container is shared across tests.

## Transactions
- Firestore transactions are limited (max 500 docs per transaction, retries on contention).
- Use Firestore.runTransaction(...) for multi-document atomic writes.
- For auth flows, transactions are typically NOT needed: signup writes one user document; refresh rotation writes one refresh token. Avoid premature use of transactions.

## Eventual consistency caveats
- Single-document reads are strongly consistent.
- Queries (where, orderBy) can be eventually consistent in some scenarios. For auth-critical reads (login, refresh validation), always read by document ID.

## Forbidden patterns
- Firestore SDK imports outside infrastructure.persistence.firestore package.
- Returning DocumentSnapshot or DocumentReference from any method whose contract is a domain port.
- Hardcoding collection names in multiple places. Use a constants class FirestoreCollections in infrastructure.persistence.firestore.
- Using camelCase for collection names (must be snake_case).
- Storing passwords or any secret in Firestore as plain text.

## Quick checklist when generating a new adapter
1. Does the class implement a domain port? Yes is mandatory.
2. Are all public method signatures using domain types only? No Firestore types?
3. Is mapping isolated in toDocument/fromDocument helpers?
4. Is the collection name in FirestoreCollections (constants)?
5. Is the test class using FirestoreEmulatorContainer?
