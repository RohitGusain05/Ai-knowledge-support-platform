# Knowledge Support Backend

Java 21 + Spring Boot backend for the AI Knowledge & Support Platform.

## Run locally

From this directory:

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

The API starts on port 8080.

## Health endpoint

```
GET /api/v1/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "knowledge-support-backend"
}
```

## Next

PostgreSQL and Flyway migrations will be added in the next foundation milestone.
