
# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy root pom and module poms
COPY pom.xml .
COPY worker-service/pom.xml worker-service/pom.xml
COPY core-libs/pom.xml core-libs/pom.xml

# Copy source code
COPY worker-service worker-service
COPY core-libs core-libs

# Build worker-service jar
RUN mvn clean package -pl worker-service -am -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy generated jar from build stage
COPY --from=build /app/worker-service/target/*.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]

