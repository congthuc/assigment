# Copilot instructions for this repository

## Build and test commands

Use the Gradle wrapper for all project tasks: `./gradlew` on Unix-like shells, `.\gradlew.bat` on Windows.

- Build the app: `./gradlew build`
- Build without tests: `./gradlew clean build -x test`
- Run the default test suite: `./gradlew test`
  - `build.gradle` excludes `**/blackbox/**` from the standard `test` task.
- Run only blackbox tests: `./gradlew blackboxTest`
- Run all tests: `./gradlew allTests`
- Run one unit/component test class: `./gradlew test --tests com.sw.insurance.unit.VehicleServiceTest`
- Run one blackbox test class: `./gradlew blackboxTest --tests com.sw.insurance.blackbox.VehicleApiBlackboxTest`
- Docker helper tasks from Gradle:
  - `./gradlew dockerComposeUp`
  - `./gradlew dockerComposeDown`
  - `./gradlew dockerComposeLogs`

There is no dedicated lint or formatter task configured in `build.gradle`.

## High-level architecture

This is a Spring Boot 3.5 / Java 21 monolith with a layered package structure under `src/main/java/com/sw/insurance/`.

- `controller/VehicleController.java` exposes `GET /api/v1/vehicles/{registrationNumber}`.
- `service/VehicleService.java` contains the main request orchestration.
- `service/FeatureFlagService.java` wraps LaunchDarkly checks.
- `repository/VehicleRepository.java` is the JPA access point for `Vehicle`.
- `entity/Vehicle.java` maps to `ifsw_schema.vehicles`.
- `dto/VehicleResponse.java` is returned to clients.
- `config/SecurityConfig.java` and `config/LaunchDarklyConfig.java` provide auth and feature-flag wiring.
- Flyway migrations in `src/main/resources/db/migration` define the database schema and sample data.

For the vehicle lookup endpoint, the runtime flow is:

1. `VehicleController` receives the request.
2. `VehicleService` checks LaunchDarkly flag `sw-insurance-car-available`.
3. If the flag is off, the service throws `FeatureNotAvailableException` and returns `403`.
4. If the flag is on, the service loads the entity through `VehicleRepository.findByRegistrationNumber(...)`.
5. Missing data becomes `ResourceNotFoundException` and returns `404`.
6. The service manually maps the entity to `VehicleResponse`.

Security is global: all non-Swagger endpoints require HTTP Basic auth. Swagger/OpenAPI routes are explicitly whitelisted in `SecurityConfig`.

Local feature flags come from `src/main/resources/launchdarkly/ld-flags-local.json`. `LaunchDarklyConfig` is disabled for the `test` profile, while `application-test.properties` switches the datasource to in-memory H2.

## Key conventions

- The codebase is organized by technical layer, not by feature.
- Keep feature-flag checks in the service layer before repository access; `VehicleService` is the pattern to follow.
- API-to-DTO mapping is done manually inside services. There is no mapper library in use.
- Error-to-HTTP mapping is handled with exception classes annotated by `@ResponseStatus`, not a shared global exception handler.
- Tests are split by intent:
  - `unit/` uses plain JUnit 5 + Mockito.
  - `component/` also uses direct Mockito-based controller tests, not Spring MVC/MockMvc.
  - `blackbox/` hits a running app over HTTP on `localhost:8080`, so those tests need the application running and valid Basic auth credentials.
- The repository consistently uses the spelling `assigment` in the project name and Spring application name; preserve existing identifiers unless you are intentionally renaming across the codebase.
- When README content conflicts with source, trust the source files. For example, the real vehicle endpoint is `GET /api/v1/vehicles/{registrationNumber}`.
