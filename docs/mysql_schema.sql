-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: ODE
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.12.04.1

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
-- Table structure for table `MSG_QUEUE`
--

DROP TABLE IF EXISTS `MSG_QUEUE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MSG_QUEUE` (
  `ID` int(11) NOT NULL,
  `MSG_TYPE` varchar(45) DEFAULT NULL,
  `REGION` varchar(45) DEFAULT NULL,
  `QUEUE_NAME` varchar(45) DEFAULT NULL,
  `QUEUE_CONN_FACT` varchar(45) DEFAULT NULL,
  `TARGET_ADDRESS` varchar(45) DEFAULT NULL,
  `TARGET_PORT` int(11) DEFAULT NULL,
  `WS_HOST` varchar(45) DEFAULT NULL,
  `WS_URL` varchar(75) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ODE_REGISTRATION`
--

DROP TABLE IF EXISTS `ODE_REGISTRATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ODE_REGISTRATION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AGENT_ID` varchar(45) DEFAULT NULL,
  `REG_TYPE` varchar(45) DEFAULT NULL,
  `REGION` varchar(45) DEFAULT NULL,
  `MSG_TYPE` varchar(45) DEFAULT NULL,
  `START_DATE` datetime DEFAULT NULL,
  `END_DATE` datetime DEFAULT NULL,
  `SUB_RECEIVE_ADDR` varchar(45) DEFAULT NULL,
  `SUB_RECEIVE_PORT` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=393 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RDE_ARCHIVE_ENTRY`
--

DROP TABLE IF EXISTS `RDE_ARCHIVE_ENTRY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RDE_ARCHIVE_ENTRY` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ARCHIVE_DATE` datetime DEFAULT NULL,
  `MSG_TYPE` varchar(45) DEFAULT NULL,
  `REGION` varchar(45) DEFAULT NULL,
  `METADATA_LOC` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `REGISTRATION`
--

DROP TABLE IF EXISTS `REGISTRATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `REGISTRATION` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DIALOG_ID` varchar(45) DEFAULT NULL,
  `SEQUENCE_ID` varchar(45) DEFAULT NULL,
  `GROUP_ID` varchar(45) DEFAULT NULL,
  `REQUEST_ID` varchar(45) DEFAULT NULL,
  `SUB_ADDRESS` varchar(45) DEFAULT NULL,
  `SUB_PORT` varchar(45) DEFAULT NULL,
  `SUB_PROTOCOL` varchar(5) DEFAULT NULL,
  `REG_TYPE` varchar(45) DEFAULT NULL,
  `START_DATE` datetime DEFAULT NULL,
  `END_DATE` datetime DEFAULT NULL,
  `SUB_ID` varchar(45) DEFAULT NULL,
  `MESSAGE_TYPES` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=853 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-17 15:00:25
