# ms-barriodigital-audit

Microservicio de auditoría: consume los eventos de trámites desde **Kafka**
(publicados por `ms-barriodigital-requests`) y guarda un historial inmutable en
Oracle, consultable por el rol Auditor a través del BFF.

## Cómo correr local

Requiere Java 17, Maven, Oracle y Kafka accesibles.

```bash
./mvnw spring-boot:run
```

Levanta en `http://localhost:8083`.

Tests: `./mvnw verify` — corren contra H2 en memoria, con el listener de Kafka
desactivado (`barriodigital.kafka.listener-auto-startup: false` en
`application-test.yml`).

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_SERVICE` | `localhost` / `1521` / `XEPDB1` | Conexión a Oracle |
| `DB_USER` | `barriodigital_audit` | Usuario Oracle dedicado a este servicio |
| `DB_PASSWORD` | — | Password de ese usuario |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Brokers Kafka (coma-separado si son varios) |
| `AAD_ISSUER_URI` | — | `https://login.microsoftonline.com/<TENANT_ID>/v2.0` |
| `AAD_API_CLIENT_ID` | — | Client ID del App Registration `barriodigital-api` |
| `AAD_REQUIRED_SCOPE` | `access_as_user` | Scope exigido en el token |

Las migraciones de esquema están en `src/main/resources/db/migration` (Flyway).

## Docker

```bash
docker build -t ms-barriodigital-audit .
docker run -p 8083:8083 --env-file .env ms-barriodigital-audit
```

Imagen publicada automáticamente en cada push a `main`:
`ghcr.io/phamnukz/ms-barriodigital-audit:latest`.
