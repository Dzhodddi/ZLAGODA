FROM maven:3.9.9-eclipse-temurin-17 AS base
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY Makefile ./

FROM base AS dev
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]

FROM base AS builder
RUN mvn -B dependency:go-offline
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/java-go-server-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

FROM base AS test
WORKDIR /app
ENTRYPOINT ["make"]
CMD ["test-all"]
