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
-- Dumping routines for database 'GUSE'
--
/*!50003 DROP PROCEDURE IF EXISTS `CreateOrAddToJobInstance` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`<DATABASEUSER_MODIFY>`@`localhost`*/ /*!50003 PROCEDURE `CreateOrAddToJobInstance`(

AwrtID varchar(255),
AjobName varchar(255),
Apid varchar(255),
AwfID varchar(255),
AuserID varchar(255),
AportalID varchar(255),
Aresource varchar(255),
Atime TIMESTAMP,
AjobState varchar(255),
Aterminal BOOL,
AwfStatus varchar(255))
BEGIN 

  (SELECT @jiID:=id, @count:=count(1) 
    INTO @jiID, @count
    FROM `stat_JobInstance`
    WHERE `wrtID` = AwrtID AND `jobName` = AjobName AND pid = Apid LIMIT 1);

   
   

if @count=0 THEN

    INSERT INTO `stat_JobInstance`
        (`wrtID`,
        `jobName`,
        `pid`,
        `wfID`,
        `userID`,
        `portalID`,
        `resource`,
        `startTime`, 
        `terminated`)
        VALUES
        (AwrtID,
        AjobName ,
        Apid,
        AwfID ,
        AuserID ,
        AportalID , Aresource,
        Atime , 
        0 
        );
    
     SET @jiID := (SELECT LAST_INSERT_ID());
     INSERT INTO `stat_JobInstanceStatus`
        (`jobInstanceId`,
        `jobState`,
        `startTime`, `endTime`)
        VALUES
        (@jiID,
        AjobState,
        Atime,Atime);
        
      INSERT INTO `stat_WorkflowInstance`      
      (`wrtID`, 
      `startTime`,
      `status`, `wfID`)
      VALUES
      (AwrtID,
      Atime,
      AwfStatus,

      AwfID)
      ON DUPLICATE KEY UPDATE 
      status=AwfStatus;

ELSEIF Aterminal = 0 then  

      if(AjobState='5') then 

        UPDATE `stat_JobInstance` SET `resource` = Aresource WHERE id = @jiID;

    end if;



   SELECT @prevSTATUSID :=`id`, @prevSTATUS := jobState
    INTO @prevSTATUSID,@prevSTATUS 
    FROM  `stat_JobInstanceStatus`
    WHERE `id`=
      (SELECT MAX(id) FROM `stat_JobInstanceStatus`  
        WHERE jobInstanceId = @jiID) 
	LIMIT 1;
 
    

    if @prevSTATUS = AjobState THEN

         UPDATE `stat_JobInstanceStatus`

            SET

            `endTime` = Atime

              WHERE  id =@prevSTATUSID ; 

    else

          UPDATE `stat_JobInstanceStatus`

             SET

             `endTime` = Atime

             WHERE  id =@prevSTATUSID ;



            INSERT INTO `stat_JobInstanceStatus`

                (`jobInstanceId`,

                `jobState`,

                `startTime`, `endTime`)

                VALUES

                (@jiID,

                AjobState,

                Atime,Atime);



    END IF; 



ELSE

     
   
    SELECT @prevSTATUSID :=`id`, @prevSTATUS := jobState
    INTO @prevSTATUSID,@prevSTATUS 
    FROM  `stat_JobInstanceStatus`
    WHERE `id`=
      (SELECT MAX(id) FROM `stat_JobInstanceStatus`  
        WHERE jobInstanceId = @jiID) 
	LIMIT 1;
 


    SET @prevSTATUS := (SELECT jobState FROM  `stat_JobInstanceStatus`

        WHERE id = @prevSTATUSID);

    if(@prevSTATUS = '5') THEN  

      UPDATE `stat_JobInstanceStatus`

        SET

        `endTime` = Atime,

        `jobState` = '55' 

        WHERE  id =@prevSTATUSID ;



    ELSE

      UPDATE `stat_JobInstanceStatus`

          SET

          `endTime` = Atime

          WHERE  id =@prevSTATUSID ;



  END IF;

    INSERT INTO `stat_JobInstanceStatus`

        (`jobInstanceId`,

        `jobState`,

        `startTime`, `endTime` )

        VALUES

        (@jiID,

        AjobState,

        Atime,Atime );

        

  UPDATE `stat_JobInstance`

        SET

        `endTime` = Atime,

        `terminated`= 1

        WHERE   id = @jiID ;





end if;



END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `JobInstanceToAggregateJob` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`<DATABASEUSER_MODIFY>`@`localhost`*/ /*!50003 PROCEDURE `JobInstanceToAggregateJob`( aJobInstanceID INT, aEndTime TIMESTAMP)
BEGIN
DECLARE AwrtID VARCHAR(255);
DECLARE Aresource VARCHAR(255);
DECLARE AjobName VARCHAR(255);
DECLARE Ajobstarttime TIMESTAMP;
DECLARE AaggregateJobID BIGINT;
 

	SELECT wrtID, resource, jobName, startTime
	INTO AwrtID,Aresource,AjobName,Ajobstarttime
	FROM stat_JobInstance
	WHERE stat_JobInstance.ID = aJobInstanceID;



SELECT  stat_AggregateJob.ID, @c:=count(1)
	INTO AaggregateJobID, @c
	 FROM stat_AggregateJob 
	WHERE stat_AggregateJob.wrtID = AwrtID 
    AND stat_AggregateJob.resource= Aresource 
    AND stat_AggregateJob.jobName = AjobName;

if @c = 0  then 

INSERT INTO  stat_AggregateJob 
          (
           Resource ,
           NumberOfJobs ,
           JobName ,
           StartTS ,
           EndTS ,
           wfID ,
           wrtID ,
           userID ,
           portalID ,
           squaresOfRunningTime 
          )   
	SELECT 
	 stat_JobInstance.resource ,
	1,
	 stat_JobInstance.jobName ,
	 stat_JobInstance.startTime ,
	 aEndTime,  
	 stat_JobInstance.wfID ,
	 stat_JobInstance.wrtID ,  
	 stat_JobInstance.userID , 
	 stat_JobInstance.portalID ,
	POW(TIMESTAMPDIFF(SECOND,
				 stat_JobInstance.startTime ,aEndTime),2)
	FROM  stat_JobInstance WHERE  stat_JobInstance.id  = aJobInstanceID;  

(SELECT LAST_INSERT_ID() INTO AaggregateJobID);

else 


UPDATE  stat_AggregateJob 
SET
	 stat_AggregateJob.NumberOfJobs = stat_AggregateJob.NumberOfJobs +1,
	 stat_AggregateJob.StartTS =LEAST( stat_AggregateJob.StartTS ,Ajobstarttime),
	 stat_AggregateJob.EndTS =GREATEST( stat_AggregateJob.EndTS ,aEndTime),
   stat_AggregateJob.squaresOfRunningTime  =    stat_AggregateJob.squaresOfRunningTime + POW(TIMESTAMPDIFF(SECOND, Ajobstarttime, aEndTime),2)
  WHERE  ID = AaggregateJobID;
   

end if;   

INSERT INTO stat_AggregateJobStatus
( jobstate , min , max , total , squares , num , stat_aggregateJob_ID ) 
SELECT 
 alias.jobState, alias.delta, alias.delta, alias.delta,alias.sq,1,AaggregateJobID FROM  
	 (SELECT   
 		 POW(TIMESTAMPDIFF(SECOND,   stat_JobInstanceStatus.startTime ,   stat_JobInstanceStatus.endTime ),2) as sq,
		 jobState, 
		 TIMESTAMPDIFF(SECOND,   stat_JobInstanceStatus.startTime ,   stat_JobInstanceStatus.endTime ) as delta 
 		FROM   stat_JobInstanceStatus   
 		WHERE  stat_JobInstanceStatus.jobInstanceId=aJobInstanceID)
 	  AS alias
ON DUPLICATE KEY 
UPDATE 
  stat_AggregateJobStatus.min      = LEAST( stat_AggregateJobStatus.min , VALUES( min )),
  stat_AggregateJobStatus.max      = GREATEST( stat_AggregateJobStatus.max , VALUES( max )),
  stat_AggregateJobStatus.total    =  stat_AggregateJobStatus.total +VALUES( total ),
  stat_AggregateJobStatus.squares  =  stat_AggregateJobStatus.squares +VALUES( squares ),
  stat_AggregateJobStatus.num      =  stat_AggregateJobStatus.num +VALUES( num );

 

    

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

-- Dump completed on 2011-04-21 14:31:17
