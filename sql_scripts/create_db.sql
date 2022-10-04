CREATE DATABASE IF NOT EXISTS `ser322_games_db`;
USE `ser322_games_db`;

CREATE TABLE `person` (
  `employee_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`employee_id`));

CREATE TABLE `ceo` (
  `employee_id` INT NOT NULL,
  PRIMARY KEY (`employee_id`),
  CONSTRAINT `fk_ceo_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `person` (`employee_id`) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE `designer` (
  `employee_id` INT NOT NULL,
  `salary` DECIMAL(10, 2) DEFAULT NULL,
  `employment_date` DATE DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  CONSTRAINT `fk_des_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `person` (`employee_id`) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE `publisher` (
  `company_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`company_id`));

CREATE TABLE `game` (
  `game_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `profit` DECIMAL(10, 2) DEFAULT NULL,
  `genre` VARCHAR(45) DEFAULT NULL,
  `release_date` DATE NOT NULL,
  PRIMARY KEY (`game_id`));

CREATE TABLE `rating` (
  `game_id` INT NOT NULL,
  `reviewer` VARCHAR(45) NOT NULL,
  `score` INT DEFAULT NULL,
  PRIMARY KEY (`game_id`, `reviewer`),
  CONSTRAINT `fk_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON UPDATE CASCADE);

CREATE TABLE `works_for` (
  `employee_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  PRIMARY KEY (`employee_id`, `company_id`),
  CONSTRAINT `fk_for_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `person` (`employee_id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_for_company_id` FOREIGN KEY (`company_id`) REFERENCES `publisher` (`company_id`) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE `works_on` (
  `employee_id` INT NOT NULL,
  `game_id` INT NOT NULL,
  PRIMARY KEY (`employee_id`, `game_id`),
  CONSTRAINT `fk_on_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `person` (`employee_id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_on_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE `publish` (
  `company_id` INT NOT NULL,
  `game_id` INT NOT NULL,
  PRIMARY KEY (`company_id`, `game_id`),
  CONSTRAINT `fk_pub_company_id` FOREIGN KEY (`company_id`) REFERENCES `publisher` (`company_id`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_pub_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON UPDATE CASCADE ON DELETE CASCADE);

