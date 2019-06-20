#!/bin/bash

export mysql_port="33090"
export mysql_url="localhost:"${mysql_port}
export mysql_password="passw0rd"
export mysql_user="pass"
export mysql_schema_name="pass-management"

PROJECT_NAME="pass-management"
MAVEN_IMAGE="maven:3-jdk-8-slim"

echo "============================================= building service ============================================"

docker run -it --rm -v "$(pwd)"/:/usr/src/$PROJECT_NAME -w /usr/src/$PROJECT_NAME $MAVEN_IMAGE mvn clean install

echo "========================================= removing mysql container ========================================"

docker stop $(docker ps -a -q --filter="name=mysql")
docker rm $(docker ps -a -q --filter="name=mysql")

echo "========================================= running mysql instance =========================================="

docker run -d -p ${mysql_port}:3306 --name=mysql \
	-e MYSQL_ROOT_PASSWORD=${mysql_password} \
	-e MYSQL_PASSWORD=${mysql_password} \
	-e MYSQL_USER=${mysql_user} \
	-e MYSQL_DATABASE=${mysql_schema_name} \
	mysql

sleep 10

#while ! mysqladmin ping -h"$DB_HOST" --silent; do
#    sleep 1
#done

echo "============================================== running service ============================================"

mvn spring-boot:run