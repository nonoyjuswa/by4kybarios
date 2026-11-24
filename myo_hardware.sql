-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: myo_hardware
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory` (
  `inventory_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `rack_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uq_product_rack` (`product_id`,`rack_id`),
  KEY `fk_inventory_rack` (`rack_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_inventory_rack` FOREIGN KEY (`rack_id`) REFERENCES `racks` (`rack_id`),
  CONSTRAINT `chk_inventory_quantity` CHECK (`quantity` >= 0)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,3,1,5,'2026-10-21 19:55:00'),(2,4,4,20,'2026-10-22 04:06:17'),(3,1,33,50,'2026-10-22 04:03:01'),(4,2,1,17,'2026-10-22 04:36:10'),(5,5,31,18,'2026-10-22 04:05:46'),(7,3,2,20,'2026-10-22 04:06:16'),(12,10,1,15,'2026-10-27 04:14:39'),(19,1,2,3,'2026-10-27 04:16:57');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(20) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `unit` varchar(20) NOT NULL DEFAULT 'pcs',
  `quantity` int(11) NOT NULL DEFAULT 0,
  `reorder_level` int(11) NOT NULL DEFAULT 10,
  `rack_location` varchar(20) DEFAULT NULL,
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_code` (`product_code`),
  UNIQUE KEY `uq_product_name` (`product_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'SKU-001','Safety Helmet',NULL,'Safety Equipment','pcs',53,10,'C-03-3',350.00,'2026-10-21 14:45:46','2026-10-27 04:16:57'),(2,'SKU-002','Cordless Drill 18V',NULL,'Power Tools','pcs',17,5,'A-01-1',2500.00,'2026-10-21 14:45:46','2026-10-22 04:36:10'),(3,'SKU-003','Hex Bolt M8 (box of 100)',NULL,'Fasteners','box',25,25,'A-01-2',180.00,'2026-10-21 14:45:46','2026-10-21 19:55:00'),(4,'SKU-004','Safety Gloves, Large',NULL,'Safety Equipment','pair',20,15,'A-02-1',120.00,'2026-10-21 14:45:46','2026-10-21 20:04:59'),(5,'SKU-005','Extension Cord 10m',NULL,'Electrical','pcs',18,20,'C-03-1',450.00,'2026-10-21 14:45:46','2026-10-21 14:45:46'),(10,'SKU-010','Foil',NULL,NULL,'pcs',15,5,'A-01-1',0.00,'2026-10-27 02:54:11','2026-10-27 04:14:39');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racks`
--

DROP TABLE IF EXISTS `racks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racks` (
  `rack_id` int(11) NOT NULL AUTO_INCREMENT,
  `rack_code` varchar(20) NOT NULL,
  `zone` varchar(20) NOT NULL,
  `rack_name` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`rack_id`),
  UNIQUE KEY `rack_code` (`rack_code`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racks`
--

LOCK TABLES `racks` WRITE;
/*!40000 ALTER TABLE `racks` DISABLE KEYS */;
INSERT INTO `racks` VALUES (1,'A-01-1','A','Rack A01 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(2,'A-01-2','A','Rack A01 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(3,'A-01-3','A','Rack A01 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(4,'A-02-1','A','Rack A02 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(5,'A-02-2','A','Rack A02 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(6,'A-02-3','A','Rack A02 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(7,'A-03-1','A','Rack A03 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(8,'A-03-2','A','Rack A03 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(9,'A-03-3','A','Rack A03 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(10,'A-04-1','A','Rack A04 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(11,'A-04-2','A','Rack A04 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(12,'A-04-3','A','Rack A04 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(13,'B-01-1','B','Rack B01 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(14,'B-01-2','B','Rack B01 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(15,'B-01-3','B','Rack B01 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(16,'B-02-1','B','Rack B02 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(17,'B-02-2','B','Rack B02 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(18,'B-02-3','B','Rack B02 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(19,'B-03-1','B','Rack B03 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(20,'B-03-2','B','Rack B03 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(21,'B-03-3','B','Rack B03 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(22,'B-04-1','B','Rack B04 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(23,'B-04-2','B','Rack B04 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(24,'B-04-3','B','Rack B04 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(25,'C-01-1','C','Rack C01 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(26,'C-01-2','C','Rack C01 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(27,'C-01-3','C','Rack C01 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(28,'C-02-1','C','Rack C02 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(29,'C-02-2','C','Rack C02 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(30,'C-02-3','C','Rack C02 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(31,'C-03-1','C','Rack C03 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(32,'C-03-2','C','Rack C03 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(33,'C-03-3','C','Rack C03 - Shelf 3',NULL,'Active','2026-10-21 14:48:03'),(34,'C-04-1','C','Rack C04 - Shelf 1',NULL,'Active','2026-10-21 14:48:03'),(35,'C-04-2','C','Rack C04 - Shelf 2',NULL,'Active','2026-10-21 14:48:03'),(36,'C-04-3','C','Rack C04 - Shelf 3',NULL,'Active','2026-10-21 14:48:03');
/*!40000 ALTER TABLE `racks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_movements`
--

DROP TABLE IF EXISTS `stock_movements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_movements` (
  `movement_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `rack_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `movement_type` enum('IN','OUT') NOT NULL,
  `quantity` int(11) NOT NULL,
  `reference_no` varchar(50) DEFAULT NULL,
  `remarks` text DEFAULT NULL,
  `movement_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`movement_id`),
  KEY `fk_movement_product` (`product_id`),
  KEY `fk_movement_rack` (`rack_id`),
  KEY `fk_movement_user` (`user_id`),
  CONSTRAINT `fk_movement_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_movement_rack` FOREIGN KEY (`rack_id`) REFERENCES `racks` (`rack_id`),
  CONSTRAINT `fk_movement_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `chk_movement_quantity` CHECK (`quantity` > 0)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_movements`
--

LOCK TABLES `stock_movements` WRITE;
/*!40000 ALTER TABLE `stock_movements` DISABLE KEYS */;
INSERT INTO `stock_movements` VALUES (1,1,3,1,'IN',10,'SI-0001','Initial stock received','2026-10-21 01:00:00'),(2,2,1,1,'IN',5,'SI-0002','New cordless drills received','2026-10-21 01:30:00'),(3,3,2,2,'OUT',2,'SO-0001','Issued hex bolts','2026-10-21 02:00:00'),(4,4,4,3,'OUT',3,'SO-0002','Issued safety gloves','2026-10-21 02:30:00'),(5,5,31,4,'IN',5,'SI-0003','Extension cords received','2026-10-21 03:00:00'),(6,3,1,1,'IN',5,'0001','from crush','2026-10-21 19:55:00'),(7,4,4,1,'IN',12,'003','clean first','2026-10-21 20:04:59'),(9,2,1,1,'OUT',5,'001','sale','2026-10-22 04:32:07'),(10,2,1,1,'OUT',3,'101','sale','2026-10-22 04:36:10'),(11,10,1,1,'IN',5,NULL,'Initial stock','2026-10-27 02:54:11'),(12,10,1,1,'IN',5,NULL,'Stock added from Inventory screen','2026-10-27 03:12:04'),(13,10,1,1,'OUT',10,'00143','sell','2026-10-27 03:16:51'),(14,10,1,1,'IN',5,NULL,'Stock added from Inventory screen','2026-10-27 03:28:24'),(15,10,1,1,'IN',5,NULL,'Stock added from Inventory screen','2026-10-27 03:34:13'),(16,10,1,1,'IN',5,NULL,'Stock added from Inventory screen','2026-10-27 04:14:39'),(17,1,2,1,'IN',3,'001','sell','2026-10-27 04:16:57');
/*!40000 ALTER TABLE `stock_movements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `f_name` varchar(100) NOT NULL,
  `role` enum('Admin','Employee') DEFAULT 'Employee',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `employee_id` (`employee_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'ADM-001','admin','admin123','Angelee Tabia','Admin','2026-10-21 14:50:56'),(2,'EMP-001','employee1','emp123','Dayniel Baitan','Employee','2026-10-21 14:50:56'),(3,'EMP-002','employee2','emp123','Emmarie Salabia','Employee','2026-10-21 14:50:56'),(4,'EMP-003','employee3','emp123','Adrian Mallorca','Employee','2026-10-21 14:50:56');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-20 15:19:39
