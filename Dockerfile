FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/*.jar
ENV DATASOURCE_URL=db_url
ENV DATASOURCE_PASSWORD=db_password
ENV DATASOURCE_USERNAME=db_username
ENV GOOGLE_CLIENT_ID=google_client_id
ENV GOOGLE_CLIENT_SECRET=google_client_secret
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.datasource.url=${DATASOURCE_URL}", "-Dspring.datasource.password=${DATASOURCE_PASSWORD}", "-Dspring.datasource.username=${DATASOURCE_USERNAME}", "-Dspring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}", "-Dspring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}", "-jar", "app.jar"]