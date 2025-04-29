# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

RUN chmod +x mvnw

# Copy build files first (for layer caching)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY backend/mvnw .

RUN apt-get update && apt-get install -y bash

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build application
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]