# Base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the .jar file into the container's /app directory
COPY ./build/libs/*.jar /app/

# Set entrypoint to run the app
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
