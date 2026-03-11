# Person Transformer

Sample Java backend that consumes person events from Kafka, transforms them, and upserts into MongoDB. Processed events are audited in PostgreSQL.

## Stack

- **Java 17**, **Maven**
- **Spring Boot 3.2**
- **Kafka** – event source
- **MongoDB** – main store (person documents; insert if new, update if exists by `externalId`)
- **PostgreSQL** – audit log of processed events (topic, partition, offset, action)

## Flow

1. Consume messages from Kafka topic `person-events`.
2. Transform payload: normalize email (lowercase, trim), trim strings.
3. Look up person in MongoDB by `externalId`.
4. If not found → insert new document.
5. If found → update existing document.
6. Write an audit row to PostgreSQL (INSERT/UPDATE, timestamps, Kafka metadata).

## Prerequisites

- JDK 17+
- Maven 3.6+
- Kafka (e.g. local or Docker)
- MongoDB
- PostgreSQL

## Configuration

Edit `src/main/resources/application.yml`:

- **Kafka**: `spring.kafka.bootstrap-servers`, `app.kafka.topic`
- **MongoDB**: `spring.data.mongodb.*`
- **PostgreSQL**: `spring.datasource.*`

Default topic: `person-events`.

## Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

## Kafka message format

JSON payload (example):

```json
{
  "externalId": "ext-001",
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "Jane.Doe@Example.COM"
}
```

Produce test message (Kafka CLI):

```bash
kafka-console-producer --broker-list localhost:9092 --topic person-events
# Then paste: {"externalId":"ext-001","firstName":"Jane","lastName":"Doe","email":"jane@example.com"}
```

## Project layout

- `domain/` – MongoDB `Person` entity
- `dto/` – Kafka `PersonEvent` DTO
- `transform/` – transformation (normalize, trim)
- `repository/` – MongoDB repository
- `audit/` – PostgreSQL entity and repository
- `service/` – upsert logic (Mongo + audit)
- `kafka/` – Kafka listener

## License

Sample project for testing; use as needed.
