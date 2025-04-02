FROM openjdk:21-oracle
LABEL maintainer="Almas"
COPY main-service.jar main-service-backend.jar
ENTRYPOINT ["java", "-jar", "main-service-backend.jar"]
