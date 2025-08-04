-- CREATE USER 'xideral'@'%' IDENTIFIED BY 'xideral';
  CREATE DATABASE IF NOT EXISTS javatechie;
  GRANT ALL PRIVILEGES ON javatechie.* TO 'xideral'@'%';
  FLUSH PRIVILEGES;