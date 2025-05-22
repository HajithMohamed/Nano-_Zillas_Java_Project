-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: tecums
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
                              `student_id` varchar(20) NOT NULL,
                              `course_code` varchar(10) NOT NULL,
                              `week1` tinyint(1) DEFAULT NULL,
                              `week2` tinyint(1) DEFAULT NULL,
                              `week3` tinyint(1) DEFAULT NULL,
                              `week4` tinyint(1) DEFAULT NULL,
                              `week5` tinyint(1) DEFAULT NULL,
                              `week6` tinyint(1) DEFAULT NULL,
                              `week7` tinyint(1) DEFAULT NULL,
                              `week8` tinyint(1) DEFAULT NULL,
                              `week9` tinyint(1) DEFAULT NULL,
                              `week10` tinyint(1) DEFAULT NULL,
                              `week11` tinyint(1) DEFAULT NULL,
                              `week12` tinyint(1) DEFAULT NULL,
                              `week13` tinyint(1) DEFAULT NULL,
                              `week14` tinyint(1) DEFAULT NULL,
                              `week15` tinyint(1) DEFAULT NULL,
                              PRIMARY KEY (`student_id`,`course_code`),
                              KEY `course_code` (`course_code`),
                              CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`username`),
                              CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`course_code`) REFERENCES `course` (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES ('TG/2022/1413','ICT2113',1,1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('TG/2022/1413','ICT2122',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('TG/2022/1414','ICT2113',0,0,NULL,1,NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('TG/2022/1414','ICT2122',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_code` varchar(20) NOT NULL,
  `course_title` varchar(100) NOT NULL,
  `lecturer_id` varchar(20) NOT NULL,
  `course_credit` decimal(3,1) NOT NULL,
  `course_type` varchar(30) NOT NULL,
  `credit_hours` int NOT NULL,
  PRIMARY KEY (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES ('ICT2113','Object oriented programming','LEC/RUH/123',3.0,'Theory',2),('ICT2122','Object Oriented Programming','LEC/RUH/TEC/001',2.0,'Practical',3);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_materials`
--

DROP TABLE IF EXISTS `course_materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_materials` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_code` varchar(20) NOT NULL,
  `week` varchar(20) NOT NULL,
  `courseMaterial` text NOT NULL,
  `courseType` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course_code` (`course_code`),
  CONSTRAINT `fk_course_code` FOREIGN KEY (`course_code`) REFERENCES `course` (`course_code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_materials`
--

LOCK TABLES `course_materials` WRITE;
/*!40000 ALTER TABLE `course_materials` DISABLE KEYS */;
INSERT INTO `course_materials` VALUES (1,'ICT2122','Week 1','C:\\L2Sem1\\Java oop\\ICT1142 - Mid Examination (Practical) - Batch 06 (2023).pdf','PDF','2025-05-17 15:10:21',NULL);
/*!40000 ALTER TABLE `course_materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eligibility`
--

DROP TABLE IF EXISTS `eligibility`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eligibility` (
  `student_id` varchar(50) NOT NULL,
  `course_code` varchar(20) NOT NULL,
  `caFinal` decimal(6,2) DEFAULT '0.00',
  `attendancePercentage` decimal(6,2) DEFAULT '0.00',
  `eligibilityStatus` varchar(50) NOT NULL,
  PRIMARY KEY (`student_id`,`course_code`),
  KEY `course_code` (`course_code`),
  CONSTRAINT `eligibility_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`username`),
  CONSTRAINT `eligibility_ibfk_2` FOREIGN KEY (`course_code`) REFERENCES `course` (`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eligibility`
--

LOCK TABLES `eligibility` WRITE;
/*!40000 ALTER TABLE `eligibility` DISABLE KEYS */;
INSERT INTO `eligibility` VALUES ('TG/2022/1414','ICT2122',49.10,6.67,'Not eligible (CA & Attendance)');
/*!40000 ALTER TABLE `eligibility` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_reports`
--

DROP TABLE IF EXISTS `medical_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_reports` (
  `medicalId` int NOT NULL AUTO_INCREMENT,
  `courseCode` varchar(20) NOT NULL,
  `studentId` varchar(20) NOT NULL,
  `week` varchar(20) NOT NULL,
  `medicalNo` varchar(50) NOT NULL,
  `medicalReport` varchar(255) NOT NULL,
  `status` varchar(10) DEFAULT 'pending',
  `submissionDate` date NOT NULL,
  PRIMARY KEY (`medicalId`),
  KEY `courseCode` (`courseCode`),
  KEY `studentId` (`studentId`),
  CONSTRAINT `medical_reports_ibfk_1` FOREIGN KEY (`courseCode`) REFERENCES `course` (`course_code`),
  CONSTRAINT `medical_reports_ibfk_2` FOREIGN KEY (`studentId`) REFERENCES `users` (`username`),
  CONSTRAINT `medical_reports_chk_1` CHECK ((`status` in (_utf8mb4'pending',_utf8mb4'accepted',_utf8mb4'rejected')))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_reports`
--

LOCK TABLES `medical_reports` WRITE;
/*!40000 ALTER TABLE `medical_reports` DISABLE KEYS */;
INSERT INTO `medical_reports` VALUES (1,'ICT2113','TG/2022/1414','1','003','1747486051955_TG_2022_1414_003.pdf','accepted','2025-05-17');
/*!40000 ALTER TABLE `medical_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notices`
--

DROP TABLE IF EXISTS `notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notices` (
  `notice_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `role` enum('Admin','Lecturer','Student','Technical Officer') DEFAULT NULL,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notices`
--

LOCK TABLES `notices` WRITE;
/*!40000 ALTER TABLE `notices` DISABLE KEYS */;
INSERT INTO `notices` VALUES (2,'exam','dear students,\nAll students are required to pay your exam fee before 22nd may 2025 for semester 1','2025-05-15 06:41:49','Student');
/*!40000 ALTER TABLE `notices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_final_gpa`
--

DROP TABLE IF EXISTS `student_final_gpa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_final_gpa` (
  `stu_id` varchar(50) NOT NULL,
  `final_gpa` decimal(4,2) NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`stu_id`),
  CONSTRAINT `student_final_gpa_ibfk_1` FOREIGN KEY (`stu_id`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_final_gpa`
--

LOCK TABLES `student_final_gpa` WRITE;
/*!40000 ALTER TABLE `student_final_gpa` DISABLE KEYS */;
INSERT INTO `student_final_gpa` VALUES ('TG/2022/1414',4.00,'2025-05-17 07:15:03');
/*!40000 ALTER TABLE `student_final_gpa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_grades`
--

DROP TABLE IF EXISTS `student_grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_grades` (
  `stu_id` varchar(50) NOT NULL,
  `course_code` varchar(20) NOT NULL,
  `quiz01` decimal(5,2) DEFAULT '0.00',
  `quiz02` decimal(5,2) DEFAULT '0.00',
  `quiz03` decimal(5,2) DEFAULT '0.00',
  `quiz04` decimal(5,2) DEFAULT '0.00',
  `assessment01` decimal(5,2) DEFAULT '0.00',
  `assessment02` decimal(5,2) DEFAULT '0.00',
  `mid` decimal(5,2) DEFAULT '0.00',
  `finalTheory` decimal(5,2) DEFAULT '0.00',
  `finalPractical` decimal(5,2) DEFAULT '0.00',
  `caTotal` decimal(6,2) DEFAULT '0.00',
  `finalTotal` decimal(6,2) DEFAULT '0.00',
  `finalMarks` decimal(6,2) DEFAULT '0.00',
  `grade` varchar(2) DEFAULT NULL,
  `gpa` decimal(4,2) DEFAULT '0.00',
  PRIMARY KEY (`stu_id`,`course_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_grades`
--

LOCK TABLES `student_grades` WRITE;
/*!40000 ALTER TABLE `student_grades` DISABLE KEYS */;
INSERT INTO `student_grades` VALUES ('STU002','ICT2142',0.00,0.00,0.00,0.00,92.00,0.00,80.00,0.00,85.00,0.00,0.00,0.00,NULL,0.00),('STU002','ICT2152',88.00,90.00,85.00,0.00,87.00,89.00,0.00,90.00,0.00,0.00,0.00,0.00,NULL,0.00),('TG/2022/1414','ICT2113',80.00,85.00,90.00,0.00,0.00,0.00,75.00,88.00,82.00,32.50,59.80,92.30,'A+',4.00),('TG/2022/1414','ICT2122',70.00,75.00,85.00,85.00,90.00,0.00,78.00,85.00,0.00,49.10,51.00,100.10,'A+',4.00),('TG/2022/1414','ICT2133',65.00,70.00,75.00,80.00,85.00,88.00,0.00,80.00,90.00,50.10,59.00,109.10,'A+',4.00);
/*!40000 ALTER TABLE `student_grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timetable` (
  `id` int NOT NULL AUTO_INCREMENT,
  `time_slot` varchar(20) NOT NULL,
  `monday` text,
  `tuesday` text,
  `wednesday` text,
  `thursday` text,
  `friday` text,
  `course_code` varchar(20) DEFAULT NULL,
  `course_title` varchar(100) DEFAULT NULL,
  `lecturer_id` varchar(20) DEFAULT NULL,
  `credits` int DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `credit_hours` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `time_slot` (`time_slot`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable`
--

LOCK TABLES `timetable` WRITE;
/*!40000 ALTER TABLE `timetable` DISABLE KEYS */;
INSERT INTO `timetable` VALUES (1,'8:00 - 10:00','wgdfggftre\njhwfjdshhfsdkh\nfsdhghgdsgj','','','','',NULL,NULL,NULL,NULL,NULL,NULL),(2,'10:00 - 12:00','','','','','',NULL,NULL,NULL,NULL,NULL,NULL),(3,'12:00 - 1:00','LUNCH BREAK','LUNCH BREAK','LUNCH BREAK','LUNCH BREAK','LUNCH BREAK',NULL,NULL,NULL,NULL,NULL,NULL),(4,'1:00 - 3:00','','','','','',NULL,NULL,NULL,NULL,NULL,NULL),(5,'3:00 - 5:00','','','','','',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `timetable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role` enum('Admin','Lecturer','Student','Technical Officer') NOT NULL,
  `email` varchar(100) NOT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `password` varchar(100) NOT NULL,
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('ADMIN/RUH/TEC/001','Sam Judson','Admin','admin.ruh.tec@gmail.com','0751234567','C:\\Users\\Admin\\Pictures\\1711728895958.jpg','2025-04-23 17:29:58','admin@123'),('LEC/RUH/TEC/001','Chanduni Gamage','Lecturer','dsfjkhdsh@gmail.com','0123456789',NULL,'2025-04-26 20:23:40','lec123'),('techOff/0001','Uthayakumar Dineshcdf','Technical Officer','ruh.tec.tecofficer@gmail.com','07401234567','C:\\Users\\Admin\\Pictures\\Screenshots\\Screenshot (2).png','2025-04-26 17:35:48','1234'),('TG/2022/1413','Mohamed Jafran','Student','jafran@gmail.com','0123456789',NULL,'2025-04-23 17:55:26','$2a$10$3GEBXJasOJQNsb0Fb/57E.doYwuHrr.kbrFsmTvfVsRQ5xnlHAtUm'),('TG/2022/1414','Mohamed Hajith','Student','hanoufaatif@gmail.com','07405bfgdvgsjgahd','C:\\Users\\Admin\\Pictures\\Screenshots\\Screenshot (2).png','2025-04-21 15:50:19','Tg1414');
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

-- Dump completed on 2025-05-18 16:56:52
