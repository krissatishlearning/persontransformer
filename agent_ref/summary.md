# agent_ref/summary.md

## Project Overview
A Spring Boot Kafka consumer that reads `PersonEvent` messages from Kafka topics, upserts transformed person records into MongoDB, and records audit entries in PostgreSQL.

## Architecture

| Package | Responsibility |
|---|---|
| `kafka` | Kafka listener (`PersonEventConsumer`) consuming from two configurable topics |
| `service` | `PersonUpsertService` — orchestrates MongoDB upsert + PostgreSQL audit write |
| `transform` | `PersonTransformer` — maps DTOs to domain objects, normalizes/trims fields |
| `domain` | MongoDB documents: `Person`, `Address`, `Phone` |
| `dto` | Kafka payload POJOs: `PersonEvent`, `AddressDTO`, `PhoneDTO` |
| `repository` | `PersonMongoRepository` (Spring Data MongoDB) |
| `audit` | `ProcessedEvent` JPA entity + `ProcessedEventRepository` (Spring Data JPA / PostgreSQL) |

## Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.2.5 (Data MongoDB, Data JPA, Kafka, Validation)
- **Databases:** MongoDB (documents), PostgreSQL (audit)
- **Messaging:** Apache Kafka via `spring-kafka`
- **Build:** Maven (`spring-boot-maven-plugin`)
- **Testing:** JUnit 5, AssertJ, `spring-boot-starter-test`, `spring-kafka-test`
- **Lombok** is on the classpath but **not actively used** — all classes use explicit getters/setters and constructors

## Conventions
- **No Lombok annotations** in existing code; write explicit constructors, getters, and setters
- **Constructor injection** used throughout — do not use field injection
- **Always write unit tests** alongside new service/transformer logic
- Email normalization: always `trim().toLowerCase()`
- Null-safe merge: incoming non-null value wins; otherwise keep existing value
- DTOs annotate with `@JsonIgnoreProperties(ignoreUnknown = true)`
