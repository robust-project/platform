DROP DATABASE IF EXISTS activiti;
DROP USER 'catActiviti'@'localhost';
create database activiti;
create user 'catActiviti'@'localhost' IDENTIFIED BY 'password';
grant all privileges on activiti.* to catActiviti@localhost;

CREATE DATABASE  IF NOT EXISTS `robustwp1` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `robustwp1`;
-- MySQL dump 10.13  Distrib 5.6.10, for Win64 (x86_64)
--
-- Host: localhost    Database: robustwp1
-- ------------------------------------------------------
-- Server version	5.6.10-log

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
INSERT INTO `community` VALUES (1,'Smart Leadership Community ','c76ebc42-5b21-41c9-99f4-7f05b926ff0c','ibm.it-innovation.soton.ac.uk',0,'c76ebc42-5b21-41c9-99f4-7f05b926ff0c',NULL),(2,'Innovation Hub','6e7f3d22-8946-4b18-840a-02e184661448','ibm.it-innovation.soton.ac.uk',0,'6e7f3d22-8946-4b18-840a-02e184661448',NULL),(3,'B2B Community','dd3ce105-bec1-4e54-a832-e3498371fa9f','ibm.it-innovation.soton.ac.uk',0,'dd3ce105-bec1-4e54-a832-e3498371fa9f',NULL),(4,'HR Community','c64097aa-091a-4ffd-bb11-bb0725ed0537','ibm.it-innovation.soton.ac.uk',0,'c64097aa-091a-4ffd-bb11-bb0725ed0537',NULL);
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
INSERT INTO `condition` VALUES (1,1,'502114f8-39e3-46fe-bc76-04b908c632d1','FLOAT','Decrease in percentage of number of users in a role','Percentage change in total number of user',NULL,'SINGLE'),(2,2,'9b1d818c-673b-48ee-835b-1e0e28499d3a','FLOAT','Decrease in percentage of number of users in a role','Percentage change in total number of users',NULL,'SINGLE'),(3,3,'8af9b5f8-6a7c-4259-b707-bfa51803db00','FLOAT','Increase in percentage of number of users in a role','Percentage change in total number of users',NULL,'SINGLE'),(4,4,'74aa46e9-e20c-496e-adc5-9787c379f414','FLOAT','Number of Users below the specified value','Number of user below the specified value',NULL,'SINGLE'),(5,5,'5125beea-3813-4bbc-8905-40722b797c7e','FLOAT','Number of Users above the specified value','Number of user above the specified value',NULL,'SINGLE');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conditioninstance`
--

LOCK TABLES `conditioninstance` WRITE;
/*!40000 ALTER TABLE `conditioninstance` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `constraint`
--

LOCK TABLES `constraint` WRITE;
/*!40000 ALTER TABLE `constraint` DISABLE KEYS */;
INSERT INTO `constraint` VALUES (1,'1','DEFAULT',1,NULL),(2,'1','MIN',1,NULL),(3,'52','MAX',1,NULL),(4,'1','STEP',1,NULL),(5,'-10','DEFAULT',NULL,1),(6,'-100','MIN',NULL,1),(7,'0','MAX',NULL,1),(8,'Inactive','DEFAULT',2,NULL),(9,'Inactive','VALUESALLOWED',2,NULL),(10,'Lurker','VALUESALLOWED',2,NULL),(11,'Contributor','VALUESALLOWED',2,NULL),(12,'Super User','VALUESALLOWED',2,NULL),(13,'Follower','VALUESALLOWED',2,NULL),(14,'BroadCaster','VALUESALLOWED',2,NULL),(15,'Daily User','VALUESALLOWED',2,NULL),(16,'Leader','VALUESALLOWED',2,NULL),(17,'Celebrity','VALUESALLOWED',2,NULL),(18,'Unmatched','VALUESALLOWED',2,NULL),(19,'-10','DEFAULT',NULL,2),(20,'-100','MIN',NULL,2),(21,'0','MAX',NULL,2),(22,'Inactive','DEFAULT',3,NULL),(23,'Inactive','VALUESALLOWED',3,NULL),(24,'Lurker','VALUESALLOWED',3,NULL),(25,'Contributor','VALUESALLOWED',3,NULL),(26,'Super User','VALUESALLOWED',3,NULL),(27,'Follower','VALUESALLOWED',3,NULL),(28,'BroadCaster','VALUESALLOWED',3,NULL),(29,'Daily User','VALUESALLOWED',3,NULL),(30,'Leader','VALUESALLOWED',3,NULL),(31,'Celebrity','VALUESALLOWED',3,NULL),(32,'Unmatched','VALUESALLOWED',3,NULL),(33,'10','DEFAULT',NULL,3),(34,'0','MIN',NULL,3),(35,'1000','MAX',NULL,3),(36,'Inactive','DEFAULT',4,NULL),(37,'Inactive','VALUESALLOWED',4,NULL),(38,'Lurker','VALUESALLOWED',4,NULL),(39,'Contributor','VALUESALLOWED',4,NULL),(40,'Super User','VALUESALLOWED',4,NULL),(41,'Follower','VALUESALLOWED',4,NULL),(42,'BroadCaster','VALUESALLOWED',4,NULL),(43,'Daily User','VALUESALLOWED',4,NULL),(44,'Leader','VALUESALLOWED',4,NULL),(45,'Celebrity','VALUESALLOWED',4,NULL),(46,'Unmatched','VALUESALLOWED',4,NULL),(47,'10','DEFAULT',NULL,4),(48,'0','MIN',NULL,4),(49,'1000','MAX',NULL,4),(50,'Inactive','DEFAULT',5,NULL),(51,'Inactive','VALUESALLOWED',5,NULL),(52,'Lurker','VALUESALLOWED',5,NULL),(53,'Contributor','VALUESALLOWED',5,NULL),(54,'Super User','VALUESALLOWED',5,NULL),(55,'Follower','VALUESALLOWED',5,NULL),(56,'BroadCaster','VALUESALLOWED',5,NULL),(57,'Daily User','VALUESALLOWED',5,NULL),(58,'Leader','VALUESALLOWED',5,NULL),(59,'Celebrity','VALUESALLOWED',5,NULL),(60,'Unmatched','VALUESALLOWED',5,NULL),(61,'10','DEFAULT',NULL,5),(62,'0','MIN',NULL,5),(63,'1000','MAX',NULL,5),(64,'Inactive','DEFAULT',6,NULL),(65,'Inactive','VALUESALLOWED',6,NULL),(66,'Lurker','VALUESALLOWED',6,NULL),(67,'Contributor','VALUESALLOWED',6,NULL),(68,'Super User','VALUESALLOWED',6,NULL),(69,'Follower','VALUESALLOWED',6,NULL),(70,'BroadCaster','VALUESALLOWED',6,NULL),(71,'Daily User','VALUESALLOWED',6,NULL),(72,'Leader','VALUESALLOWED',6,NULL),(73,'Celebrity','VALUESALLOWED',6,NULL),(74,'Unmatched','VALUESALLOWED',6,NULL);
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
  `metadata` varchar(45) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
INSERT INTO `event` VALUES (1,'714c0d86-a828-4d1f-a9d4-c9335839b919','Decrease in percentage of user in role 1','',1),(2,'674b4887-ef25-49e7-9a3f-7b12af9fc788','Decrease in percentage of user in role 2','',1),(3,'54156207-7bd8-41d4-9923-22e4f331499b','Increase in percentage of user in a role','',1),(4,'4552723d-3c2a-46e5-a2dc-a31e6e4cee6e','Number of user below a value','',1),(5,'1552723d-3c2a-46e5-a2dc-a31e6e4cee6e','Number of user above a value','',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `objective`
--

LOCK TABLES `objective` WRITE;
/*!40000 ALTER TABLE `objective` DISABLE KEYS */;
INSERT INTO `objective` VALUES (1,'9ffceb31-a4ae-494a-9b01-80a4937e4edf','Knowledge sharing amongst the employees',1,'Knowledge sharing'),(2,'da0cd540-7ca1-4e42-91ba-01b08349c3b1','Diverse activity level to ensure participation by many members',1,'Diverse activity level'),(3,'3c6b2774-e77e-4ead-8b0a-3b62971e80ef','Usage of the platform should be easy and fruitful for employees',1,'Quality of experience'),(4,'1611beea-0bcf-4abd-bd4a-b2ecb1575903','Very good quality content needed',1,'Quality of content'),(5,'423756fa-e2b7-4f58-9042-973100041c42','High activity level needed to ensure engagement of members',1,'High activity level'),(6,'b2f55925-3f10-4df1-adc0-a1794951d9a4','Knowledge sharing amongst the employees',2,'Knowledge sharing'),(7,'e50f531a-1ebd-474a-bab7-3804a8a7901b','Diverse activity level to ensure participation by many members',2,'Diverse activity level'),(8,'bc070c0a-a735-41e7-8994-0954fc29aea0','Usage of the platform should be easy and fruitful for employees',2,'Quality of experience'),(9,'6c5692f8-ebd4-4068-94fc-5eee448280c5','Very good quality content needed',2,'Quality of content'),(10,'610ecfe0-25b1-49d9-b3cd-561fe4cae6c8','High activity level needed to ensure engagement of members',2,'High activity level'),(11,'9f8fd770-65c5-4cd9-ab82-aadf0e9bc409','Knowledge sharing amongst the employees',3,'Knowledge sharing'),(12,'9ddd6afc-7fbd-497d-b531-b7820b07e181','Diverse activity level to ensure participation by many members',3,'Diverse activity level'),(13,'abf2b159-4a41-4af8-9efc-177cb1b645ad','Usage of the platform should be easy and fruitful for employees',3,'Quality of experience'),(14,'0e6f2066-0409-41dd-b92d-8b9059d6ddcf','Very good quality content needed',3,'Quality of content'),(15,'59d6f4e0-0a73-456b-9773-04caf64ace44','High activity level needed to ensure engagement of members',3,'High activity level'),(16,'b3eb5fb6-9316-46a8-86d0-fb9e2d89e134','Knowledge sharing amongst the employees',4,'Knowledge sharing'),(17,'3ad30644-630f-402a-95cb-ae01a15dbe81','Diverse activity level to ensure participation by many members',4,'Diverse activity level'),(18,'17ec5e1e-f2f8-45db-bfda-937e0b2547b4','Usage of the platform should be easy and fruitful for employees',4,'Quality of experience'),(19,'32fc5fc4-d230-4252-bf1c-efaa39293d3b','Very good quality content needed',4,'Quality of content'),(20,'fa4df900-49b0-406d-aab9-ad65ba13360a','High activity level needed to ensure engagement of members',4,'High activity level');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parameter`
--

LOCK TABLES `parameter` WRITE;
/*!40000 ALTER TABLE `parameter` DISABLE KEYS */;
INSERT INTO `parameter` VALUES (1,'c77b8e84-c46c-46f3-bc7c-8a2e2e5c2e17',1,NULL,'Forecast period','The available forecast period options','weeks','INT','SINGLE','5','EQUAL',1),(2,'3cb9ee06-dc1d-47da-b566-7a24215e146e',NULL,1,'Roles for the IBM use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(3,'9ddf50ad-06b9-4fd1-98c0-46ef642bde10',NULL,2,'Roles for the IBM use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(4,'da670628-dafa-4d4b-9c40-b6fe8515ef39',NULL,3,'Roles for the IBM use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(5,'ea0c9578-db94-44cb-8f31-86409dd97264',NULL,4,'Roles for the IBM use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0),(6,'246ead0e-4715-4684-93bc-b79d0409d694',NULL,5,'Roles for the IBM use case','Insert the name of the role','','STRING','SINGLE',NULL,NULL,0);
/*!40000 ALTER TABLE `parameter` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predictor`
--

LOCK TABLES `predictor` WRITE;
/*!40000 ALTER TABLE `predictor` DISABLE KEYS */;
INSERT INTO `predictor` VALUES (1,'04da6df8-9699-4a33-a1ad-34050833600e','CM Predictor Service','1.0','CM Predictor service','http://localhost/predictorServiceCM-IBM-1.3/service','http://cm.ps.robust.cormsis.soton.ac.uk/','PredictorServiceImplService','PredictorServiceImplPort');
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
  `evaluationResult_idevalresults` int(11) NOT NULL,
  `name` varchar(150) DEFAULT NULL,
  `prob` double DEFAULT NULL,
  PRIMARY KEY (`idresultitem`),
  KEY `fk_resultitem_evaluationResult1` (`evaluationResult_idevalresults`),
  CONSTRAINT `fk_resultitem_evaluationResult1` FOREIGN KEY (`evaluationResult_idevalresults`) REFERENCES `evaluationresult` (`idevalresults`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `riskop`
--

LOCK TABLES `riskop` WRITE;
/*!40000 ALTER TABLE `riskop` DISABLE KEYS */;
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

-- Dump completed on 2013-04-03 11:08:23
