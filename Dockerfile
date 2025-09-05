# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
