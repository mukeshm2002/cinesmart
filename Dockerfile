# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar cinesmart.jar
EXPOSE 8080
# 💡 JVM Memory Optimization: 512MB RAM-க்குள்ள அடக்க இந்த -Xmx300m ரொம்ப முக்கியம்!
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "cinesmart.jar"]FROM ubuntu:latest
LABEL authors="ELCOT"

ENTRYPOINT ["top", "-b"]