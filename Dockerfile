# Base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the .jar file into the container's /app directory
COPY ./build/libs/todo-0.0.1-SNAPSHOT.jar /app/todo.jar

# Set entrypoint to run the app
ENTRYPOINT ["java", "-jar", "/app/todo.jar"]
