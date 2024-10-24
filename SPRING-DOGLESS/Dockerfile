FROM openjdk:24-ea-17-slim-bullseye
VOLUME /tmp
EXPOSE 8080
ADD ./target/SPRING-DOGLESS-0.0.1-SNAPSHOT.jar dogless.jar
ENTRYPOINT ["java", "-jar", "dogless.jar"]

