FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/*.jar
ENV DATASOURCE_URL=db_url
ENV DATASOURCE_PASSWORD=db_password
ENV DATASOURCE_USERNAME=db_username
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.datasource.url=${DATASOURCE_URL}","-Dspring.datasource.password=${DATASOURCE_PASSWORD}", "-Dspring.datasource.username=${DATASOURCE_USERNAME}", "-jar", "app.jar"]
