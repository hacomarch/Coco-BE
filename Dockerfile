# Stage 1: Build stage
FROM openjdk:17-slim as build

WORKDIR /app

# Copy project files to the working directory
COPY . .

# Configure proxy for Gradle
RUN mkdir -p /root/.gradle && \
    echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

# Make gradlew executable and build the project
RUN chmod +x gradlew && ./gradlew build -x test

# List the build output to verify
RUN ls /app/build/libs/

# Stage 2: Runtime stage
FROM openjdk:17-slim
VOLUME /tmp

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/ide-0.0.1-SNAPSHOT.jar /app/ide-0.0.1-SNAPSHOT.jar

# Install Python and Redis
RUN apt-get update && \
    apt-get install -y python3 python3-pip redis-server && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set the entry point to run the application
ENTRYPOINT ["java","-jar","/app/ide-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080/tcp
