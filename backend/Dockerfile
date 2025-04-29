FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests
CMD ["java", "-jar", "/app/target/seu-app.jar"]