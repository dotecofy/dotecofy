-- MySQL dump 10.16  Distrib 10.1.37-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: dotecofy
-- ------------------------------------------------------
-- Server version	10.1.37-MariaDB

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
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_impr_kind` int(10) unsigned NOT NULL,
  `id_improvement` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_signature_impr` (`signature`,`id_improvement`),
  KEY `fk_assignment_impr_type_idx` (`id_impr_kind`),
  KEY `fk_assignment_impr_idx` (`id_improvement`),
  CONSTRAINT `fk_assignment_impr` FOREIGN KEY (`id_improvement`) REFERENCES `improvement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignment_impr_kind` FOREIGN KEY (`id_impr_kind`) REFERENCES `improvement_kind` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment_impr_layer`
--

DROP TABLE IF EXISTS `assignment_impr_layer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment_impr_layer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_assignment` int(10) unsigned NOT NULL,
  `id_impr_layer` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_assign_impr_layer` (`id_assignment`,`id_impr_layer`),
  KEY `fk_assignment_impr_layer_assign_idx` (`id_assignment`),
  KEY `fk_assignment_impr_layer_impr_layer_idx` (`id_impr_layer`),
  CONSTRAINT `fk_assignment_impr_layer_assign` FOREIGN KEY (`id_assignment`) REFERENCES `assignment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_assignment_impr_layer_impr_layer` FOREIGN KEY (`id_impr_layer`) REFERENCES `improvement_layer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_impr_layer`
--

LOCK TABLES `assignment_impr_layer` WRITE;
/*!40000 ALTER TABLE `assignment_impr_layer` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignment_impr_layer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cycle`
--

DROP TABLE IF EXISTS `cycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cycle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_workspace` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cycle_project_idx` (`id_workspace`),
  CONSTRAINT `fk_cycle_workspace` FOREIGN KEY (`id_workspace`) REFERENCES `workspace` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cycle`
--

LOCK TABLES `cycle` WRITE;
/*!40000 ALTER TABLE `cycle` DISABLE KEYS */;
INSERT INTO `cycle` VALUES (1,1,'cycle-1','Cycle 1','The first cycle','2018-12-22 15:33:25',NULL),(2,1,'documentation','Documentation','The documentation is the first step','2018-12-24 12:31:53',NULL);
/*!40000 ALTER TABLE `cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature`
--

DROP TABLE IF EXISTS `feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feature` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_project` int(11) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `signature_UNIQUE` (`signature`),
  KEY `fk_feature_project_idx` (`id_project`),
  CONSTRAINT `fk_feature_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature`
--

LOCK TABLES `feature` WRITE;
/*!40000 ALTER TABLE `feature` DISABLE KEYS */;
INSERT INTO `feature` VALUES (1,1,'feature-1','Feature1','The first feature','2018-12-12 10:21:00',NULL),(2,1,'feature-2','Feature 2','The second feature','2018-12-12 16:19:35',NULL);
/*!40000 ALTER TABLE `feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,'grp1_admin','admin','admin','2018-12-18 12:48:22',NULL);
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_right`
--

DROP TABLE IF EXISTS `group_right`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_right` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_group` int(10) unsigned NOT NULL,
  `id_right` int(10) unsigned NOT NULL,
  `id_tuple` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_group_right_tuple` (`id_group`,`id_right`,`id_tuple`),
  KEY `fk_group_right_group_idx` (`id_group`),
  KEY `fk_group_right_right_idx` (`id_right`),
  CONSTRAINT `fk_group_right_group` FOREIGN KEY (`id_group`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_right_right` FOREIGN KEY (`id_right`) REFERENCES `right` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_right`
--

LOCK TABLES `group_right` WRITE;
/*!40000 ALTER TABLE `group_right` DISABLE KEYS */;
INSERT INTO `group_right` VALUES (1,1,1,1);
/*!40000 ALTER TABLE `group_right` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `improvement`
--

DROP TABLE IF EXISTS `improvement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `improvement` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_feature` int(11) unsigned NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documentation` tinytext COLLATE utf8mb4_unicode_ci,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `signature_UNIQUE` (`signature`),
  KEY `fk_improvement_feature_idx` (`id_feature`),
  CONSTRAINT `fk_improvement_feature` FOREIGN KEY (`id_feature`) REFERENCES `feature` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `improvement`
--

LOCK TABLES `improvement` WRITE;
/*!40000 ALTER TABLE `improvement` DISABLE KEYS */;
INSERT INTO `improvement` VALUES (1,1,'Create the database','create-database','Create the data base of the project','Nothing much to say','2018-12-12 17:34:21',NULL);
/*!40000 ALTER TABLE `improvement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `improvement_cycle`
--

DROP TABLE IF EXISTS `improvement_cycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `improvement_cycle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_improvement` int(10) unsigned NOT NULL,
  `id_cycle` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_improvement_cycle_impr_idx` (`id_improvement`),
  KEY `fk_improvement_cycle_cycle_idx` (`id_cycle`),
  CONSTRAINT `fk_improvement_cycle_cycle` FOREIGN KEY (`id_cycle`) REFERENCES `cycle` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_improvement_cycle_impr` FOREIGN KEY (`id_improvement`) REFERENCES `improvement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `improvement_cycle`
--

LOCK TABLES `improvement_cycle` WRITE;
/*!40000 ALTER TABLE `improvement_cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `improvement_cycle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `improvement_kind`
--

DROP TABLE IF EXISTS `improvement_kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `improvement_kind` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_improvement` int(10) unsigned NOT NULL,
  `id_kind` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_improvement_type_type_idx` (`id_kind`),
  KEY `fk_improvement_type_impr_idx` (`id_improvement`),
  CONSTRAINT `fk_improvement_kind_impr` FOREIGN KEY (`id_improvement`) REFERENCES `improvement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_improvement_kind_kind` FOREIGN KEY (`id_kind`) REFERENCES `kind` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `improvement_kind`
--

LOCK TABLES `improvement_kind` WRITE;
/*!40000 ALTER TABLE `improvement_kind` DISABLE KEYS */;
/*!40000 ALTER TABLE `improvement_kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `improvement_layer`
--

DROP TABLE IF EXISTS `improvement_layer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `improvement_layer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_improvement` int(11) unsigned NOT NULL,
  `id_layer` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_improvement_layer_impr_idx` (`id_improvement`),
  KEY `fk_improvement_layer_layer_idx` (`id_layer`),
  CONSTRAINT `fk_improvement_layer_impr` FOREIGN KEY (`id_improvement`) REFERENCES `improvement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_improvement_layer_layer` FOREIGN KEY (`id_layer`) REFERENCES `layer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `improvement_layer`
--

LOCK TABLES `improvement_layer` WRITE;
/*!40000 ALTER TABLE `improvement_layer` DISABLE KEYS */;
/*!40000 ALTER TABLE `improvement_layer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `improvement_version`
--

DROP TABLE IF EXISTS `improvement_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `improvement_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_improvement` int(11) unsigned NOT NULL,
  `id_version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_improvement_version_version_idx` (`id_version`),
  KEY `fk_improvement_version_impr_idx` (`id_improvement`),
  CONSTRAINT `fk_improvement_version_impr` FOREIGN KEY (`id_improvement`) REFERENCES `improvement` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_improvement_version_version` FOREIGN KEY (`id_version`) REFERENCES `version` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `improvement_version`
--

LOCK TABLES `improvement_version` WRITE;
/*!40000 ALTER TABLE `improvement_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `improvement_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind`
--

DROP TABLE IF EXISTS `kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kind` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_workspace` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_type_project_idx` (`id_workspace`),
  CONSTRAINT `fk_type_workspace` FOREIGN KEY (`id_workspace`) REFERENCES `workspace` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind`
--

LOCK TABLES `kind` WRITE;
/*!40000 ALTER TABLE `kind` DISABLE KEYS */;
INSERT INTO `kind` VALUES (1,1,'task','Task','All the requiered tasks','2018-12-25 16:40:42',NULL);
/*!40000 ALTER TABLE `kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `layer`
--

DROP TABLE IF EXISTS `layer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `layer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_workspace` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_layer_workspace_idx` (`id_workspace`),
  CONSTRAINT `fk_layer_workspace` FOREIGN KEY (`id_workspace`) REFERENCES `workspace` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `layer`
--

LOCK TABLES `layer` WRITE;
/*!40000 ALTER TABLE `layer` DISABLE KEYS */;
INSERT INTO `layer` VALUES (1,1,'layer-1','Layer 1','Descr layer 1','2018-12-22 15:08:19',NULL);
/*!40000 ALTER TABLE `layer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_user` int(10) unsigned NOT NULL,
  `table` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_row` int(10) unsigned NOT NULL,
  `message` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_log_user_idx` (`id_user`),
  CONSTRAINT `fk_log_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `output`
--

DROP TABLE IF EXISTS `output`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `output` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_assignment` int(10) unsigned NOT NULL,
  `id_impr_cycle` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_assign_impr_cycle` (`id_assignment`,`id_impr_cycle`),
  UNIQUE KEY `signature_UNIQUE` (`signature`),
  KEY `fk_output_assign_idx` (`id_assignment`),
  KEY `fk_output_impr_cycle_idx` (`id_impr_cycle`),
  CONSTRAINT `fk_output_assign` FOREIGN KEY (`id_assignment`) REFERENCES `assignment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_output_impr_cycle` FOREIGN KEY (`id_impr_cycle`) REFERENCES `improvement_cycle` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `output`
--

LOCK TABLES `output` WRITE;
/*!40000 ALTER TABLE `output` DISABLE KEYS */;
/*!40000 ALTER TABLE `output` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_workspace` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_signature_workspace` (`id_workspace`,`signature`),
  KEY `fk_project_workspace_idx` (`id_workspace`),
  CONSTRAINT `fk_project_workspace` FOREIGN KEY (`id_workspace`) REFERENCES `workspace` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,1,'project-1','First project',NULL,'2018-12-13 20:46:32',NULL),(2,1,'project-2','Project 2','This is my second project inserted from the website','2018-12-23 18:01:47',NULL),(4,1,'project-3','Project 3 ','This is the third project','2018-12-23 18:42:59',NULL);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `right`
--

DROP TABLE IF EXISTS `right`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `right` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `right` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `table` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `right_UNIQUE` (`right`),
  KEY `ix_table` (`table`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `right`
--

LOCK TABLES `right` WRITE;
/*!40000 ALTER TABLE `right` DISABLE KEYS */;
INSERT INTO `right` VALUES (1,'VIEW_WORKSPACE','workspace');
/*!40000 ALTER TABLE `right` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fullname` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `salt` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `salt_UNIQUE` (`salt`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Joël Favre','joel.favre@dest.cloud','aefawef','fa\'9w0f8','2018-12-18 12:40:41',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_user` int(10) unsigned NOT NULL,
  `id_group` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_group` (`id_user`,`id_group`),
  KEY `fk_user_group_user_idx` (`id_user`),
  KEY `fk_user_group_group_idx` (`id_group`),
  CONSTRAINT `fk_user_group_group` FOREIGN KEY (`id_group`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_group_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES (1,1,1);
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification`
--

DROP TABLE IF EXISTS `verification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_output` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verification_date` datetime NOT NULL,
  `created_date` datetime NOT NULL,
  `update_date` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `signature_UNIQUE` (`signature`),
  KEY `fk_verification_output_idx` (`id_output`),
  CONSTRAINT `fk_verification_output` FOREIGN KEY (`id_output`) REFERENCES `output` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification`
--

LOCK TABLES `verification` WRITE;
/*!40000 ALTER TABLE `verification` DISABLE KEYS */;
/*!40000 ALTER TABLE `verification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `version` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_project` int(10) unsigned NOT NULL,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documentation` tinytext COLLATE utf8mb4_unicode_ci,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_signature_project` (`id_project`,`signature`),
  UNIQUE KEY `uq_version_project` (`id_project`,`version`),
  KEY `fk_version_project_idx` (`id_project`),
  CONSTRAINT `fk_version_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version`
--

LOCK TABLES `version` WRITE;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
INSERT INTO `version` VALUES (1,1,'0-18-12-12','0.18.12.12','The alpha version of our soft','You can find everything on dotecofy.com','2018-12-12 17:33:43',NULL),(2,1,'1-18-12-24','1.18.12.24','The Christmas version',NULL,'2018-12-24 11:28:32',NULL);
/*!40000 ALTER TABLE `version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workspace`
--

DROP TABLE IF EXISTS `workspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workspace` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `signature` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `signature_UNIQUE` (`signature`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workspace`
--

LOCK TABLES `workspace` WRITE;
/*!40000 ALTER TABLE `workspace` DISABLE KEYS */;
INSERT INTO `workspace` VALUES (1,'workspace1','First workspace',NULL,'2018-12-13 20:45:52',NULL),(140,'update-workspace-1-1','Update Workspace 1.1','Description ws 1.1','2018-12-21 17:51:09','2018-12-21 17:51:09');
/*!40000 ALTER TABLE `workspace` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-28 17:33:31
