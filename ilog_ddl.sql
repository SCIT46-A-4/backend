create database ilog;


-- -----------------------------------------------------
-- Schema ilog
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `ilog` ;

-- -----------------------------------------------------
-- Schema ilog
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ilog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `ilog` ;


-- -----------------------------------------------------
-- Table `ilog`.`claims`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`claims`;

CREATE TABLE IF NOT EXISTS `ilog`.`claims` (
  `claims_id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `question` TEXT NOT NULL,
  `answer` TEXT NULL DEFAULT NULL,
  `category` ENUM('GENERAL', 'TECHNICAL', 'BILLING', 'OTHER') NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`claims_id`),
  CONSTRAINT `fk_claims_member`
    FOREIGN KEY (`member_id`)
    REFERENCES `ilog`.`member` (`member_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `fk_claims_member` ON `ilog`.`claims` (`member_id` ASC) VISIBLE;

commit;
select * from `ilog`.`claims`;

-- -----------------------------------------------------
-- Table `ilog`.`child`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`child` ;

CREATE TABLE IF NOT EXISTS `ilog`.`child` (
  `child_id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `birth_date` DATETIME NULL,
  `note` VARCHAR(1000) NULL DEFAULT NULL,
  `gender` ENUM('W', 'M', 'N') NOT NULL,
  `weight` DOUBLE NULL,
  `height` DOUBLE NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  PRIMARY KEY (`child_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `ilog`.`member`
-- -----------------------------------------------------
use ilog;
DROP TABLE IF EXISTS `ilog`.`member` ;

CREATE TABLE IF NOT EXISTS `ilog`.`member` (
  `member_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(10) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `password` CHAR(65) NOT NULL,
  `supervisor_role` ENUM('parent', 'teacher', 'expert', 'carer') NOT NULL,
  `role` ENUM('USER', 'ADMIN') NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  PRIMARY KEY (`member_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

commit;
select * from `ilog`.`member`;

CREATE UNIQUE INDEX `email` ON `ilog`.`member` (`email` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `ilog`.`child_diary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`child_diary` ;

CREATE TABLE IF NOT EXISTS `ilog`.`child_diary` (
  `diary_id` BIGINT NOT NULL AUTO_INCREMENT,
  `child_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_by` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  `author_id` BIGINT NOT NULL,
  PRIMARY KEY (`diary_id`),
  CONSTRAINT `fk_diary_child`
    FOREIGN KEY (`child_id`)
    REFERENCES `ilog`.`child` (`child_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_diary_member`
    FOREIGN KEY (`author_id`)
    REFERENCES `ilog`.`member` (`member_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `fk_diary_child` ON `ilog`.`child_diary` (`child_id` ASC) VISIBLE;

CREATE INDEX `fk_diary_user` ON `ilog`.`child_diary` (`author_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `ilog`.`diary_analysis_results`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`diary_analysis_result` ;

CREATE TABLE IF NOT EXISTS `ilog`.`diary_analysis_result` (
  `analysis_id` INT NOT NULL AUTO_INCREMENT,
  `diary_id` BIGINT NOT NULL,
  `emotion_score` FLOAT NULL DEFAULT NULL,
  `analysis_result` TEXT NULL DEFAULT NULL,
  `suggested_solution` TEXT NULL DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  PRIMARY KEY (`analysis_id`),
  CONSTRAINT `fk_analysis_diary`
    FOREIGN KEY (`diary_id`)
    REFERENCES `ilog`.`child_diary` (`diary_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `fk_analysis_diary` ON `ilog`.`diary_analysis_results` (`diary_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `ilog`.`health_surveys`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`health_survey` ;

CREATE TABLE IF NOT EXISTS `ilog`.`health_survey` (
  `survey_id` BIGINT NOT NULL AUTO_INCREMENT,
  `child_id` BIGINT NOT NULL,
  `submitted_by` BIGINT NOT NULL,
  `survey_data` JSON NULL DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  PRIMARY KEY (`survey_id`),
  CONSTRAINT `fk_survey_child`
    FOREIGN KEY (`child_id`)
    REFERENCES `ilog`.`child` (`child_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_survey_user`
    FOREIGN KEY (`submitted_by`)
    REFERENCES `ilog`.`member` (`member_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `fk_survey_child` ON `ilog`.`health_surveys` (`child_id` ASC) VISIBLE;

CREATE INDEX `fk_survey_user` ON `ilog`.`health_surveys` (`submitted_by` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `ilog`.`relationship`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`relationship` ;

CREATE TABLE IF NOT EXISTS `ilog`.`relationship` (
  `relationship_id` BIGINT NOT NULL AUTO_INCREMENT,
  `member_id` BIGINT NOT NULL,
  `child_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `permission_level` ENUM('owner', 'editor', 'viewer') NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_at` TIMESTAMP NULL,
  `created_by` BIGINT NOT NULL,
  `modified_by` BIGINT NULL,
  PRIMARY KEY (`relationship_id`),
  CONSTRAINT `fk_relationship_child_id_child`
    FOREIGN KEY (`child_id`)
    REFERENCES `ilog`.`child` (`child_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_relationship_member_id_member`
    FOREIGN KEY (`member_id`)
    REFERENCES `ilog`.`member` (`member_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `fk_relationship_child_id_child` ON `ilog`.`relationship` (`child_id` ASC) VISIBLE;

CREATE INDEX `fk_relationship_member_id_member_idx` ON `ilog`.`relationship` (`member_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `ilog`.`child_media_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ilog`.`child_media_data` ;

CREATE TABLE IF NOT EXISTS `ilog`.`child_media_data` (
  `media_id` BIGINT NOT NULL AUTO_INCREMENT,
  `child_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `modified_at` TIMESTAMP NULL,
  `type` VARCHAR(45) NOT NULL,
  `created_by` BIGINT NOT NULL,
  `modified_by` BIGINT NULL,
  `analysis_result` VARCHAR(1000) NULL,
  PRIMARY KEY (`media_id`),
  CONSTRAINT `fk_child_media_data_child_id_child`
    FOREIGN KEY (`child_id`)
    REFERENCES `ilog`.`child` (`child_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_child_media_data_child_id_child_idx` ON `ilog`.`child_media_data` (`child_id` ASC) VISIBLE;

SET SQL_MODE = '';
DROP USER IF EXISTS was_ilog;
SET SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
CREATE USER 'was_ilog' IDENTIFIED BY 'was_ilog';

GRANT SELECT ON TABLE `ilog`.* TO 'was_ilog';
GRANT SELECT, INSERT, TRIGGER ON TABLE `ilog`.* TO 'was_ilog';
GRANT SELECT, INSERT, TRIGGER, UPDATE, DELETE ON TABLE `ilog`.* TO 'was_ilog';
GRANT EXECUTE ON ROUTINE `ilog`.* TO 'was_ilog';


