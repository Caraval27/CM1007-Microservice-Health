FROM openjdk:17-jdk-alpine
WORKDIR /journal_app
EXPOSE 8080

ARG JAR_FILE=Lab3_Health-0.0.1-SNAPSHOT.jar
ARG DEPENDENCY_JAR_FILE=Lab3_Health-0.0.1-SNAPSHOT-jar-with-dependencies.jar

COPY ${JAR_FILE} /journal_app/
COPY ${DEPENDENCY_JAR_FILE} /journal_app/

CMD ["sh", "-c", "java -jar /journal_app/Lab3_Health-0.0.1-SNAPSHOT.jar && java -jar /journal_app/Lab3_Health-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
