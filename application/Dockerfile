FROM java:8

WORKDIR /work

COPY ./target/application.jar .
ENTRYPOINT ["java", "-jar", "./application.jar"]