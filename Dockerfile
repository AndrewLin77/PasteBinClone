# 1. Build Stage: Uses Maven to build the JAR file
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run Stage: Creates the final minimal image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Open port 8080
EXPOSE 8080
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]