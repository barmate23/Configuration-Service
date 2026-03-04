# syntax=docker/dockerfile:1

# ======================
# Build stage
# ======================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src src
RUN mvn clean package -DskipTests

# ======================
# Run stage
# ======================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Install fonts (required for SunFontManager / PDF / reports)
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8084

# Headless Java mode
ENV JAVA_OPTS="-Djava.awt.headless=true"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
