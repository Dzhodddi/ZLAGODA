FROM maven:3.9.9-eclipse-temurin-17 AS dev
WORKDIR /app
COPY pom.xml .
COPY src ./src
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/java-go-server-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
