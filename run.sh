#!/bin/bash

export mysql_port="3307"
export mysql_url="localhost:"${mysql_port}
export mysql_password="passw0rd"
export mysql_user="pass"
export mysql_schema_name="pass-management"

echo "============================================= building service ============================================"

mvn clean install

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

sleep 5

echo "============================================== running service ============================================"

mvn spring-boot:run


