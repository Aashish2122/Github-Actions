# ---------- stage 1: build ----------
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy ONLY the pom first. This layer changes rarely, so the expensive
# dependency download below stays cached across source-code changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Source changes on every commit, so it must come AFTER the download.
COPY src ./src
RUN mvn -B clean package

# ---------- stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.source="https://github.com/Aashish2122/Github-Actions"
LABEL org.opencontainers.image.description="NoteKeeper API"
LABEL org.opencontainers.image.licenses="MIT"

RUN apk upgrade --no-cache

WORKDIR /app

# Do not run as root.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Only the jar crosses the boundary. Renamed so the version is not hardcoded.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]