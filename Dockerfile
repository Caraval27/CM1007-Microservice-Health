FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /build_journal_app
COPY Lab3_Health/pom.xml .

RUN mvn dependency:go-offline

COPY Lab3_Health/src ./src

RUN mvn clean test

RUN mvn clean package

FROM openjdk:17-jdk-alpine
WORKDIR /journal_app
EXPOSE 8080

COPY --from=build /build_journal_app/target/*.jar /journal_app/

CMD ["sh", "-c", "java -jar /journal_app/Lab3_Health-0.0.1-SNAPSHOT.jar && java -jar /journal_app/Lab3_Health-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]