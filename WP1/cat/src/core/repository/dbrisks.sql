DROP DATABASE IF EXISTS activiti;
DROP USER 'catActiviti'@'localhost';
create database activiti;
create user 'catActiviti'@'localhost' IDENTIFIED BY 'password';
grant all privileges on activiti.* to catActiviti@localhost;

CREATE DATABASE  IF NOT EXISTS `robustwp1` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `robustwp1`;
-- MySQL dump 10.13  Distrib 5.6.11, for Win64 (x86_64)
--
-- Host: localhost    Database: robustwp1
-- ------------------------------------------------------
-- Server version	5.6.11-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `community`
--

DROP TABLE IF EXISTS `community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `community` (
  `idcommunity` int(11) NOT NULL AUTO_INCREMENT,
  `com_name` varchar(250) NOT NULL,
  `com_uuid` varchar(150) DEFAULT NULL,
  `com_uri` varchar(250) DEFAULT NULL,
  `com_isStream` tinyint(1) DEFAULT NULL,
  `com_id` varchar(250) DEFAULT NULL,
  `com_streamName` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idcommunity`),
  UNIQUE KEY `name_UNIQUE` (`com_name`),
  UNIQUE KEY `uuid_UNIQUE` (`com_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community`
--

LOCK TABLES `community` WRITE;
/*!40000 ALTER TABLE `community` DISABLE KEYS */;
INSERT INTO `community` VALUES (1,'Nihongo (Japanese)','967048ff-86bb-4142-bf18-8cc1067ce1a2',NULL,0,'224',NULL),(2,'World of Warcraft','bf196089-b561-4ad5-b2ff-9eef25327b2c',NULL,0,'524',NULL),(3,'Prime Time Cartoons','1357257f-4746-4562-94fe-c7762f9b839c',NULL,0,'512',NULL),(4,'After Hours','ecee4c1d-f642-4a75-83f0-f0db409a2615',NULL,0,'7',NULL);
/*!40000 ALTER TABLE `community` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `condition`
--

DROP TABLE IF EXISTS `condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `condition` (
  `idcondition` int(11) NOT NULL AUTO_INCREMENT,
  `event_idevents` int(11) NOT NULL,
  `cond_uuid` varchar(45) DEFAULT NULL,
  `cond_value_type` varchar(45) DEFAULT NULL,
  `cond_name` varchar(250) DEFAULT NULL,
  `cond_desc` varchar(250) DEFAULT NULL,
  `cond_unit` varchar(45) DEFAULT NULL,
  `values_allowed_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idcondition`),
  UNIQUE KEY `uuid_UNIQUE` (`cond_uuid`),
  KEY `fk_condition_events1` (`event_idevents`),
  CONSTRAINT `fk_condition_events1` FOREIGN KEY (`event_idevents`) REFERENCES `event` (`idevents`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `condition`
--

LOCK TABLES `condition` WRITE;
/*!40000 ALTER TABLE `condition` DISABLE KEYS */;
INSERT INTO `condition` VALUES (1,1,'b0f8edb6-665e-403b-989f-7f140d10a8f5','FLOAT','Decrease in percentage of number of users in a role','Percentage change in total number of user',NULL,'SINGLE'),(2,2,'7408d872-eaf8-4afd-b8b7-f2ef3cb1ed36','FLOAT','Increase in percentage of number of users in a role','Percentage change in total number of users',NULL,'SINGLE'),(3,3,'cc3926ff-0108-4687-b299-c7aefc5cbc06','FLOAT','Number of Users below the specified value','Number of user below the specified value',NULL,'SINGLE'),(4,4,'f6be262d-8fa2-4395-8f8c-8db70f9b304d','FLOAT','Number of Users above the specified value','Number of user above the specified value',NULL,'SINGLE'),(5,5,'87084ba9-edac-4e79-b23a-76a14e52730e','FLOAT','Activity drop Threshold','',NULL,'SINGLE');
/*!40000 ALTER TABLE `condition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conditioninstance`
--

DROP TABLE IF EXISTS `conditioninstance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conditioninstance` (
  `idconditioninstance` int(11) NOT NULL AUTO_INCREMENT,
  `pre_value` varchar(45) DEFAULT NULL,
  `pre_val_eval_type` varchar(45) DEFAULT NULL,
  `pos_value` varchar(45) DEFAULT NULL,
  `pos_val_eval_type` varchar(45) DEFAULT NULL,
  `condition_idcondition` int(11) NOT NULL,
  `riskop_idriskop` int(11) NOT NULL,
  PRIMARY KEY (`idconditioninstance`),
  KEY `fk_conditioninstance_condition1` (`condition_idcondition`),
  KEY `fk_conditioninstance_riskop1` (`riskop_idriskop`),
  CONSTRAINT `fk_conditioninstance_condition1` FOREIGN KEY (`condition_idcondition`) REFERENCES `condition` (`idcondition`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_conditioninstance_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conditioninstance`
--

LOCK TABLES `conditioninstance` WRITE;
/*!40000 ALTER TABLE `conditioninstance` DISABLE KEYS */;
INSERT INTO `conditioninstance` VALUES (1,NULL,NULL,'20.0','GREATER_OR_EQUAL',2,1),(2,NULL,NULL,'-30.0','GREATER_OR_EQUAL',1,1),(3,NULL,NULL,'0.2','GREATER_OR_EQUAL',5,2);
/*!40000 ALTER TABLE `conditioninstance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `constraint`
--

DROP TABLE IF EXISTS `constraint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `constraint` (
  `idconstraint` int(11) NOT NULL AUTO_INCREMENT,
  `cons_value` varchar(45) DEFAULT NULL,
  `cons_type` varchar(45) DEFAULT NULL,
  `parameter_idparameter` int(11) DEFAULT NULL,
  `condition_idcondition` int(11) DEFAULT NULL,
  PRIMARY KEY (`idconstraint`),
  KEY `fk_constraint_parameter2` (`parameter_idparameter`),
  KEY `fk_constraint_condition1` (`condition_idcondition`),
  CONSTRAINT `fk_constraint_parameter1` FOREIGN KEY (`parameter_idparameter`) REFERENCES `parameter` (`idparameter`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_constraint_condition1` FOREIGN KEY (`condition_idcondition`) REFERENCES `condition` (`idcondition`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `constraint`
--

LOCK TABLES `constraint` WRITE;
/*!40000 ALTER TABLE `constraint` DISABLE KEYS */;
INSERT INTO `constraint` VALUES (1,'1','DEFAULT',1,NULL),(2,'1','MIN',1,NULL),(3,'52','MAX',1,NULL),(4,'1','STEP',1,NULL),(5,'-20','DEFAULT',NULL,1),(6,'-100','MIN',NULL,1),(7,'0','MAX',NULL,1),(8,'Lurker','DEFAULT',2,NULL),(9,'Inactive','VALUESALLOWED',2,NULL),(10,'Lurker','VALUESALLOWED',2,NULL),(11,'Contributor','VALUESALLOWED',2,NULL),(12,'Super User','VALUESALLOWED',2,NULL),(13,'Follower','VALUESALLOWED',2,NULL),(14,'BroadCaster','VALUESALLOWED',2,NULL),(15,'Daily User','VALUESALLOWED',2,NULL),(16,'Leader','VALUESALLOWED',2,NULL),(17,'Celebrity','VALUESALLOWED',2,NULL),(18,'Unmatched','VALUESALLOWED',2,NULL),(19,'20','DEFAULT',NULL,2),(20,'0','MIN',NULL,2),(21,'200','MAX',NULL,2),(22,'Lurker','DEFAULT',3,NULL),(23,'Inactive','VALUESALLOWED',3,NULL),(24,'Lurker','VALUESALLOWED',3,NULL),(25,'Contributor','VALUESALLOWED',3,NULL),(26,'Super User','VALUESALLOWED',3,NULL),(27,'Follower','VALUESALLOWED',3,NULL),(28,'BroadCaster','VALUESALLOWED',3,NULL),(29,'Daily User','VALUESALLOWED',3,NULL),(30,'Leader','VALUESALLOWED',3,NULL),(31,'Celebrity','VALUESALLOWED',3,NULL),(32,'Unmatched','VALUESALLOWED',3,NULL),(33,'10','DEFAULT',NULL,3),(34,'0','MIN',NULL,3),(35,'10000','MAX',NULL,3),(36,'Lurker','DEFAULT',4,NULL),(37,'Inactive','VALUESALLOWED',4,NULL),(38,'Lurker','VALUESALLOWED',4,NULL),(39,'Contributor','VALUESALLOWED',4,NULL),(40,'Super User','VALUESALLOWED',4,NULL),(41,'Follower','VALUESALLOWED',4,NULL),(42,'BroadCaster','VALUESALLOWED',4,NULL),(43,'Daily User','VALUESALLOWED',4,NULL),(44,'Leader','VALUESALLOWED',4,NULL),(45,'Celebrity','VALUESALLOWED',4,NULL),(46,'Unmatched','VALUESALLOWED',4,NULL),(47,'10','DEFAULT',NULL,4),(48,'0','MIN',NULL,4),(49,'10000','MAX',NULL,4),(50,'Lurker','DEFAULT',5,NULL),(51,'Inactive','VALUESALLOWED',5,NULL),(52,'Lurker','VALUESALLOWED',5,NULL),(53,'Contributor','VALUESALLOWED',5,NULL),(54,'Super User','VALUESALLOWED',5,NULL),(55,'Follower','VALUESALLOWED',5,NULL),(56,'BroadCaster','VALUESALLOWED',5,NULL),(57,'Daily User','VALUESALLOWED',5,NULL),(58,'Leader','VALUESALLOWED',5,NULL),(59,'Celebrity','VALUESALLOWED',5,NULL),(60,'Unmatched','VALUESALLOWED',5,NULL),(61,'12','DEFAULT',6,NULL),(62,'3','MIN',6,NULL),(63,'28','MAX',6,NULL),(64,'1','STEP',6,NULL),(65,'0','DEFAULT',7,NULL),(66,'0','MIN',7,NULL),(67,'100','MAX',7,NULL),(68,'0.2','DEFAULT',NULL,5),(69,'0','MIN',NULL,5),(70,'1','MAX',NULL,5);
/*!40000 ALTER TABLE `constraint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluationresult`
--

DROP TABLE IF EXISTS `evaluationresult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `evaluationresult` (
  `idevalresults` int(11) NOT NULL AUTO_INCREMENT,
  `riskop_idriskop` int(11) NOT NULL,
  `currentdate` varchar(150) NOT NULL,
  `uuid` varchar(150) NOT NULL,
  `forecastdate` date NOT NULL,
  `metadata` varchar(450) DEFAULT NULL,
  `jobref` varchar(120) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `requestdate` varchar(150) DEFAULT NULL,
  `startdate` varchar(150) DEFAULT NULL,
  `completiondate` varchar(150) DEFAULT NULL,
  `currentobs` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idevalresults`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`),
  KEY `fk_evaluationResult_riskop1` (`riskop_idriskop`),
  CONSTRAINT `fk_evaluationResult_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluationresult`
--

LOCK TABLES `evaluationresult` WRITE;
/*!40000 ALTER TABLE `evaluationresult` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluationresult` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `idevents` int(11) NOT NULL AUTO_INCREMENT,
  `event_uuid` varchar(45) NOT NULL,
  `event_title` varchar(150) NOT NULL,
  `event_desc` varchar(250) DEFAULT NULL,
  `predictor_idpredictor` int(11) NOT NULL,
  PRIMARY KEY (`idevents`),
  KEY `fk_events_predictor2` (`predictor_idpredictor`),
  CONSTRAINT `fk_events_predictor1` FOREIGN KEY (`predictor_idpredictor`) REFERENCES `predictor` (`idpredictor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (1,'ccce03cf-2df9-4dbd-bb39-ba5e341b5878','Decrease in percentage of user in a role','',1),(2,'23b37482-2a33-4144-be4f-7ddf3d7c9081','Increase in percentage of user in a role','',1),(3,'5d8e9d73-c751-481c-a805-55b380e22324','Number of user below a value','',1),(4,'a3fd457a-ee52-4117-91b0-a9d7ef3443e1','Number of user above a value','',1),(5,'e19b4722-4257-4a7c-a36c-970565ecb552','User activity drop','Users activity drop according to some percentage treshold',2);
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `impact`
--

DROP TABLE IF EXISTS `impact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `impact` (
  `idImpact` int(11) DEFAULT NULL,
  `riskop_idriskop` int(11) NOT NULL,
  `value` varchar(45) NOT NULL,
  `objective_idobjective` int(11) NOT NULL,
  PRIMARY KEY (`riskop_idriskop`,`objective_idobjective`),
  KEY `fk_Impact_riskop1` (`riskop_idriskop`),
  KEY `fk_Impact_objective1` (`objective_idobjective`),
  CONSTRAINT `fk_Impact_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_Impact_objective1` FOREIGN KEY (`objective_idobjective`) REFERENCES `objective` (`idobjective`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impact`
--

LOCK TABLES `impact` WRITE;
/*!40000 ALTER TABLE `impact` DISABLE KEYS */;
INSERT INTO `impact` VALUES (NULL,1,'NEG_MEDIUM',7),(NULL,1,'NEG_VHIGH',8),(NULL,2,'NEG_VHIGH',8);
/*!40000 ALTER TABLE `impact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `objective`
--

DROP TABLE IF EXISTS `objective`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `objective` (
  `idobjective` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(45) NOT NULL,
  `description` varchar(250) NOT NULL,
  `community_idcommunity` int(11) NOT NULL,
  `title` varchar(45) NOT NULL,
  PRIMARY KEY (`idobjective`),
  KEY `fk_objective_community1` (`community_idcommunity`),
  CONSTRAINT `fk_objective_community1` FOREIGN KEY (`community_idcommunity`) REFERENCES `community` (`idcommunity`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `objective`
--

LOCK TABLES `objective` WRITE;
/*!40000 ALTER TABLE `objective` DISABLE KEYS */;
INSERT INTO `objective` VALUES (1,'d48547f9-d465-4846-b1d5-84022270cb93','Knowledge sharing amongst the employees',1,'Knowledge sharing'),(2,'5db9d048-239f-456e-b48b-fc304f16bd5a','Diverse activity level to ensure participation by many memberss',1,'Diverse activity level'),(3,'0077a1be-9e08-44b7-9a73-492b20afeadd','Knowledge sharing amongst the employees',2,'Knowledge sharing'),(4,'1438533d-5a0a-4d21-9009-c69e8a47a239','Diverse activity level to ensure participation by many memberss',2,'Diverse activity level'),(5,'b33aecaa-4b58-495c-b9a7-5cfcd21719b9','Knowledge sharing amongst the employees',3,'Knowledge sharing'),(6,'d38f31e8-a4aa-4d33-9d86-780fa91849df','Diverse activity level to ensure participation by many memberss',3,'Diverse activity level'),(7,'1f51825a-558e-466d-94d5-593e5aa306ea','Very good quality content needed',4,'Quality of content'),(8,'b72edd00-b164-4342-8352-23c227d2e176','High activity level needed to ensure engagement of members',4,'High activity level');
/*!40000 ALTER TABLE `objective` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parameter`
--

DROP TABLE IF EXISTS `parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parameter` (
  `idparameter` int(11) NOT NULL AUTO_INCREMENT,
  `param_uuid` varchar(45) DEFAULT NULL,
  `predictor_idpredict` int(11) DEFAULT NULL,
  `event_idevent` int(11) DEFAULT NULL,
  `param_name` varchar(250) DEFAULT NULL,
  `param_desc` varchar(250) DEFAULT NULL,
  `param_unit` varchar(45) DEFAULT NULL,
  `param_value_type` varchar(45) DEFAULT NULL,
  `param_allowed_type` varchar(45) DEFAULT NULL,
  `param_value` varchar(45) DEFAULT NULL,
  `param_value_eval_type` varchar(45) DEFAULT NULL,
  `forecast_period_param` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idparameter`),
  UNIQUE KEY `uuid_UNIQUE` (`param_uuid`),
  KEY `fk_parameter_predictor2` (`predictor_idpredict`),
  KEY `fk_parameter_events2` (`event_idevent`),
  CONSTRAINT `fk_parameter_predictor1` FOREIGN KEY (`predictor_idpredict`) REFERENCES `predictor` (`idpredictor`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_parameter_events1` FOREIGN KEY (`event_idevent`) REFERENCES `event` (`idevents`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parameter`
--

LOCK TABLES `parameter` WRITE;
/*!40000 ALTER TABLE `parameter` DISABLE KEYS */;
INSERT INTO `parameter` VALUES (1,'37848b65-ea01-4ff6-a2ac-a3eb6d5d000e',1,NULL,'Forecast period','The available forecast period options','weeks','INT','SINGLE','1',NULL,1),(2,'966f1759-57d7-4be5-92ef-e28782e1f58e',NULL,1,'Roles for the use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(3,'dfd2e0ca-f747-49e2-8e13-57b94fc439fe',NULL,2,'Roles for the use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(4,'9f6b87fb-88b7-4566-9e6a-b602b62ac692',NULL,3,'Roles for the use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(5,'1589e427-a935-4d01-b7eb-2fc6e93f868e',NULL,4,'Roles for the use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(6,'75302132-65db-4c59-a8f0-d2c4ae702de3',2,NULL,'Number of historical snapshots','The total number of covariates that will be used in the GS','','INT','SINGLE',NULL,NULL,0),(7,'0dce1066-5c85-4fd0-a115-a5d66ddda6b7',2,NULL,'Activity filter threshold','The minimum number of posts a user should have posted in the prediction period to be included in the population','posts','INT','SINGLE',NULL,NULL,0),(8,'3eb18987-2e32-45aa-a90e-d590b7cdb5e7',2,NULL,'The forecast period, which specifies the size of the time window','The parameter that defines the total number of days for each covariate','weeks','FLOAT','SINGLE','1',NULL,1);
/*!40000 ALTER TABLE `parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paraminstance`
--

DROP TABLE IF EXISTS `paraminstance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paraminstance` (
  `paraminstance_value` varchar(45) NOT NULL,
  `paraminstance_eval_type` varchar(45) DEFAULT NULL,
  `parameter_idparameter` int(11) NOT NULL,
  `riskop_idriskop` int(11) NOT NULL,
  KEY `fk_paraminstance_parameter1_idx` (`parameter_idparameter`),
  KEY `fk_paraminstance_riskop1_idx` (`riskop_idriskop`),
  CONSTRAINT `fk_paraminstance_parameter1` FOREIGN KEY (`parameter_idparameter`) REFERENCES `parameter` (`idparameter`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_paraminstance_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paraminstance`
--

LOCK TABLES `paraminstance` WRITE;
/*!40000 ALTER TABLE `paraminstance` DISABLE KEYS */;
INSERT INTO `paraminstance` VALUES ('Lurker',NULL,3,1),('Contributor',NULL,2,1);
/*!40000 ALTER TABLE `paraminstance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `predictor`
--

DROP TABLE IF EXISTS `predictor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `predictor` (
  `idpredictor` int(11) NOT NULL AUTO_INCREMENT,
  `predic_uuid` varchar(45) NOT NULL,
  `predic_name` varchar(45) NOT NULL,
  `predic_version` varchar(45) DEFAULT NULL,
  `predic_desc` varchar(256) DEFAULT NULL,
  `predic_uri` varchar(256) DEFAULT NULL,
  `predic_svctargetnamespace` varchar(256) DEFAULT NULL,
  `predic_svcname` varchar(256) DEFAULT NULL,
  `predic_portname` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idpredictor`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predictor`
--

LOCK TABLES `predictor` WRITE;
/*!40000 ALTER TABLE `predictor` DISABLE KEYS */;
INSERT INTO `predictor` VALUES (1,'68afb02e-7e89-4e5e-96c2-473c85c4ea8e','CM User Role Predictor Service','1.0','CM Predictor service','http://robust-demo.softwaremind.pl/userRolePredictorService-1.3/service','http://cm.ps.robust.cormsis.soton.ac.uk/','PredictorServiceImplService','PredictorServiceImplPort'),(2,'a19b4722-3257-3b7c-c36c-970565ecb552','GS User Activity Predictor Service','1.0','This Service uses a GS to calculate the probability of users dropping in activity','http://robust-demo.softwaremind.pl/userActivityPredictorService-1.0/service','http://impl.ps.robust.itinnovation.soton.ac.uk/','PredictorServiceImplService','PredictorServiceImplPort');
/*!40000 ALTER TABLE `predictor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `procid`
--

DROP TABLE IF EXISTS `procid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `procid` (
  `idprocid` int(11) NOT NULL AUTO_INCREMENT,
  `procid` varchar(45) NOT NULL,
  `riskop_idriskop` int(11) NOT NULL,
  PRIMARY KEY (`idprocid`),
  KEY `fk_procid_riskop1` (`riskop_idriskop`),
  CONSTRAINT `fk_procid_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `procid`
--

LOCK TABLES `procid` WRITE;
/*!40000 ALTER TABLE `procid` DISABLE KEYS */;
/*!40000 ALTER TABLE `procid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resultitem`
--

DROP TABLE IF EXISTS `resultitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resultitem` (
  `idresultitem` int(11) NOT NULL AUTO_INCREMENT,
  `evaluationresult_idevalresults` int(11) NOT NULL,
  `name` varchar(150) DEFAULT NULL,
  `prob` double DEFAULT NULL,
  PRIMARY KEY (`idresultitem`),
  KEY `fk_resultitem_evaluationResult1` (`evaluationresult_idevalresults`),
  CONSTRAINT `fk_resultitem_evaluationResult1` FOREIGN KEY (`evaluationresult_idevalresults`) REFERENCES `evaluationresult` (`idevalresults`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resultitem`
--

LOCK TABLES `resultitem` WRITE;
/*!40000 ALTER TABLE `resultitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `resultitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `riskop`
--

DROP TABLE IF EXISTS `riskop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `riskop` (
  `idriskop` int(11) NOT NULL AUTO_INCREMENT,
  `community_idcommunity` int(11) NOT NULL,
  `uuid` varchar(45) NOT NULL,
  `state` varchar(45) NOT NULL DEFAULT 'INACTIVE',
  `title` varchar(150) DEFAULT NULL,
  `scope` varchar(45) DEFAULT NULL,
  `cat_review_freq` int(11) DEFAULT NULL,
  `cat_review_period` varchar(45) DEFAULT NULL,
  `admin_review_freq` int(11) DEFAULT NULL,
  `admin_review_period` varchar(45) DEFAULT NULL,
  `notification` tinyint(1) DEFAULT NULL,
  `owner` varchar(45) DEFAULT NULL,
  `type` tinyint(1) DEFAULT NULL,
  `group` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idriskop`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`),
  KEY `fk_riskop_community1` (`community_idcommunity`),
  CONSTRAINT `fk_riskop_community1` FOREIGN KEY (`community_idcommunity`) REFERENCES `community` (`idcommunity`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `riskop`
--

LOCK TABLES `riskop` WRITE;
/*!40000 ALTER TABLE `riskop` DISABLE KEYS */;
INSERT INTO `riskop` VALUES (1,4,'7fda609a-71ed-47e0-8b20-9280df7e774b','active','Undesirable role composition','community',1,'week',1,'week',1,'Risk manager',1,'Default group '),(2,4,'bc007862-8d67-4760-b06a-755f34c4254c','active','Drop in activity','community',1,'week',1,'week',1,'Risk manager',1,'Default group ');
/*!40000 ALTER TABLE `riskop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `treat_risk`
--

DROP TABLE IF EXISTS `treat_risk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treat_risk` (
  `uuid` varchar(250) NOT NULL,
  `riskop_idriskop` int(11) NOT NULL,
  `priority` float DEFAULT NULL,
  `title` varchar(250) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`uuid`,`riskop_idriskop`),
  KEY `fk_treat_risk_riskop1` (`riskop_idriskop`),
  CONSTRAINT `fk_treat_risk_riskop1` FOREIGN KEY (`riskop_idriskop`) REFERENCES `riskop` (`idriskop`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `treat_risk`
--

LOCK TABLES `treat_risk` WRITE;
/*!40000 ALTER TABLE `treat_risk` DISABLE KEYS */;
INSERT INTO `treat_risk` VALUES ('01ac6047-4f13-4959-aa52-a9b392edefb0',2,1,'Address drop in user activity','Use this treatment plan to address a drop in user activity within your community'),('96c83bf0-773d-4553-b891-84deefe2e779',1,1,'Improve Role Composition','A low number of active users might be caused by users feeling ignored when they want to discusss issues. This policy-based treatment includes simulating discussion thread order changes so that newest replies come first.');
/*!40000 ALTER TABLE `treat_risk` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-05-27 22:00:17
