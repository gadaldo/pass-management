#!/bin/bash

echo "========================================= removing mysql container ========================================"

docker stop $(docker ps -a -q --filter="name=mysql")
docker rm $(docker ps -a -q --filter="name=mysql")

echo "========================================= running mysql instance =========================================="

docker run -d -p 3306:3306 --name=mysql --env="MYSQL_ROOT_PASSWORD=passw0rd!" --env="MYSQL_PASSWORD=passw0rd!" --env="MYSQL_DATABASE=pass-management" mysql

sleep 5

echo "=================================================== Done =================================================="

echo "==================================== building artifact & running service =================================="

mvn clean install spring-boot:run
