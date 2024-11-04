FROM openjdk:21-jdk-slim as builder
WORKDIR /build
COPY . /build
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=builder /build/target/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]
