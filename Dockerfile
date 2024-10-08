FROM ubuntu:22.04 AS build-common

WORKDIR /build

RUN apt update --fix-missing \
    && apt install -y --no-install-recommends openjdk-21-jdk maven curl \
    && apt clean

COPY ./pom.xml /build/pom.xml
COPY ./src /build/src
COPY ./.git .git

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests


WORKDIR /app
RUN cp /build/target/*.jar /app/aggregator.jar
RUN rm -rf /build

ENTRYPOINT ["java", "-jar",  "/app/aggregator.jar"]

CMD ["/bin/sh", "-c", "bash"]