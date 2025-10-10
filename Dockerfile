# syntax=docker/dockerfile:1

# ======================
# Build stage
# ======================
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src src
RUN mvn clean package -DskipTests

# ======================
# Run stage
# ======================
FROM openjdk:17-jdk-slim
WORKDIR /app

# 🧩 Install fonts and fontconfig to fix "Sun Font Manager" issues
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    fonts-dejavu-core \
    ttf-dejavu \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ✅ Optional: Add custom fonts (uncomment if you have .ttf files)
# COPY fonts/*.ttf /usr/share/fonts/truetype/
# RUN fc-cache -fv

# Copy built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# ✅ Copy Excel (and other resource) files if needed by code
COPY src/main/resources/ /app/src/main/resources/

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
