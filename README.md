# OpenTelemetry CRUD API

[![CI/CD Pipeline](https://github.com/devops-thiago/otel-example-java/actions/workflows/ci.yml/badge.svg)](https://github.com/devops-thiago/otel-example-java/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/devops-thiago/otel-example-java/branch/main/graph/badge.svg)](https://codecov.io/gh/devops-thiago/otel-example-java)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=devops-thiago_otel-example-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=devops-thiago_otel-example-java)
[![Dependabot Status](https://img.shields.io/badge/Dependabot-enabled-brightgreen.svg)](https://github.com/devops-thiago/otel-example-java/network/dependencies)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://openjdk.org/projects/jdk/24/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://hub.docker.com/)
[![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-2.18.1-blueviolet.svg)](https://opentelemetry.io/)
[![JaCoCo](https://img.shields.io/badge/JaCoCo-0.8.13-brightgreen.svg)](https://www.jacoco.org/jacoco/)

A comprehensive Java Spring Boot application demonstrating a complete CRUD API with OpenTelemetry integration and full observability stack for distributed tracing, metrics, and log aggregation.

## Features

- **CRUD Operations**: Complete Create, Read, Update, Delete functionality for User entities
- **OpenTelemetry Integration**: Distributed tracing with manual and automatic instrumentation
- **Full Observability Stack**: Grafana Alloy, Tempo, Mimir, Loki, MinIO, and Grafana
- **Spring Boot 3.4.5**: Latest Spring Boot features and optimizations
- **Virtual Threads**: Enabled for high-performance concurrent request handling
- **H2 Database**: In-memory database for easy testing and development
- **Input Validation**: Comprehensive validation using Bean Validation
- **Error Handling**: Robust error handling with proper HTTP status codes
- **Docker Support**: Complete containerization with multi-stage builds
- **Health Checks**: Actuator endpoints for monitoring
- **Maven**: Standard Maven project structure

## Technologies Used

- **Java 24**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **H2 Database**
- **OpenTelemetry 2.18.1**
- **JaCoCo 0.8.13**
- **Maven**
- **Bean Validation**
- **Spring Boot Actuator**
- **Docker & Docker Compose**
- **Grafana Stack**: Alloy, Tempo, Mimir, Loki, Grafana

## Architecture Overview

### Basic Application
```
Client → Spring Boot App → H2 Database
```

### Full Observability Stack
```
Spring Boot App → Alloy (Collector) → Tempo (Traces) + Mimir (Metrics) + Loki (Logs) → Grafana (UI)
                                   ↓
                                 MinIO (Object Storage)
```

### Components

| Component | Purpose | Port | UI |
|-----------|---------|------|-----|
| **Spring Boot App** | Main application | 8080 | http://localhost:8080 |
| **Grafana Alloy** | Telemetry collector | 4320 (gRPC), 4321 (HTTP) | http://localhost:12345 |
| **Tempo** | Distributed tracing | 3200, 4317, 4318 | - |
| **Mimir** | Metrics storage | 9009 | - |
| **Loki** | Log aggregation | 3100 | - |
| **MinIO** | Object storage | 9000 | http://localhost:9001 |
| **Grafana** | Visualization | 3000 | http://localhost:3000 |

## Project Structure

```
src/
├── main/
│   ├── java/com/example/otelcrudapi/
│   │   ├── OtelCrudApiApplication.java      # Main application class
│   │   ├── config/
│   │   │   ├── OpenTelemetryConfig.java     # OpenTelemetry configuration
│   │   │   └── VirtualThreadsConfig.java    # Virtual threads configuration
│   │   ├── controller/
│   │   │   └── UserController.java          # REST controller
│   │   ├── model/
│   │   │   └── User.java                    # User entity
│   │   ├── repository/
│   │   │   └── UserRepository.java          # Data access layer
│   │   └── service/
│   │       └── UserService.java             # Business logic layer
│   └── resources/
│       ├── application.properties           # Default configuration
│       ├── application-docker.properties    # Docker-specific configuration
│       └── data.sql                         # Sample data
├── test/
│   └── java/                                # Test classes
├── config/                                  # Observability configurations
│   ├── alloy.alloy                         # Alloy configuration
│   ├── tempo.yaml                          # Tempo configuration
│   ├── mimir.yaml                          # Mimir configuration
│   ├── loki.yaml                           # Loki configuration
│   └── grafana/                            # Grafana configurations
├── docker-compose.yml                      # Full stack deployment
└── Dockerfile                              # Application container
```

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6+
- Docker and Docker Compose (for full observability stack)

### Option 1: Quick Start (Basic Application)

1. **Clone the repository**
   ```bash
   git clone https://github.com/devops-thiago/otel-example-java.git
   cd otel-example-java
   ```

2. **Build and run the application**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8080/api/users
   - H2 Console: http://localhost:8080/h2-console
   - Health Check: http://localhost:8080/actuator/health

### Option 2: Full Observability Stack

1. **Start the complete stack**
   ```bash
   docker-compose up -d
   ```

2. **Access the services**
   - **Application**: http://localhost:8080
   - **Grafana**: http://localhost:3000 (admin/admin)
   - **Alloy**: http://localhost:12345
   - **MinIO**: http://localhost:9001 (admin/password123)

3. **Generate some data**
   ```bash
   # Create test data
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"name": "Test User", "email": "test@example.com", "bio": "Test user"}'

   # Make some requests to generate traces
   curl http://localhost:8080/api/users
   curl http://localhost:8080/api/users/1
   ```
### Database Access

The application uses H2 in-memory database with the following credentials:
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## API Documentation

### Base URL
`http://localhost:8080/api/users`

### Endpoints

#### 1. Get All Users
- **Method**: GET
- **URL**: `/api/users`
- **Response**: Array of User objects

#### 2. Get User by ID
- **Method**: GET
- **URL**: `/api/users/{id}`
- **Response**: User object or 404 Not Found

#### 3. Get User by Email
- **Method**: GET
- **URL**: `/api/users/email/{email}`
- **Response**: User object or 404 Not Found

#### 4. Create User
- **Method**: POST
- **URL**: `/api/users`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "bio": "Software Engineer"
  }
  ```
- **Response**: Created User object (201 Created)

#### 5. Update User
- **Method**: PUT
- **URL**: `/api/users/{id}`
- **Request Body**:
  ```json
  {
    "name": "John Updated",
    "email": "john.updated@example.com",
    "bio": "Senior Software Engineer"
  }
  ```
- **Response**: Updated User object

#### 6. Delete User
- **Method**: DELETE
- **URL**: `/api/users/{id}`
- **Response**: 204 No Content

#### 7. Search Users by Name
- **Method**: GET
- **URL**: `/api/users/search?name={name}`
- **Response**: Array of matching User objects

#### 8. Get Recent Users
- **Method**: GET
- **URL**: `/api/users/recent?days={days}`
- **Response**: Array of User objects created within the specified days

#### 9. Health Check
- **Method**: GET
- **URL**: `/api/users/health`
- **Response**: Service health status

#### 10. Thread Information
- **Method**: GET
- **URL**: `/api/users/thread-info`
- **Response**: Information about the current thread (demonstrates virtual threads)

### User Object Structure

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "bio": "Software Engineer",
  "createdAt": "2023-12-01T10:30:00",
  "updatedAt": "2023-12-01T10:30:00"
}
```

## Observability and Monitoring

### OpenTelemetry Configuration

The application supports multiple OpenTelemetry exporters:

**For Development (logs only)**:
```properties
otel.traces.exporter=logging
otel.metrics.exporter=none
```

**For Full Observability Stack**:
```properties
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
otel.exporter.otlp.endpoint=http://localhost:4320
```

### Using Grafana for Monitoring

#### Data Sources (Pre-configured)
1. **Tempo** - Distributed tracing
2. **Mimir** - Metrics (Prometheus-compatible)
3. **Loki** - Logs

#### Key Metrics to Monitor

1. **HTTP Request Rate**: `rate(http_requests_total[5m])`
2. **Error Rate**: `rate(http_requests_total{status=~"5.."}[5m])`
3. **Response Time**: `histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))`
4. **Database Queries**: `rate(database_queries_total[5m])`

#### Example Queries

**Traces (Tempo)**:
```
{service.name="otel-crud-api"}
```

**Metrics (Mimir)**:
```
rate(http_requests_total[5m])
```

**Logs (Loki)**:
```
{job="otel-crud-api"}
```

### Trace Analysis Features

1. **Service Map**: View service dependencies
2. **Span Analysis**: Examine individual operations
3. **Performance**: Identify slow operations
4. **Error Analysis**: Debug failed requests
5. **Correlation**: Link traces to metrics and logs

### Custom Instrumentation

The service layer includes custom span attributes:
- `user.count`: Number of users returned
- `user.id`: User ID being processed
- `user.email`: User email being processed
- `user.found`: Whether user was found
- `search.query`: Search query used
- `search.results`: Number of search results

## Virtual Threads Configuration

Virtual threads are enabled for improved concurrency performance.

### Benefits
- **High Concurrency**: Handle thousands of concurrent requests
- **Reduced Memory Footprint**: Lightweight threads
- **Improved Scalability**: Better performance under load
- **No Code Changes**: Existing code works without modification

### Configuration
```properties
spring.threads.virtual.enabled=true
server.tomcat.threads.max=200
```

### Testing Virtual Threads
```bash
curl http://localhost:8080/api/users/thread-info
```

## Development and Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run with coverage
mvn test jacoco:report
```

### Building and Deployment

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/otel-crud-api-1.0.0.jar

# Build Docker image
docker build -t otel-crud-api .

# Run with Docker
docker run -p 8080:8080 otel-crud-api
```

### Testing the API

#### Using curl

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Test User", "email": "test@example.com", "bio": "Test user"}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated User", "email": "updated@example.com", "bio": "Updated"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1

# Search users
curl "http://localhost:8080/api/users/search?name=John"
```

### Actuator Endpoints

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

## Troubleshooting

### Common Application Issues

1. **Port already in use**: Change `server.port` in `application.properties`
2. **Database connection issues**: Verify H2 configuration
3. **OpenTelemetry not working**: Check exporter configuration
4. **Validation errors**: Ensure request body matches User model

### Common Observability Issues

1. **Services not starting**:
   ```bash
   docker-compose logs [service-name]
   docker-compose restart [service-name]
   ```

2. **No data in Grafana**:
   - Check if application is sending data to Alloy
   - Verify Alloy is forwarding to backends
   - Check service connectivity

3. **MinIO connection issues**:
   ```bash
   curl http://localhost:9000/minio/health/live
   docker-compose restart minio-setup
   ```

### Debug Commands

```bash
# Check if application is sending traces
curl -s http://localhost:4320/debug/tracez

# Check Alloy metrics
curl -s http://localhost:12345/metrics

# Check service health
curl -s http://localhost:3200/ready  # Tempo
curl -s http://localhost:9009/ready  # Mimir
curl -s http://localhost:3100/ready  # Loki
```

### Debug Mode

Enable debug logging:
```properties
logging.level.com.example.otelcrudapi=DEBUG
logging.level.io.opentelemetry=DEBUG
```

## Advanced Configuration

### Environment-Specific Configuration

Create environment-specific configurations:
- `application-dev.properties` - Development settings
- `application-prod.properties` - Production settings
- `application-docker.properties` - Container settings

### Custom Exporters

To add additional exporters, update `config/alloy.alloy` and restart:
```bash
docker-compose restart alloy
```

### Production Considerations

1. **Resource Limits**: Set appropriate CPU/memory limits
2. **Retention**: Configure data retention policies
3. **Security**: Enable authentication and TLS
4. **Scalability**: Use distributed deployments
5. **Backup**: Implement backup strategies

## Docker Commands Reference

```bash
# Start full stack
docker-compose up -d

# Start only infrastructure (no app)
docker-compose up -d --scale otel-crud-api=0

# View logs
docker-compose logs -f [service-name]

# Stop services
docker-compose down

# Stop and remove volumes (⚠️ Data loss)
docker-compose down -v

# Rebuild specific service
docker-compose build [service-name]
docker-compose up -d [service-name]
```

## Maintenance

### Regular Tasks

1. **Monitor disk usage** (MinIO)
2. **Review retention policies**
3. **Update service images**
4. **Backup configurations**

### Cleanup

```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune
```

## Resources

- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Grafana Alloy Documentation](https://grafana.com/docs/alloy/)
- [Tempo Documentation](https://grafana.com/docs/tempo/)
- [Mimir Documentation](https://grafana.com/docs/mimir/)
- [Loki Documentation](https://grafana.com/docs/loki/)

## CI/CD and Quality Metrics

### Build Status Badges

The project includes several badges that show the current state of the codebase:

- **CI/CD Pipeline**: Shows if the latest build is passing
- **Code Coverage**: Shows the percentage of code covered by tests
- **Quality Gate**: Shows overall code quality metrics (requires SonarCloud setup)
- **License**: Shows the project license
- **Java Version**: Shows the Java version used
- **Spring Boot Version**: Shows the Spring Boot version
- **Docker**: Indicates Docker support
- **OpenTelemetry**: Shows OpenTelemetry integration version
- **JaCoCo**: Shows code coverage tool version

### Setting Up CI/CD

1. **GitHub Actions**: The workflow is already configured in `.github/workflows/ci.yml`
2. **Codecov**: Sign up at [codecov.io](https://codecov.io) and add your repository
3. **SonarCloud** (optional): Sign up at [sonarcloud.io](https://sonarcloud.io) for code quality analysis

### Required GitHub Secrets

For full CI/CD functionality, add these secrets to your GitHub repository:

```
Settings → Secrets and variables → Actions → New repository secret
```

**Required secrets:**
- `CODECOV_TOKEN`: Token from Codecov for coverage reporting
- `DOCKER_USERNAME`: Docker Hub username (for Docker image publishing)
- `DOCKER_PASSWORD`: Docker Hub password or access token

### Running Coverage Locally

```bash
# Generate coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Workflow Triggers

The CI/CD pipeline runs on:
- Push to `main` or `develop` branches
- Pull requests to `main` branch

### Pipeline Stages

1. **Test**: Runs unit tests and generates coverage reports
2. **Build**: Compiles the application and creates JAR file
3. **Docker**: Builds and pushes Docker image (main branch only)
4. **Integration Test**: Tests the full application stack (main branch only)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or issues, please open an issue in the GitHub repository.

---

Happy coding and monitoring! 🚀