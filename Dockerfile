# syntax=docker/dockerfile:1

# ======================
# Build stage
# ======================
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies for faster rebuilds
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build application (skip tests)
COPY src src
RUN mvn clean package -DskipTests

# ======================
# Run stage
# ======================
FROM openjdk:17-jdk-slim
WORKDIR /app

# ✅ Install all dependencies required for Java AWT & Font rendering
RUN apt-get update && apt-get install -y --no-install-recommends \
    libfreetype6 \
    fontconfig \
    fonts-dejavu-core \
    ttf-dejavu \
    libx11-6 \
    && fc-cache -fv \
    && rm -rf /var/lib/apt/lists/*

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy resources (Excel/templates/fonts if needed)
COPY src/main/resources/ /app/src/main/resources/

EXPOSE 8084

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
