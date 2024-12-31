# Use a base OpenJDK image for the build stage
FROM openjdk:17-jdk-slim AS build

# Set the working directory for Gradle build
WORKDIR /app

# Copy the Gradle wrapper files and build files
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle /app/gradle

# Make the gradlew file executable
RUN chmod +x gradlew

# Copy the source code
COPY src /app/src

# Run Gradle build using the wrapper
RUN ./gradlew build --no-daemon

# Use a slim OpenJDK image for the final image
FROM openjdk:17-jdk-slim

# Set the working directory for the final image
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/SpringBatch-0.0.1-SNAPSHOT.jar app.jar

# Make the JAR file executable
RUN chmod +x /app/app.jar

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
