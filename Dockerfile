# ======================
# Run stage
# ======================
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install dependencies for Java AWT / SunFontManager
RUN apt-get update && apt-get install -y --no-install-recommends \
    libfreetype6 \
    fontconfig \
    fonts-dejavu-core \
    libx11-6 \
    && fc-cache -fv \
    && rm -rf /var/lib/apt/lists/*

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy resources
COPY src/main/resources/ /app/src/main/resources/

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
