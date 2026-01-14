FROM eclipse-temurin:17-jdk-alpine AS dev
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache bash maven
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]

FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN apk add --no-cache maven
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/java-go-server-1.0.0.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseSerialGC", \
    "-Xss512k", \
    "-jar", "app.jar"]