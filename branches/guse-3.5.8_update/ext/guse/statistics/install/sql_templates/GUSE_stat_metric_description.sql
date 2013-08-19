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
-- Table structure for table `stat_metric_description`
--

DROP TABLE IF EXISTS `stat_metric_description`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stat_metric_description` (
  `column_name` varchar(255) DEFAULT NULL,
  `pretty_name` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `units` varchar(255) DEFAULT NULL,
  `percision` int(11) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `forlevel` varchar(255) DEFAULT NULL,
  `statetype` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


INSERT INTO `stat_metric_description` (`column_name`, `pretty_name`, `category`, `units`, `percision`, `source`, `forlevel`, `statetype`, `id`) VALUES
('', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1),
('Average', 'Job Instance Average Execution Time', '3', 's', 1, 'stat_statistics', 'all', NULL, 2),
('delta', 'Workflow Instance Execution Time', '6', 's', 1, 'stat_WorkflowInstance', 'workflowinstance', NULL, 3),
('FailureRate', 'Failure Rate', '1', '%', 3, 'stat_statistics', 'all', NULL, 4),
('NumFailedJobs', 'Total Number of Failed Job Instances', '1', 'jobs', 0, 'stat_statistics', 'all', NULL, 5),
('NumJobs', 'Total Number of Job Instances', '3', 'jobs', 0, 'stat_statistics', 'all', NULL, 6),
('StdDev', 'Standard Deviation of Job Average Execution Time', '3', 's', 3, 'stat_statistics', 'all', NULL, 7),
('TotalJobTime', 'Total Running Time', '0', 's', 1, 'stat_statistics', 'all', NULL, 8),
('Average', 'Average Time Spent in the Failed Run State', '2', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAILEDRUN', 10),
('Average', 'Average Time Spent in the Queue State', '2', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'QUEUE', 11),
('Average', 'Average Time Spent in the Portal State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'PORTAL', 12),
('Average', 'Average Time Spent in the Terminal State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'TERMINAL', 13),
('Average', 'Average Time Spent in the Fail State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAIL', 14),
('Average', 'Average Time Spent in the Run State', '2', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'SUCCESSRUN', 15),
('Average', 'Average Time Spent in the Other State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'OTHER', 16),
('StdDev', 'Standard Deviation of Time Spent in the Failed Run State', '4', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAILEDRUN', 17),
('StdDev', 'Standard Deviation of Time Spent in the Queue State', '4', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'QUEUE', 18),
('StdDev', 'Standard Deviation of Time Spent in the Portal State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'PORTAL', 19),
('StdDev', 'Standard Deviation of Time Spent in the Terminal State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'TERMINAL', 20),
('StdDev', 'Standard Deviation of Time Spent in the Fail State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAIL', 21),
('StdDev', 'Standard Deviation of Time Spent in the Run State', '4', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'SUCCESSRUN', 22),
('StdDev', 'Standard Deviation of Time Spent in the Other State', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'OTHER', 23),
('Num', 'Number of Times the Job Entered the Failed Run State', '5', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'FAILEDRUN', 24),
('Num', 'Number of Times the Job Entered the Queue State', '5', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'QUEUE', 25),
('Num', 'Number of Times the Job Entered the Portal State', '0', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'PORTAL', 26),
('Num', 'Number of Times the Job Entered the Terminal State', '0', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'TERMINAL', 27),
('Num', 'Number of Times the Job Entered the Fail State', '0', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'FAIL', 28),
('Num', 'Number of Times the Job Entered the Run State', '5', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'SUCCESSRUN', 29),
('Num', 'Number of Times the Job Entered the Other State', '0', 'entries', 0, 'stat_JobStateTypeStatistics', 'all', 'OTHER', 30),
('FailureRate', 'Failure Rate', '8', '%', 3, 'stat_statistics', 'all', NULL, 31),
('TotalTimeInStates', 'Failed Run', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAILEDRUN', 34),
('TotalTimeInStates', 'Run', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'SUCCESSRUN', 35),
('TotalTimeInStates', 'Queue', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'QUEUE', 36),
('TotalTimeInStates', 'Portal', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'PORTAL', 37),
('TotalTimeInStates', 'Terminal', '0', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'TERMINAL', 38),
('TotalTimeInStates', 'Fail', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'FAIL', 39),
('TotalTimeInStates', 'Other', '7', 's', 2, 'stat_JobStateTypeStatistics', 'all', 'OTHER', 40),
('average', 'Average Workflow Execution Time', '6', 's', 2, 'stat_ConcreteWorkflow', 'concreteworkflow', NULL, 41);

-- Dump completed on 2011-04-21 14:31:09
