FROM maven:3.8.4-openjdk-17 AS build

# Copy and build the Spring Boot application
WORKDIR /app
COPY server/ /app/
RUN mvn clean package -DskipTests

# Build the Angular application
FROM node:18 as angular-build
WORKDIR /ng-app
COPY client/ /ng-app/
RUN npm install
RUN npm run build

# Final image
FROM openjdk:17-slim
WORKDIR /app

# Copy the Spring Boot JAR
COPY --from=build /app/target/*.jar /app/app.jar

# Create directory for static resources
RUN mkdir -p /app/static

# Copy the Angular build output to the static directory
COPY --from=angular-build /ng-app/dist/client/browser/ /app/static/

# Expose port
EXPOSE 3000

# Run the application with static resource location
ENTRYPOINT ["java", "-Dspring.web.resources.static-locations=file:/app/static/", "-jar", "/app/app.jar"] 