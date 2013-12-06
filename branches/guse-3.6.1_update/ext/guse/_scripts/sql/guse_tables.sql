--
-- MySQL dump 10.11
--

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
-- Table structure for table `ainput`
--

DROP TABLE IF EXISTS `ainput`;
CREATE TABLE `ainput` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `seq` int(11) NOT NULL default '0',
  `prejob` varchar(100) NOT NULL default '',
  `preoutput` varchar(100) NOT NULL default '',
  `txt` text NOT NULL,
  `x` varchar(7) NOT NULL default '',
  `y` varchar(7) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2610 DEFAULT CHARSET=latin1 COMMENT='Absztrakt inputleirasok';

--
-- Table structure for table `ajob`
--

DROP TABLE IF EXISTS `ajob`;
CREATE TABLE `ajob` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `id_aworkflow` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `txt` text NOT NULL,
  `x` varchar(7) NOT NULL default '',
  `y` varchar(7) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2382 DEFAULT CHARSET=latin1 COMMENT='Job deffiniciok';

--
-- Table structure for table `aoutput`
--

DROP TABLE IF EXISTS `aoutput`;
CREATE TABLE `aoutput` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `seq` varchar(100) NOT NULL default '',
  `txt` text NOT NULL,
  `x` varchar(7) NOT NULL default '0',
  `y` varchar(7) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1456 DEFAULT CHARSET=latin1 COMMENT='Jobokhoz tartozo output definiciok';

--
-- Table structure for table `aworkflow`
--

DROP TABLE IF EXISTS `aworkflow`;
CREATE TABLE `aworkflow` (
  `id` bigint(20) NOT NULL auto_increment,
  `id_portal` varchar(255) collate utf8_bin NOT NULL default '',
  `id_user` varchar(255) collate utf8_bin NOT NULL default '',
  `name` varchar(255) collate utf8_bin NOT NULL default '',
  `txt` text collate utf8_bin NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=503 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Abstract workflow deffinicio';

--
-- Table structure for table `error_prop`
--

DROP TABLE IF EXISTS `error_prop`;
CREATE TABLE `error_prop` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `jobname` varchar(255) NOT NULL default '',
  `id_port` varchar(255) NOT NULL default '',
  `id_error` varchar(255) NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Hibas workflow config uzenetek';

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `id_workflow` bigint(20) NOT NULL default '0',
  `id_ajob` bigint(20) NOT NULL default '0',
  `user` varchar(255) NOT NULL default '',
  `port` varchar(3) NOT NULL default '',
  `dat` varchar(50) NOT NULL default '',
  `mdyid` varchar(255) NOT NULL default '',
  `ovalue` text NOT NULL,
  `nvalue` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='konkret workflow konfiguracios valtozasok';

--
-- Table structure for table `input_prop`
--

DROP TABLE IF EXISTS `input_prop`;
CREATE TABLE `input_prop` (
  `id_workflow` bigint(20) default '0',
  `id_ainput` bigint(20) unsigned default '0',
  `name` varchar(255) default '',
  `value` text,
  `label` text NOT NULL,
  `desc` text NOT NULL,
  `inh` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `job_desc`
--

DROP TABLE IF EXISTS `job_desc`;
CREATE TABLE `job_desc` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `value` text NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Jobleiro';

--
-- Table structure for table `job_outputs`
--

DROP TABLE IF EXISTS `job_outputs`;
CREATE TABLE `job_outputs` (
  `wrtid` varchar(255) default NULL,
  `id_ajob` bigint(20) default NULL,
  `pid` bigint(20) default NULL,
  `output` varchar(255) default NULL,
  `cnt` bigint(20) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Job output adatok';

--
-- Table structure for table `job_prop`
--

DROP TABLE IF EXISTS `job_prop`;
CREATE TABLE `job_prop` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `value` varchar(255) default NULL,
  `label` text NOT NULL,
  `desc` text NOT NULL,
  `inh` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='A jobokhoz kapcsolodo valos beallitasok';

--
-- Table structure for table `job_status`
--

DROP TABLE IF EXISTS `job_status`;
CREATE TABLE `job_status` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `wrtid` varchar(255) NOT NULL default '',
  `pid` bigint(20) unsigned NOT NULL default '0',
  `status` char(2) NOT NULL default '',
  `resource` text NOT NULL default '',
  KEY `index_job_status` USING BTREE (`id_workflow`,`id_ajob`,`wrtid`,`pid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `logg`
--

DROP TABLE IF EXISTS `logg`;
CREATE TABLE `logg` (
  `tim` varchar(255) NOT NULL default '',
  `service` varchar(255) NOT NULL default '',
  `user` varchar(255) NOT NULL default '',
  `workflow` varchar(255) NOT NULL default '',
  `job` varchar(255) NOT NULL default '',
  `title` varchar(255) NOT NULL default '',
  `txt` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='logg';

--
-- Table structure for table `output_prop`
--

DROP TABLE IF EXISTS `output_prop`;
CREATE TABLE `output_prop` (
  `id_workflow` bigint(20) NOT NULL default '0',
  `id_aoutput` bigint(20) NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `value` varchar(255) NOT NULL default '',
  `label` text NOT NULL,
  `desc` text NOT NULL,
  `inh` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Outputokhoz kapcsolodo erteket';

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
CREATE TABLE `repository` (
  `id` bigint(20) NOT NULL auto_increment,
  `id_portal` varchar(100) NOT NULL default '',
  `name` varchar(100) NOT NULL default '',
  `type` varchar(10) NOT NULL default '',
  `path` varchar(255) NOT NULL default '',
  `text` text NOT NULL,
  `user` varchar(55) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=109 DEFAULT CHARSET=latin1;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
CREATE TABLE `status` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `id_ajob` bigint(20) unsigned NOT NULL default '0',
  `id_rt` varchar(255) NOT NULL default '',
  `typ0` bigint(20) unsigned NOT NULL default '0',
  `tim0` bigint(20) unsigned NOT NULL default '0',
  `typ1` bigint(20) unsigned NOT NULL default '0',
  `tim1` bigint(20) unsigned NOT NULL default '0',
  `typ2` bigint(20) unsigned NOT NULL default '0',
  `tim2` bigint(20) unsigned NOT NULL default '0',
  `typ3` bigint(20) unsigned NOT NULL default '0',
  `tim3` bigint(20) unsigned NOT NULL default '0',
  `typ4` bigint(20) unsigned NOT NULL default '0',
  `tim4` bigint(20) unsigned NOT NULL default '0',
  `typ5` bigint(20) unsigned NOT NULL default '0',
  `tim5` bigint(20) unsigned NOT NULL default '0',
  `typ6` bigint(20) unsigned NOT NULL default '0',
  `tim6` bigint(20) unsigned NOT NULL default '0',
  `typ7` bigint(20) unsigned NOT NULL default '0',
  `tim7` bigint(20) unsigned NOT NULL default '0',
  `typ8` bigint(20) unsigned NOT NULL default '0',
  `tim8` bigint(20) unsigned NOT NULL default '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Vizualizacio status tabla';

--
-- Table structure for table `workflow`
--

DROP TABLE IF EXISTS `workflow`;
CREATE TABLE `workflow` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `id_aworkflow` bigint(20) unsigned NOT NULL default '0',
  `name` varchar(255) collate utf8_bin NOT NULL default '',
  `txt` varchar(255) collate utf8_bin NOT NULL default '',
  `wtyp` bigint(4) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1280 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Valos workflow deffinicios tabla';

--
-- Table structure for table `workflow_prop`
--

DROP TABLE IF EXISTS `workflow_prop`;
CREATE TABLE `workflow_prop` (
  `id_workflow` bigint(20) unsigned NOT NULL default '0',
  `wrtid` varchar(255) NOT NULL default '',
  `name` varchar(255) NOT NULL default '',
  `value` varchar(255) NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Dump completed on 2008-04-16 10:02:17
--


CREATE TABLE IF NOT EXISTS `stat_running` (
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
  `tim` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

