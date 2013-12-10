-- MySQL dump 10.13  Distrib 5.1.67, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: cliqueboardsie
-- ------------------------------------------------------
-- Server version	5.1.67-0ubuntu0.11.10.1
--
--/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
--/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
--/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
--/*!40101 SET NAMES utf8 */;
--/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
--/*!40103 SET TIME_ZONE='+00:00' */;
--/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
--/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
--/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
--/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table forums
--

DROP TABLE IF EXISTS forums;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE forums (
  forumid bigint NOT NULL DEFAULT '0',
  title varchar(75) DEFAULT NULL,
  parentid bigint DEFAULT '0',
  PRIMARY KEY (forumid)
);
--ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table forums
--

--LOCK TABLES forums WRITE;
--/*!40000 ALTER TABLE forums DISABLE KEYS */;
INSERT INTO forums VALUES (15,'Test',0),(3,'Rec',0),(4,'Games',0),(5,'Tech',0),(6,'adverts.ie',81),(7,'After Hours',3),(8,'Sports',42),(9,'Computers &amp; Technology',907),(10,'Work &amp; Jobs',857)
--/*!40000 ALTER TABLE forums ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table nostartpostthreads
--

DROP TABLE IF EXISTS nostartpostthreads;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE nostartpostthreads (
  threadid bigint NOT NULL,
  PRIMARY KEY (threadid)
);
-- ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table nostartpostthreads
--

--LOCK TABLES nostartpostthreads WRITE;
--/*!40000 ALTER TABLE nostartpostthreads DISABLE KEYS */;
INSERT INTO nostartpostthreads VALUES (24024),(25418),(27720),(30786),(30796),(30914),(31000),(31002),(31055),(31108),(31118),(31186),(31213),(31384),(31883),(32026),(32027),(32066),(32692),(32732),(32931),(33052),(33189),(33334),(33382),(33383),(33387),(33463),(33573),(33724),(34017),(34465)
--/*!40000 ALTER TABLE nostartpostthreads ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table posts
--

DROP TABLE IF EXISTS posts;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE posts (
  postid bigint NOT NULL,
  threadid bigint NOT NULL,
  userid bigint NOT NULL,
  title varchar(75) DEFAULT '',
  posteddate datetime NOT NULL,
  content blob(1000),
  isop int NOT NULL DEFAULT '0',
  PRIMARY KEY (postid),
--  KEY userid (userid),
--  KEY threadid (threadid),
--  KEY idx_posteddate (posteddate),
--  KEY posts_posteddate (posteddate),
--  KEY posts_threadid (threadid)
);
-- ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table posts
--

--LOCK TABLES posts WRITE;
--/*!40000 ALTER TABLE posts DISABLE KEYS */;
INSERT INTO posts VALUES
(3,17048,2680,'Test post','2001-04-24 16:49:00',HEXTORAW('11111111'),1),
(138495,17048,2680,'Last level let down','2001-04-24 16:49:00',HEXTORAW('1111111111'),1),
(138516,17051,313,'','2001-05-06 01:44:00',HEXTORAW('1111'),0),
(138515,17051,1250,'','2001-05-05 22:34:00',HEXTORAW('123456'),0),
(138507,17049,2680,'TFC Lucky Dip 12:00 Tonight (Saturday)','2001-05-05 22:30:00',HEXTORAW('123123'),1),
(138514,17051,2555,'','2001-05-05 22:12:00',HEXTORAW('312312'),0),
(138513,17051,313,'','2001-05-05 21:57:00',HEXTORAW('312123'),0),
(15,4,364,'new board......ta cloud hun:)','1998-12-04 15:11:00',HEXTORAW('312312'),1),
(16,5,121,'Stuff','1998-12-04 18:23:00',HEXTORAW('32123321'),1),
(17,6,292,'MY PROBLEM','1998-12-04 18:36:00',HEXTORAW('332112'),1),
(18,6,10,'','1998-12-04 18:41:00',HEXTORAW('3213'),0),
(19,7,10,'Its lacks Spice','1998-12-04 18:34:00',HEXTORAW('1233'),1),
(20,7,364,'','1998-12-04 19:50:00',HEXTORAW('312312'),0),
(21,7,10,'','1998-12-07 09:23:00',HEXTORAW('23213122'),0),
(22,7,10,'Its lacks Spice','1998-12-04 18:34:00',HEXTORAW('1233'),1),
(23,7,364,'','1998-12-04 19:50:00',HEXTORAW('312312'),0),
(24,7,10,'','1998-12-07 09:23:00',HEXTORAW('23213122'),0),
(25,7,364,'','1998-12-04 19:50:00',HEXTORAW('312312'),0),
(26,3123123,10,'','1998-12-07 09:23:00',HEXTORAW('23213122'),0)
;
--/*!40000 ALTER TABLE posts ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table replies
--

DROP TABLE IF EXISTS replies;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE replies (
  origpostid bigint NOT NULL,
  replyingpostid bigint NOT NULL,
--  UNIQUE KEY origpostid (origpostid,replyingpostid),
--  KEY replyingpostid (replyingpostid),
--  KEY origpostid_idx (origpostid)
);
--ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table replies
--

--LOCK TABLES replies WRITE;
--/*!40000 ALTER TABLE replies DISABLE KEYS */;
INSERT INTO replies VALUES (138495,3),(3,138495),(3,55120216),(15,1544447),(15,1544466),(15,1544474),(15,1544481),(15,1544483),(15,1544489),(15,1544496),(15,1544502),(16,1545078),(16,1545373),(16,1545431),(16,1545463),(16,1545466),(16,1545487),(16,1545508),(16,1545509),(16,1545512),(16,1545529),(16,1545560),(16,1545632),(16,1545770),(16,1546541),(16,1547142),(17,18),(17,651502),(19,20),(19,21),(21,2152487),(21,2373503),(22,23),(23,2373521),(24,25),(24,26),(24,27),(24,28),(24,29),(24,30),(24,31),(24,32),(24,33),(24,34),(24,35),(24,2454376),(35,2152494),(35,2449907),(36,37),(36,38),(37,50360925),(38,50444291),(39,40),(39,41),(39,50360923),(39,50379036),(39,50379047),(41,50891667),(41,51049329),(42,43),(42,50379042),(43,2152492),(43,51093944),(44,2152498),(44,50379025),(44,50379031),(44,50379138),(44,51163037),(44,51163099),(44,51163689),(45,46),(45,47),(45,48),(45,3216230),(45,50360920),(45,50444514),(48,50444307),(49,50),(49,50360918),(50,51525172),(51,921961),(51,921983),(51,922006),(51,922024),(51,922139),(51,922167),(51,
922172),(51,922188),(51,922697);
--/*!40000 ALTER TABLE replies ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table threads
--

DROP TABLE IF EXISTS threads;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE threads (
  threadid bigint NOT NULL,
  forumid bigint NOT NULL,
  userid bigint NOT NULL,
  title varchar(75) DEFAULT NULL,
  createddate datetime NOT NULL,
  views int NOT NULL DEFAULT '0',
  PRIMARY KEY (threadid),
--  KEY forumid (forumid),
--  KEY userid (userid),
--  KEY threads_forumid (forumid)
);
-- ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table threads
--

--LOCK TABLES threads WRITE;
--/*!40000 ALTER TABLE threads DISABLE KEYS */;
INSERT INTO threads VALUES
(17048,15,836,'Test message','2001-05-05 22:30:00',6),
(17049,15,836,'TFC Lucky Dip 12:00 Tonight (Saturday)','2001-05-05 22:30:00',6),
(17052,15,313,'What the fup?','2001-05-06 16:18:00',12),
(17050,15,2555,'Old Quake 2 mod / DOD','2001-05-06 11:42:00',20),
(17051,15,2555,'Old Quake 2 mod / DOD','2001-05-06 11:42:00',20),
(4,7,364,'new board......ta cloud hun:)','1998-12-04 15:11:00',1116),
(5,7,121,'Stuff','1998-12-04 18:23:00',439),
(6,7,292,'MY PROBLEM','1998-12-04 18:36:00',228),
(7,7,10,'Its lacks Spice','1998-12-04 18:34:00',524),
(8,52,408,'MY PROBLEM(s)','1998-12-05 20:26:00',171),
(9,7,442,'ewwo!','1998-12-04 15:18:00',574),
(10,7,346,'MY PROBLEM','1998-12-10 09:12:00',280),
(11,7,294,'Webpage Updated','1998-12-11 00:35:00',370),
(12,7,136,'Brand Spanking New and Bright as a Button.....','1998-12-18 13:52:00',315),
(13,7,434,'nothing really','1998-12-25 17:22:00',291),
(14,7,350,'my love has left me....','1998-12-24 23:48:00',529);
--,(17,7,364,'ah cloud where all my posts gone????
--???','1999-01-09 23:28:00',636),(18,7,350,'i cant stop posting to this stoopid board','1999-01-06 03:36:00',264),(19,7,385,'my problem','1999-01-10 20:06:00',441),(20,7,210,'I&#039;m BACK !!!!!!!!!','1999-01-14 19:06:00',576),(21,7,401,'the fall of the milk bottle','1999-01-14 18:18:00',457),(68206,53,7257,'Var Webby?','2002-10-28 12:42:57',148),(68204,12,8433,'Xbox and controls ???','2002-10-28 12:06:51',244),(23,7,364,'posts!','1999-01-24 22:51:00',360),(24,7,364,'Brian Kennedy - jasus hes good he is !','1999-01-22 13:16:00',457),(25,7,454,'How to score with Girls?','1999-01-20 15:56:00',263),(26,7,388,'adwa I wub u','1999-01-27 18:56:00',122),(27,7,186,'college sucks :(','1999-01-29 15:52:00',89),(28,7,350,'hi carol','1999-01-29 08:20:00',165),(29,7,364,'New Conversation For Board!','1999-01-25 17:55:00',495),(30,7,374,'Feeling Depressed','1999-02-02 18:53:00',93),(31,7,61,'God dam college god dam fire dam wall DAM!','1999-02-03 10:51:00',148),(32,7,136,'What&#039;s the deffinition of confidence???','1999-
--02-08 17:44:00',95)
--;

--/*!40000 ALTER TABLE threads ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table unclassposts
--

DROP TABLE IF EXISTS unclassposts;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;
CREATE TABLE unclassposts (
  postid bigint NOT NULL
);
-- ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table unclassposts
--

--LOCK TABLES unclassposts WRITE;
--/*!40000 ALTER TABLE unclassposts DISABLE KEYS */;

INSERT INTO unclassposts VALUES (107),(398),(707),(875),(991),(1224),(1328),(1678),(1790),(1803),(1997),(2283),(2368),(2486),(2600),(2908),(3058),(4175);
--/*!40000 ALTER TABLE unclassposts ENABLE KEYS */;
--UNLOCK TABLES;

--
-- Table structure for table users
--

DROP TABLE IF EXISTS users;
--/*!40101 SET @saved_cs_client     = @@character_set_client */;
--/*!40101 SET character_set_client = utf8 */;

CREATE TABLE users (
  userid bigint NOT NULL,
  username varchar(30) DEFAULT NULL,
  usertitle varchar(30) NOT NULL,
  joindate datetime NOT NULL,
  PRIMARY KEY (userid)
);
-- ENGINE=MyISAM DEFAULT CHARSET=latin1;
--/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table users
--

--LOCK TABLES users WRITE;
--/*!40000 ALTER TABLE users DISABLE KEYS */;
INSERT INTO users VALUES (313,'counter','Registered User','2003-12-11 19:26:35'),(2680,'testuser','Registered User','2006-12-24 19:26:35'),(92402,'kenco','Registered User','2006-12-24 19:26:35'),(92403,'daneois41','Banned','2006-12-24 19:31:14'),(92404,'Tony3004','Registered User','2006-12-24 19:46:15'),(92407,'mayflower1870','Registered User','2006-12-24 21:05:51'),(92406,'philng','Registered User','2006-12-24 20:36:05'),(92408,'coynie1966','Registered User','2006-12-24 21:17:05'),(92409,'bartjacobs','Banned','2006-12-24 23:01:14'),(92410,'cpt-howdy','Registered User','2006-12-24 23:06:49'),(92411,'notoriousboc','Registered User','2006-12-24 23:36:57'),(92412,'LUCKY5','Banned','2006-12-24 23:45:46');
--/*!40000 ALTER TABLE users ENABLE KEYS */;
--UNLOCK TABLES;
--/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
--
--/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
--/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
--/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
--/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
--/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
--/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
--/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-02-28 18:40:39
