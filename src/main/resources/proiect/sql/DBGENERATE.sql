-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb3 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`carte`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`carte` (
  `idCarte` INT UNSIGNED NOT NULL,
  `titluCarti` VARCHAR(45) NOT NULL,
  `autorCarte` VARCHAR(45) NOT NULL,
  `numarCarte` VARCHAR(45) NOT NULL,
  `genCarte` VARCHAR(45) NOT NULL,
  `coverCarte` VARCHAR(200) NULL DEFAULT NULL,
  `descriere` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`idCarte`),
  UNIQUE INDEX `idCarti_UNIQUE` (`idCarte` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `idUser` INT NOT NULL AUTO_INCREMENT,
  `userName` VARCHAR(16) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `librarian` TINYINT(1) NOT NULL,
  PRIMARY KEY (`idUser`))
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`cartiimprumutate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`cartiimprumutate` (
  `idImprumut` INT NOT NULL AUTO_INCREMENT,
  `dataImprumut` DATE NOT NULL,
  `dataRetur` DATE NOT NULL COMMENT '30 de zile peste data Retur\\\\\\\\n',
  `User_idUser` INT NOT NULL,
  `Carte_idCarte` INT UNSIGNED NOT NULL,
  `status` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idImprumut`, `User_idUser`, `Carte_idCarte`),
  UNIQUE INDEX `idImprumut_UNIQUE` (`idImprumut` ASC) VISIBLE,
  INDEX `fk_CartiImprumutate_User_idx` (`User_idUser` ASC) VISIBLE,
  INDEX `fk_CartiImprumutate_Carte1_idx` (`Carte_idCarte` ASC) VISIBLE,
  CONSTRAINT `fk_CartiImprumutate_Carte1`
    FOREIGN KEY (`Carte_idCarte`)
    REFERENCES `mydb`.`carte` (`idCarte`),
  CONSTRAINT `fk_CartiImprumutate_User`
    FOREIGN KEY (`User_idUser`)
    REFERENCES `mydb`.`user` (`idUser`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`genuri`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`genuri` (
  `idpreferinte` INT NOT NULL AUTO_INCREMENT,
  `genuri` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idpreferinte`),
  UNIQUE INDEX `idpreferinte_UNIQUE` (`idpreferinte` ASC) VISIBLE,
  UNIQUE INDEX `genuri_UNIQUE` (`genuri` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`myreads`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`myreads` (
  `idmyreads` INT NOT NULL AUTO_INCREMENT,
  `coverCarte` VARCHAR(200) NULL DEFAULT NULL,
  `user_idUser` INT NOT NULL,
  PRIMARY KEY (`idmyreads`, `user_idUser`),
  UNIQUE INDEX `idmyreads_UNIQUE` (`idmyreads` ASC) VISIBLE,
  INDEX `fk_myreads_user1_idx` (`user_idUser` ASC) VISIBLE,
  CONSTRAINT `fk_myreads_user1`
    FOREIGN KEY (`user_idUser`)
    REFERENCES `mydb`.`user` (`idUser`))
ENGINE = InnoDB
AUTO_INCREMENT = 48
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `mydb`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`review` (
  `idReview` INT NOT NULL AUTO_INCREMENT,
  `reviewText` VARCHAR(1500) NULL DEFAULT NULL,
  `reviewRating` INT NOT NULL,
  `User_idUser` INT NOT NULL,
  `Carte_idCarte` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`idReview`, `User_idUser`, `Carte_idCarte`),
  INDEX `fk_Review_User1_idx` (`User_idUser` ASC) VISIBLE,
  INDEX `fk_Review_Carte1_idx` (`Carte_idCarte` ASC) VISIBLE,
  CONSTRAINT `fk_Review_Carte1`
    FOREIGN KEY (`Carte_idCarte`)
    REFERENCES `mydb`.`carte` (`idCarte`),
  CONSTRAINT `fk_Review_User1`
    FOREIGN KEY (`User_idUser`)
    REFERENCES `mydb`.`user` (`idUser`))
ENGINE = InnoDB
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `mydb`.`userpref`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`userpref` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `number` INT NOT NULL DEFAULT '0',
  `user_idUser` INT NOT NULL,
  `preferinte_idpreferinte` INT NOT NULL,
  PRIMARY KEY (`id`, `user_idUser`, `preferinte_idpreferinte`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_userpref_user1_idx` (`user_idUser` ASC) VISIBLE,
  INDEX `fk_userpref_preferinte1_idx` (`preferinte_idpreferinte` ASC) VISIBLE,
  CONSTRAINT `fk_userpref_preferinte1`
    FOREIGN KEY (`preferinte_idpreferinte`)
    REFERENCES `mydb`.`genuri` (`idpreferinte`),
  CONSTRAINT `fk_userpref_user1`
    FOREIGN KEY (`user_idUser`)
    REFERENCES `mydb`.`user` (`idUser`))
ENGINE = InnoDB
AUTO_INCREMENT = 85
DEFAULT CHARACTER SET = utf8mb3;

USE `mydb`;

DELIMITER $$
USE `mydb`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `mydb`.`CartiImprumutate_BEFORE_INSERT`
BEFORE INSERT ON `mydb`.`cartiimprumutate`
FOR EACH ROW
BEGIN
IF NEW.dataRetur IS NULL THEN
	SET NEW.dataRetur = date_add(NEW.dataImprumut, INTERVAL 30 DAY);
end if;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
