# syntax=docker/dockerfile:1
# Pinned base images (2026-09): Maven 3.10 + JDK 25 (current LTS) to build,
# JRE 25 to run. Multi-stage keeps the runtime image at ~250 MB.

# ---- Build stage ----------------------------------------------------------
FROM maven:3.10.0-eclipse-temurin-25 AS build
WORKDIR /app

# Copy the POM first so dependency downloads are cached independently of source.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Test compilation is skipped here (CI owns tests) so a broken test never blocks a deploy.
RUN mvn -B -Dmaven.test.skip=true package

# ---- Runtime stage --------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine-3.24
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar

# JAVA_OPTS defaults to container-aware memory tuning: heap <= 75% of the
# container memory so the JVM never fights the kernel OOM killer. Override
# freely with a JAVA_OPTS service variable.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
