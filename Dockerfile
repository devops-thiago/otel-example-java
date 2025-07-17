# Multi-stage Dockerfile for OpenTelemetry Java Application
# Stage 1: Build stage
FROM maven:3.9.10-amazoncorretto-24 AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files first (for better layer caching)
COPY pom.xml .
# COPY .mvn .mvn

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Verify the JAR file was created
RUN ls -la target/ && \
    find target -name "*.jar" -type f

# Stage 2: Runtime stage (Alpine for minimal image size)
FROM amazoncorretto:24-alpine3.19 AS runtime

# Install curl for health checks and create non-root user
RUN apk add --no-cache curl && \
    addgroup -g 1000 appuser && \
    adduser -u 1000 -G appuser -s /bin/sh -D appuser

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/otel-crud-api-*.jar app.jar

# Create directories for logs and temporary files
RUN mkdir -p /app/logs /app/tmp && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Environment variables for OpenTelemetry
ENV OTEL_SERVICE_NAME=otel-crud-api
ENV OTEL_SERVICE_VERSION=1.0.0
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4320
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_EXPORTER_OTLP_TIMEOUT=10000
ENV OTEL_EXPORTER_OTLP_COMPRESSION=gzip

# JVM options for containerized environments (JDK 24 compatible)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Xlog:gc*:/app/logs/gc.log:time \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=docker"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Default command
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 