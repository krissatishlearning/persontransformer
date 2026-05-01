# review_standards.md

## Java 17 Idioms
- Use `var` for local variables where type is obvious (already used in `PersonUpsertService`); flag verbose explicit types where `var` improves readability
- Prefer `Optional` methods (`.isEmpty()`, `.isPresent()`, `.ifPresent()`) — never call `.get()` without a prior presence check
- Use `Collectors.toList()` (current usage); prefer `Stream.toList()` (Java 16+) in new code
- Flag raw `String` use for enum-like values (e.g., `action = "INSERT"/"UPDATE"`) — introduce an enum instead

## Spring Boot Conventions
- `@Transactional` on `PersonUpsertService.upsert` spans both MongoDB and PostgreSQL, but JPA transactions do not cover MongoDB — flag any assumption of atomicity across both stores
- Services must be annotated `@Service`; consumers `@Component`; repositories `@Repository` — enforce consistently
- Constructor injection only; flag `@Autowired` field injection
- Kafka listener methods should handle deserialization errors gracefully; flag missing `errorHandler` or `@KafkaListener` DLT configuration

## Lombok
- Lombok is available — flag manually written getters/setters/constructors on POJOs (`Address`, `Phone`, `AddressDTO`, `PhoneDTO`, `PersonEvent`, `ProcessedEvent`) as candidates for `@Data`/`@Builder`/`@AllArgsConstructor`/`@NoArgsConstructor`
- Domain entities with Lombok `@Data` should use `@EqualsAndHashCode` carefully on JPA entities

## Testing Expectations
- Framework: JUnit 5 + AssertJ (`assertThat(...)`) — no JUnit 4 or Hamcrest
- Unit tests must cover: transformer logic (null inputs, field merging, normalization), service branch logic (INSERT vs UPDATE path), consumer null-payload handling
- Flag any new service/transformer method without a corresponding test
- Integration tests for Kafka consumers should use `@EmbeddedKafka` (spring-kafka-test is present)
- Avoid reflection-based assertions in tests (as seen in existing tests) — prefer direct behavioral assertions

## Naming & Structure
- DTOs in `dto` package, domain in `domain`, repositories in `repository`, audit in `audit`
- Test classes mirror source package structure
- Constants like `"INSERT"/"UPDATE"` should be `static final` or enums, not inline string literals

## Common Pitfalls to Flag
- Missing null-check on `externalId` before MongoDB lookup
- `@Transactional` providing false atomicity guarantee across MongoDB + PostgreSQL
- `Collectors.toList()` returning mutable list — prefer `Stream.toList()` for immutability
- Audit record saved even if MongoDB operation fails silently
- `Instant.now()` called inside transformer — flag for testability (inject `Clock`)
