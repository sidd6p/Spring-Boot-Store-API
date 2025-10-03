# Simple Dockerfile for Development
FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy everything else (source code, .env, etc)
COPY . .

EXPOSE 8080

# Build and run
CMD ["mvn", "spring-boot:run"]

