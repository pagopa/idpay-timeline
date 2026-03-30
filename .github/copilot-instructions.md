# Copilot Instructions for `idpay-timeline`

## Project context

- Java 25, Maven Wrapper, Spring Boot 4.x.
- Main application class: `it.gov.pagopa.timeline.TimelineApplication`.
- Runtime stack: Spring MVC, Spring Data MongoDB, Spring Cloud Stream Kafka, Actuator, validation, AspectJ-based retry aspects.
- Main configuration files: `pom.xml`, `src/main/resources/application.yml`, `src/main/resources/logback-spring.xml`.
- Container assets: `Dockerfile` for JVM images and `Dockerfile.native` for GraalVM native images.

## Working rules

- Preserve business behavior, public HTTP APIs, MongoDB persistence semantics, Kafka message contracts, and health endpoints.
- Prefer small, surgical changes over broad refactors.
- Reuse existing patterns and naming conventions in `it.gov.pagopa.*`.
- Do not introduce new frameworks or architectural layers unless they are strictly required for compatibility.
- When native-image compatibility requires hints or configuration, prefer Spring AOT/runtime hints over ad-hoc workarounds.

## Code conventions

- Keep classes and methods focused; extract helpers instead of increasing complexity.
- Use constructor injection for Spring components.
- Keep Lombok usage consistent with surrounding code.
- Avoid broad `catch (Exception)` blocks and do not swallow errors.
- Use existing exception handling patterns in `common.web.exception` and `timeline.exception`.
- Keep logging structured and concise; never log sensitive values.

## Data and integration safeguards

- Do not change JSON field names, request/response shapes, MongoDB collection names, indexes, or Kafka binding names unless explicitly requested.
- Keep `spring.cloud.function.definition`, binder names, and topic-related properties aligned with `application.yml`.
- Preserve Mongo repository behavior, including custom repository implementations and retry aspects.

## Validation workflow

- Use the Maven Wrapper for builds: `./mvnw`.
- Before finalizing code changes, run the relevant existing checks, typically `./mvnw test` and any needed package/native build commands.
- If adding native-image support, validate both the standard JVM build and the native compilation path.

## Native-image guidance

- Target GraalVM Community / Native Build Tools compatible with Spring Boot 4 and Java 25.
- Prefer dedicated native configuration in `pom.xml` and supported Spring Boot / GraalVM mechanisms.
- Keep the standard JVM packaging path working unless the task explicitly replaces it.
