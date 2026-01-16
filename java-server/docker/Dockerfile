FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM maven:3.9.9-eclipse-temurin-17 AS runtime
WORKDIR /app
ARG MODE=prod
ENV MODE=${MODE}
COPY --from=builder /app/target/*.jar app.jar
COPY src ./src
EXPOSE 8080 5005
CMD if [ "$MODE" = "dev" ]; then \
        echo "Running in DEV mode"; \
        mvn spring-boot:run -Dspring-boot.run.fork=false; \
    else \
        echo "Running in PROD mode"; \
        java -jar app.jar; \
    fi
