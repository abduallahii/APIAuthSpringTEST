FROM openjdk:12

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9898

CMD ["java","-jar","/app.jar"]
