#!/bin/bash

echo "============================================= building service ============================================"

mvn clean install

echo "========================================= removing mysql container ========================================"

docker stop $(docker ps -a -q --filter="name=mysql")
docker rm $(docker ps -a -q --filter="name=mysql")

echo "========================================= running mysql instance =========================================="

docker run -d -p 3306:3306 --name=mysql -e MYSQL_ROOT_PASSWORD=passw0rd -e MYSQL_PASSWORD=passw0rd -e MYSQL_USER=pass -e MYSQL_DATABASE=pass-management mysql

sleep 5

echo "============================================== running service ============================================"

mvn spring-boot:run


