
# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all module poms
COPY api-service/pom.xml api-service/pom.xml
COPY worker-service/pom.xml worker-service/pom.xml
COPY core-libs/pom.xml core-libs/pom.xml

# Copy all module source code
COPY api-service api-service
COPY worker-service worker-service
COPY core-libs core-libs

# Build only worker-service
RUN mvn clean package -pl worker-service -am -DskipTests

# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/worker-service/target/*.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
