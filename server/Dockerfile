FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy Spring Boot application
COPY . .
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-Dserver.port=${PORT:-3000}", "-jar", "app.jar"] 