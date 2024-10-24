FROM openjdk:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
ADD ./target/SPRING-DOGLESS-0.0.1-SNAPSHOT.jar dogless.jar
ENTRYPOINT ["java", "-jar", "dogless.jar"]

