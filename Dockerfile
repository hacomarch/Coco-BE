# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application files to the container
COPY ./ ./

# Expose the application port
EXPOSE 8080

# Install Python and Redis
RUN apt-get update && \
    apt-get install -y python3 python3-pip redis-server && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Define environment variable for the Java app
ENV JAVA_OPTS=""

# Start Redis and then run the Java application
ENTRYPOINT ["sh", "-c", "redis-server —daemonize yes && java $JAVA_OPTS -jar app.jar"]
