FROM maven:3-eclipse-temurin-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn dependency:go-offline -f /home/app/pom.xml
RUN mvn -f  /home/app/pom.xml clean test package

FROM openjdk:21-jdk
COPY --from=build /home/app/target/command-service.jar ./command-service.jar
ENTRYPOINT ["java","-jar","command-service.jar"]
