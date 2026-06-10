# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Package stage
# 💡 FIX: openjdk-க்கு பதிலா eclipse-temurin இமேஜ் பயன்படுத்துகிறோம்
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/*.jar cinesmart.jar
EXPOSE 8080

# JVM Memory Optimization: 512MB RAM-க்குள்ள அடக்க -Xmx300m
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "cinesmart.jar"]