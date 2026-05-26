FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-kernel shared-kernel
COPY scheduling-service scheduling-service
COPY history-service history-service
COPY notification-service notification-service

RUN mvn -pl scheduling-service -am -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/scheduling-service/target/scheduling-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
