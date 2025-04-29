# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

# Copiar arquivos de build
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Garantir que o mvnw tenha permissão de execução
RUN chmod +x mvnw

# Baixar dependências
RUN ./mvnw dependency:go-offline -B

# Copiar o código-fonte
COPY backend/src/ src/

# Build da aplicação
RUN ./mvnw package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
