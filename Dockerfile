# Etapa 1: construir el JAR
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: imagen ligera para ejecutar
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia el jar construido desde la etapa builder
# Ajusta el nombre si tu jar tiene otro:
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
