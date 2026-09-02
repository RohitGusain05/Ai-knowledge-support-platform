FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/knowledge-support-backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/storage/documents
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
