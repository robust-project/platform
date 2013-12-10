SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `robustwp1` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `robustwp1` ;

-- -----------------------------------------------------
-- Table `robustwp1`.`community`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`community` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`community` (
  `idcommunity` INT NOT NULL AUTO_INCREMENT ,
  `com_name` VARCHAR(250) NOT NULL ,
  `com_uuid` VARCHAR(150) NULL ,
  `com_uri` VARCHAR(250) NULL ,
  `com_isStream` TINYINT(1) NULL ,
  `com_id` VARCHAR(250) NULL ,
  `com_streamName` VARCHAR(250) NULL ,
  PRIMARY KEY (`idcommunity`) ,
  UNIQUE INDEX `name_UNIQUE` (`com_name` ASC) ,
  UNIQUE INDEX `uuid_UNIQUE` (`com_uuid` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`objective`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`objective` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`objective` (
  `idobjective` INT NOT NULL AUTO_INCREMENT ,
  `uuid` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(250) NOT NULL ,
  `community_idcommunity` INT NOT NULL ,
  `title` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`idobjective`) ,
  INDEX `fk_objective_community1` (`community_idcommunity` ASC) ,
  CONSTRAINT `fk_objective_community1`
    FOREIGN KEY (`community_idcommunity` )
    REFERENCES `robustwp1`.`community` (`idcommunity` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`riskop`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`riskop` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`riskop` (
  `idriskop` INT NOT NULL AUTO_INCREMENT ,
  `community_idcommunity` INT NOT NULL ,
  `uuid` VARCHAR(45) NOT NULL ,
  `state` VARCHAR(45) NOT NULL DEFAULT 'INACTIVE' ,
  `title` VARCHAR(150) NULL ,
  `scope` VARCHAR(45) NULL ,
  `cat_review_freq` INT NULL ,
  `cat_review_period` VARCHAR(45) NULL ,
  `admin_review_freq` INT NULL ,
  `admin_review_period` VARCHAR(45) NULL ,
  `notification` TINYINT(1) NULL ,
  `owner` VARCHAR(45) NULL ,
  `type` TINYINT(1) NULL ,
  `group` VARCHAR(45) NULL ,
  PRIMARY KEY (`idriskop`) ,
  INDEX `fk_riskop_community1` (`community_idcommunity` ASC) ,
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC) ,
  CONSTRAINT `fk_riskop_community1`
    FOREIGN KEY (`community_idcommunity` )
    REFERENCES `robustwp1`.`community` (`idcommunity` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`predictor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`predictor` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`predictor` (
  `idpredictor` INT NOT NULL AUTO_INCREMENT ,
  `predic_uuid` VARCHAR(45) NOT NULL ,
  `predic_name` VARCHAR(45) NOT NULL ,
  `predic_version` VARCHAR(45) NULL ,
  `predic_desc` VARCHAR(256) NULL ,
  `predic_uri` VARCHAR(256) NULL ,
  `predic_svctargetnamespace` VARCHAR(256) NULL ,
  `predic_svcname` VARCHAR(256) NULL ,
  `predic_portname` VARCHAR(128) NULL ,
  PRIMARY KEY (`idpredictor`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`event` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`event` (
  `idevents` INT NOT NULL AUTO_INCREMENT ,
  `event_uuid` VARCHAR(45) NOT NULL ,
  `event_title` VARCHAR(150) NOT NULL ,
  `event_desc` VARCHAR(250) NULL ,
  `predictor_idpredictor` INT NOT NULL ,
  PRIMARY KEY (`idevents`) ,
  INDEX `fk_events_predictor2` (`predictor_idpredictor` ASC) ,
  CONSTRAINT `fk_events_predictor1`
    FOREIGN KEY (`predictor_idpredictor` )
    REFERENCES `robustwp1`.`predictor` (`idpredictor` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`impact`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`impact` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`impact` (
  `idImpact` INT NULL ,
  `riskop_idriskop` INT NOT NULL ,
  `value` VARCHAR(45) NOT NULL ,
  `objective_idobjective` INT NOT NULL ,
  PRIMARY KEY (`riskop_idriskop`, `objective_idobjective`) ,
  INDEX `fk_Impact_riskop1` (`riskop_idriskop` ASC) ,
  INDEX `fk_Impact_objective1` (`objective_idobjective` ASC) ,
  CONSTRAINT `fk_Impact_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Impact_objective1`
    FOREIGN KEY (`objective_idobjective` )
    REFERENCES `robustwp1`.`objective` (`idobjective` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`treat_risk`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`treat_risk` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`treat_risk` (
  `uuid` VARCHAR(250) NOT NULL ,
  `riskop_idriskop` INT NOT NULL ,
  `priority` FLOAT NULL ,
  `title` VARCHAR(250) NULL ,
  `description` VARCHAR(250) NULL ,
  PRIMARY KEY (`uuid`, `riskop_idriskop`) ,
  INDEX `fk_treat_risk_riskop1` (`riskop_idriskop` ASC) ,
  CONSTRAINT `fk_treat_risk_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`evaluationresult`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`evaluationresult` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`evaluationresult` (
  `idevalresults` INT NOT NULL AUTO_INCREMENT ,
  `riskop_idriskop` INT NOT NULL ,
  `currentdate` VARCHAR(150) NOT NULL ,
  `uuid` VARCHAR(150) NOT NULL ,
  `forecastdate` DATE NOT NULL ,
  `metadata` VARCHAR(450) NULL ,
  `jobref` VARCHAR(120) NULL ,
  `status` VARCHAR(45) NULL ,
  `requestdate` VARCHAR(150) NULL ,
  `startdate` VARCHAR(150) NULL ,
  `completiondate` VARCHAR(150) NULL ,
  `currentobs` VARCHAR(45) NULL ,
  INDEX `fk_evaluationResult_riskop1` (`riskop_idriskop` ASC) ,
  PRIMARY KEY (`idevalresults`) ,
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC) ,
  CONSTRAINT `fk_evaluationResult_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`parameter`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`parameter` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`parameter` (
  `idparameter` INT NOT NULL AUTO_INCREMENT ,
  `param_uuid` VARCHAR(45) NULL ,
  `predictor_idpredict` INT NULL ,
  `event_idevent` INT NULL ,
  `param_name` VARCHAR(250) NULL ,
  `param_desc` VARCHAR(250) NULL ,
  `param_unit` VARCHAR(45) NULL ,
  `param_value_type` VARCHAR(45) NULL ,
  `param_allowed_type` VARCHAR(45) NULL ,
  `param_value` VARCHAR(45) NULL ,
  `param_value_eval_type` VARCHAR(45) NULL ,
  `forecast_period_param` TINYINT(1) NOT NULL DEFAULT false ,
  PRIMARY KEY (`idparameter`) ,
  INDEX `fk_parameter_predictor2` (`predictor_idpredict` ASC) ,
  INDEX `fk_parameter_events2` (`event_idevent` ASC) ,
  UNIQUE INDEX `uuid_UNIQUE` (`param_uuid` ASC) ,
  CONSTRAINT `fk_parameter_predictor1`
    FOREIGN KEY (`predictor_idpredict` )
    REFERENCES `robustwp1`.`predictor` (`idpredictor` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_parameter_events1`
    FOREIGN KEY (`event_idevent` )
    REFERENCES `robustwp1`.`event` (`idevents` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`condition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`condition` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`condition` (
  `idcondition` INT NOT NULL AUTO_INCREMENT ,
  `event_idevents` INT NOT NULL ,
  `cond_uuid` VARCHAR(45) NULL ,
  `cond_value_type` VARCHAR(45) NULL ,
  `cond_name` VARCHAR(250) NULL ,
  `cond_desc` VARCHAR(250) NULL ,
  `cond_unit` VARCHAR(45) NULL ,
  `values_allowed_type` VARCHAR(45) NULL ,
  PRIMARY KEY (`idcondition`) ,
  INDEX `fk_condition_events1` (`event_idevents` ASC) ,
  UNIQUE INDEX `uuid_UNIQUE` (`cond_uuid` ASC) ,
  CONSTRAINT `fk_condition_events1`
    FOREIGN KEY (`event_idevents` )
    REFERENCES `robustwp1`.`event` (`idevents` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`constraint`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`constraint` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`constraint` (
  `idconstraint` INT NOT NULL AUTO_INCREMENT ,
  `cons_value` VARCHAR(45) NULL ,
  `cons_type` VARCHAR(45) NULL ,
  `parameter_idparameter` INT NULL DEFAULT NULL ,
  `condition_idcondition` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`idconstraint`) ,
  INDEX `fk_constraint_parameter2` (`parameter_idparameter` ASC) ,
  INDEX `fk_constraint_condition1` (`condition_idcondition` ASC) ,
  CONSTRAINT `fk_constraint_parameter1`
    FOREIGN KEY (`parameter_idparameter` )
    REFERENCES `robustwp1`.`parameter` (`idparameter` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_constraint_condition1`
    FOREIGN KEY (`condition_idcondition` )
    REFERENCES `robustwp1`.`condition` (`idcondition` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`conditioninstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`conditioninstance` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`conditioninstance` (
  `idconditioninstance` INT NOT NULL AUTO_INCREMENT ,
  `pre_value` VARCHAR(45) NULL ,
  `pre_val_eval_type` VARCHAR(45) NULL ,
  `pos_value` VARCHAR(45) NULL ,
  `pos_val_eval_type` VARCHAR(45) NULL ,
  `condition_idcondition` INT NOT NULL ,
  `riskop_idriskop` INT NOT NULL ,
  PRIMARY KEY (`idconditioninstance`) ,
  INDEX `fk_conditioninstance_condition1` (`condition_idcondition` ASC) ,
  INDEX `fk_conditioninstance_riskop1` (`riskop_idriskop` ASC) ,
  CONSTRAINT `fk_conditioninstance_condition1`
    FOREIGN KEY (`condition_idcondition` )
    REFERENCES `robustwp1`.`condition` (`idcondition` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_conditioninstance_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`resultitem`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`resultitem` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`resultitem` (
  `idresultitem` INT NOT NULL AUTO_INCREMENT ,
  `evaluationresult_idevalresults` INT NOT NULL ,
  `name` VARCHAR(150) NULL ,
  `prob` DOUBLE NULL ,
  PRIMARY KEY (`idresultitem`) ,
  INDEX `fk_resultitem_evaluationResult1` (`evaluationresult_idevalresults` ASC) ,
  CONSTRAINT `fk_resultitem_evaluationResult1`
    FOREIGN KEY (`evaluationresult_idevalresults` )
    REFERENCES `robustwp1`.`evaluationresult` (`idevalresults` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`procid`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`procid` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`procid` (
  `idprocid` INT NOT NULL AUTO_INCREMENT ,
  `procid` VARCHAR(45) NOT NULL ,
  `riskop_idriskop` INT NOT NULL ,
  PRIMARY KEY (`idprocid`) ,
  INDEX `fk_procid_riskop1` (`riskop_idriskop` ASC) ,
  CONSTRAINT `fk_procid_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `robustwp1`.`paraminstance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `robustwp1`.`paraminstance` ;

CREATE  TABLE IF NOT EXISTS `robustwp1`.`paraminstance` (
  `paraminstance_value` VARCHAR(45) NOT NULL ,
  `paraminstance_eval_type` VARCHAR(45) NULL ,
  `parameter_idparameter` INT NOT NULL ,
  `riskop_idriskop` INT NOT NULL ,
  INDEX `fk_paraminstance_parameter1_idx` (`parameter_idparameter` ASC) ,
  INDEX `fk_paraminstance_riskop1_idx` (`riskop_idriskop` ASC) ,
  CONSTRAINT `fk_paraminstance_parameter1`
    FOREIGN KEY (`parameter_idparameter` )
    REFERENCES `robustwp1`.`parameter` (`idparameter` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_paraminstance_riskop1`
    FOREIGN KEY (`riskop_idriskop` )
    REFERENCES `robustwp1`.`riskop` (`idriskop` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

USE `robustwp1` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
