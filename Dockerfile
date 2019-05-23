FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY target/pass-management.jar pass-management.jar
ENTRYPOINT ["java","-jar","pass-management.jar"]