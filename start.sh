#!/bin/bash

# Pull last update
git pull origin master

# Prepare jar
mvn clean
mvn package

# Ensure, that docker-compose stopped
docker-compose stop

# Add environment variables
export DATASOURCE_URL=$1
export DATASOURCE_PASSWORD=$2
export DATASOURCE_USERNAME=$3

# Start new deployment
docker-compose up --build -d