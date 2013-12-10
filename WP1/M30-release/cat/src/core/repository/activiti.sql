DROP DATABASE IF EXISTS activiti;
DROP USER 'catActiviti'@'localhost';

create database activiti;

create user 'catActiviti'@'localhost' IDENTIFIED BY 'password';

grant all privileges on activiti.* to catActiviti@localhost;