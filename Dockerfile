FROM openjdk:21-jdk-slim

COPY target/transaction-management-0.0.1-SNAPSHOT.jar /transaction-management-0.0.1.jar
EXPOSE 8080
CMD ["java", "-jar", "transaction-management-0.0.1.jar"]