FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mBankAuthorization.jar
ENTRYPOINT ["java","-jar","/mBankAuthorization.jar"]