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
RUN npm run build --prod

# Final image
FROM openjdk:17-slim
WORKDIR /app

# Copy the Spring Boot JAR
COPY --from=build /app/target/*.jar /app/app.jar

# Copy the Angular build output to the Spring Boot static resources
COPY --from=angular-build /ng-app/dist/client/ /app/static/

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 3000

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 