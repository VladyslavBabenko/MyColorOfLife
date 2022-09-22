#!/bin/bash

# Pull last update
#git pull origin master

# Prepare jar
mvn clean
mvn package

# Ensure, that docker-compose stopped
docker-compose stop

# Add environment variables
export DATASOURCE_URL=$1
export DATASOURCE_PASSWORD=$2
export DATASOURCE_USERNAME=$3
export GOOGLE_CLIENT_ID=$4
export GOOGLE_CLIENT_SECRET=$5
export EMAIL_USERNAME=$6
export EMAIL_PASSWORD=$7

# Start new deployment
docker-compose up --build -d