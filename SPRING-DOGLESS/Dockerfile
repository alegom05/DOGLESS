FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
EXPOSE 8080
COPY ./target/SPRING-DOGLESS-0.0.1-SNAPSHOT.jar dogless.jar
ENTRYPOINT ["java", "-jar", "dogless.jar"]


