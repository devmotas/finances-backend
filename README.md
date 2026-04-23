# Finances Backend

API REST em **Kotlin** com **Spring Boot 4** e **Gradle**.

## Requisitos

- JDK 21
- PostgreSQL (banco `finances` ou o definido na URL)

## Configuração de ambientes

O projeto usa profiles do Spring:

- `dev` (padrão local)
- `prod` (produção)

Arquivos:

- `src/main/resources/application.properties` (configuração comum + profile ativo)
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

Variáveis importantes:

- `SPRING_PROFILES_ACTIVE` (`dev` por padrão)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `APP_JWT_SECRET`, `APP_JWT_EXPIRATION_MS`
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS` (lista separada por vírgula)

## Executar

```bash
./gradlew bootRun
```

Esse comando sobe em `dev` automaticamente (ou usa `SPRING_PROFILES_ACTIVE` se definido).

No Windows: `gradlew.bat bootRun`

## Build

```bash
./gradlew build
```

## Testes

```bash
./gradlew test
```
