FROM maven:3.9.9-eclipse-temurin-17 AS base
WORKDIR /app
RUN apt-get update && apt-get install -y make && rm -rf /var/lib/apt/lists/*
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline

FROM base AS dev
WORKDIR /app
COPY src ./src
COPY Makefile ./
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]

FROM base AS builder
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

FROM base AS test
WORKDIR /app
COPY src ./src
COPY Makefile ./
ENTRYPOINT ["make"]
CMD ["test-all"]
