USE `<DATABASENAME_MODIFY>`;
-- MySQL dump 10.13  Distrib 5.1.34, for apple-darwin9.5.0 (i386)
--
-- Host: localhost    Database: GUSE
-- ------------------------------------------------------
-- Server version	5.5.10

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
-- Table structure for table `stat_running`
--

DROP TABLE IF EXISTS `stat_running`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stat_running` (
  `portalURL` varchar(255) COLLATE utf8_bin NOT NULL,
  `userID` varchar(255) COLLATE utf8_bin NOT NULL,
  `wfID` varchar(255) COLLATE utf8_bin NOT NULL,
  `wrtID` varchar(255) COLLATE utf8_bin NOT NULL,
  `jobName` varchar(255) COLLATE utf8_bin NOT NULL,
  `pid` varchar(255) COLLATE utf8_bin NOT NULL,
  `jobStatus` varchar(255) COLLATE utf8_bin NOT NULL,
  `wfStatus` varchar(255) COLLATE utf8_bin NOT NULL,
  `resource` varchar(255) COLLATE utf8_bin NOT NULL,
  `seq` varchar(255) COLLATE utf8_bin NOT NULL,
  `tim` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `entered` tinyint(1) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`<DATABASEUSER_MODIFY>`@`localhost`*/ /*!50003 TRIGGER `toJobInstance`
BEFORE INSERT ON `stat_running`
FOR EACH ROW
BEGIN


DECLARE Aterminal BOOL DEFAULT 0;

    
SET NEW.tim := CURRENT_TIMESTAMP;
if(NEW.entered = 0) then

  if(NEW.jobStatus = '6' OR 
    NEW.jobStatus = '7' OR 
    NEW.jobStatus = '9' OR 
    NEW.jobStatus = '11' OR 
    NEW.jobStatus = '17' OR 
    NEW.jobStatus = '21' OR 
    NEW.jobStatus = '25' OR 
    NEW.jobStatus = '99' ) then
     SET Aterminal := 1 ;
  end if;

 CALL CreateOrAddToJobInstance (
    NEW.wrtID,
    NEW.jobName ,
    NEW.pid ,
    NEW.wfID  ,
    NEW.userID  ,
    NEW.portalURL  ,
    NEW.resource  ,
    NEW.tim  ,
    NEW.jobStatus, 
    Aterminal, 
    NEW.wfStatus); 

SET NEW.entered = 1;
  



if(NEW.wfStatus = '6' OR 
    NEW.wfStatus = '7' OR 
    NEW.wfStatus = '9' OR 
    NEW.wfStatus = '11' OR 
    NEW.wfStatus = '17' OR 
    NEW.wfStatus = '21' OR 
    NEW.wfStatus = '25' OR 
    NEW.wfStatus = '99' ) then 
  
      
      UPDATE `stat_WorkflowInstance` 
        SET
        status= NEW.wfStatus,
        endTime = NEW.tim
      WHERE  `stat_WorkflowInstance`.`wrtID`=  NEW.wrtID;
   

end if;
END IF; 
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-04-21 14:31:08
