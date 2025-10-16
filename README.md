# Insurance Assignment

## Prerequisites
- Java 21
- Docker
- Docker Compose
- Gradle

## Database Configuration
Connect to the database host on AWS with the following info:
```properties
spring.datasource.url=jdbc:postgresql://<host>:5432/ifsw-db
spring.datasource.username=ifsw-user
spring.datasource.password=<password>
```

## API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI**: http://localhost:8080/v3/api-docs

## Available APIs
- `GET /api/v1/vehicles/vehicle-id` - Get vehicle information

## Docker Setup

### Using Docker Compose (Recommended)

1. **Start all services**:
   ```bash
   ./gradlew dockerComposeUp
   ```
   Or manually:
   ```bash
   docker-compose up -d
   ```

2. **View logs**:
   ```bash
   ./gradlew dockerComposeLogs
   # Or
   docker-compose logs -f
   ```

3. **Stop services**:
   ```bash
   ./gradlew dockerComposeDown
   # Or
   docker-compose down
   ```

### Manual Docker Commands

1. **Build the image**:
   ```bash
   docker build -t insurance-app .
   ```

2. **Run the container**:
   ```bash
   docker run -d -p 8080:8080 --name insurance-container insurance-app
   ```

3. **View logs**:
   ```bash
   docker logs -f insurance-container
   ```

4. **Stop the container**:
   ```bash
   docker stop insurance-container
   ```

5. **Start the container**:
   ```bash
   docker start insurance-container
   ```

## Environment Variables
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  insurance-app
```

## Testing

### Run Unit Tests (Excludes Blackbox Tests)
```bash
./gradlew test
```

### Run Only Blackbox Tests
```bash
./gradlew blackboxTest
```

### Run All Tests (Unit + Blackbox)
```bash
./gradlew allTests
```

### Build Without Tests
```bash
./gradlew build -x test
```

## Gradle Tasks

### Docker Tasks
- `dockerComposeUp` - Start all Docker containers
- `dockerComposeDown` - Stop and remove all containers
- `dockerComposeLogs` - Show container logs

### Test Tasks
- `test` - Run unit tests (excludes blackbox tests)
- `blackboxTest` - Run only blackbox tests
- `allTests` - Run all tests (unit + blackbox)
