#NOT FINISHED YET
FROM ubuntu:latest
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get update
RUN apt-get install -y gnupg
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add -
RUN add-apt-repository 'deb https://apt.corretto.aws stable main'
RUN apt-get update
RUN apt-get install -y java-17-amazon-corretto-jdk
RUN apt-get update
RUN apt-get install -y gradle
RUN apt-get update
RUN apt-get install -y mariadb-server
RUN apt-get update
RUN apt-get install -y net-tools
WORKDIR /app
COPY . .
COPY ./src/main/resources/sqlstuff/import_ready_db_10percent.sql /docker-entrypoint-initdb.d/
EXPOSE 3306
EXPOSE 443
RUN mysql -u root -p ${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE nfs"
RUN mysql -u root -p ${MYSQL_ROOT_PASSWORD} -D nfs < /docker-entrypoint-initdb.d/import_ready_db_10percent.sql
RUN gradle clean build
ENTRYPOINT ["java","-jar","/app/build/libs/NFSSoundtrack_2.0-0.9.jar"]