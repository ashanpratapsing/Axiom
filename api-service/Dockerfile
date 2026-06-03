FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy modules
COPY core-libs ./core-libs
COPY api-service ./api-service

# Build only api-service with dependencies
RUN mvn clean package -pl api-service -am -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/api-service/target/api-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar"]