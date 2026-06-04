FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY core-libs ./core-libs
COPY worker-service ./worker-service
COPY api-service ./api-service

RUN mvn clean package -pl api-service -am -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/api-service/target/api-service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar"]