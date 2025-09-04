# Use the official OpenJDK base image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/uploadingservice.jar /app/uploadingservice.jar

# Expose the port the application runs on
EXPOSE 9597

# Run the JAR file when the container launches
CMD ["java", "-jar", "uploadingservice.jar"]