CREATE SCHEMA IF NOT EXISTS `cocoide` DEFAULT CHARACTER SET utf8mb4;

GRANT ALL ON *.* TO 'root'@'localhost' IDENTIFIED BY 'root' WITH GRANT OPTION;
GRANT ALL ON cocoide.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

USE `cocoide`;
