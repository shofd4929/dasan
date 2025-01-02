# Use a slim OpenJDK image for the final image
FROM openjdk:17-jdk-slim

# Set the working directory for the final image
WORKDIR /app

# Copy the pre-built JAR file from your local machine (build/libs folder)
COPY build/libs/SpringBatch-0.0.1-SNAPSHOT.jar app.jar

# Make the JAR file executable
RUN chmod +x /app/app.jar

# Expose the application's port
EXPOSE 8091

# Start the Spring application
ENTRYPOINT ["java", "-jar", "app.jar"]
