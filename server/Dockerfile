FROM node:18 AS client-build
WORKDIR /client
# First copy just package files for better caching
COPY ../client/package*.json ./
RUN npm install
# Then copy the rest of the client code
COPY ../client .
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy Spring Boot application
COPY . .
# Create static resources directory
RUN mkdir -p src/main/resources/static
# Copy Angular build output to Spring Boot static resources
COPY --from=client-build /client/dist/client/ src/main/resources/static/
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-Dserver.port=${PORT:-3000}", "-jar", "app.jar"] 