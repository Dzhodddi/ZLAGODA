FROM eclipse-temurin:17-jdk-alpine AS base
WORKDIR /app
COPY pom.xml .
COPY src ./src

FROM base AS dev
RUN apk add --no-cache bash curl git maven
EXPOSE 8080
CMD ["sh", "-c", "mvn spring-boot:run"]

FROM base AS prod-builder
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine AS prod
WORKDIR /app
COPY --from=prod-builder /app/target/java-go-server-1.0.0.jar .
EXPOSE 8080
CMD ["java", "-jar", "java-go-server-1.0.0.jar"]
