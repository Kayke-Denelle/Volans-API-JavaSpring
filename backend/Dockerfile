FROM eclipse-temurin:17-jdk
WORKDIR /app/backend  # Assume que "backend" é a pasta do Spring Boot
COPY ./backend .      # Copia apenas a pasta backend
RUN chmod +x mvnw && ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/seu-app.jar"]