FROM eclipse-temurin:17-jdk as builder
WORKDIR /workspace
# Copy only what's needed for build
COPY backend/pom.xml .
COPY backend/.mvn .mvn
COPY backend/mvnw .
# Download dependencies first (cache this layer)
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY backend/src src
RUN ./mvnw package -DskipTests

# Final image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]