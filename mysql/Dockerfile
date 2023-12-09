FROM --platform=linux/x86_64 mysql:8.0.31


COPY ./sqls/initialize.sql /docker-entrypoint-initdb.d/
COPY ./my.cnf /etc/mysql/conf.d/my.cnf