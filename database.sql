-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: bdhospital
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `administrativo`
--

DROP TABLE IF EXISTS `administrativo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrativo` (
  `Cedula` varchar(45) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Clave` varchar(45) NOT NULL,
  PRIMARY KEY (`Cedula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrativo`
--

LOCK TABLES `administrativo` WRITE;
/*!40000 ALTER TABLE `administrativo` DISABLE KEYS */;
INSERT  IGNORE INTO `administrativo` VALUES ('123','Ana Morales','1'),('204560789','Carlos Vega','cvega456'),('305670123','María López','mlopez789'),('407890234','Jorge Rojas','jrojas321'),('509012345','Lucía Fernández','lfernandez654'),('601234567','Diego Hernández','dhernandez987');
/*!40000 ALTER TABLE `administrativo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmaceuta`
--

DROP TABLE IF EXISTS `farmaceuta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farmaceuta` (
  `Cedula` varchar(45) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `clave` varchar(45) NOT NULL,
  `Estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`Cedula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmaceuta`
--

LOCK TABLES `farmaceuta` WRITE;
/*!40000 ALTER TABLE `farmaceuta` DISABLE KEYS */;
INSERT  IGNORE INTO `farmaceuta` VALUES ('123','Karolina Diaz','3',1),('F005','Lorena Salazar','F005',1),('F009','Carolina Mendez','F009',1),('F010','Lana Lang','F010',1),('F060','Clark Kent','F060',1),('F075','María Fernández','F075',1),('F076','Juan Pérez','F076',1),('F077','Ana Gómez','F077',1),('F078','Carlos Rodríguez','F078',1),('F079','Laura Morales','F079',1),('F080','David Castro','F080',1),('F081','Isabel Torres','F081',1),('F082','Fernando Rojas','F082',1);
/*!40000 ALTER TABLE `farmaceuta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `indicaciones`
--

DROP TABLE IF EXISTS `indicaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indicaciones` (
  `Cantidad` int NOT NULL,
  `Indicaciones` varchar(45) NOT NULL,
  `Duracion` varchar(45) NOT NULL,
  `Receta_id` int NOT NULL,
  `CodigoMedicamento` varchar(45) NOT NULL,
  PRIMARY KEY (`Receta_id`,`CodigoMedicamento`),
  KEY `fk_Indicaciones_Receta1_idx` (`Receta_id`),
  KEY `fk_Indicaciones_Medicamento1_idx` (`CodigoMedicamento`),
  CONSTRAINT `fk_Indicaciones_Medicamento1` FOREIGN KEY (`CodigoMedicamento`) REFERENCES `medicamento` (`Codigo`),
  CONSTRAINT `fk_Indicaciones_Receta1` FOREIGN KEY (`Receta_id`) REFERENCES `receta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indicaciones`
--

LOCK TABLES `indicaciones` WRITE;
/*!40000 ALTER TABLE `indicaciones` DISABLE KEYS */;
INSERT  IGNORE INTO `indicaciones` VALUES (1,'Después del desayuno','3',4,'M003'),(1,'Después del desayuno','3',5,'M002'),(1,'Aplicar en la zona afectada','10',6,'M004'),(2,'Cada 8 horas','5',7,'M001'),(1,'Antes del desayuno','14',7,'M005'),(1,'Después de la cena','30',8,'M006'),(1,'Una vez al día','14',9,'M004'),(3,'Cada 12 horas','10',10,'M003'),(2,'Antes del desayuno y cena','20',11,'M017'),(1,'Después del almuerzo','7',12,'M010'),(2,'Cada 6 horas','5',13,'M001'),(1,'Inhalar 2 veces al día','15',14,'M016'),(1,'Aplicar 2 veces al día','10',15,'M013'),(1,'Antes de dormir','7',16,'M024'),(1,'3 veces al día','20',17,'M016'),(1,'Diaria después del desayuno','30',18,'M001'),(5,'Cada 2 horas','3',19,'M002'),(5,'Antes de acostarse a dormir','3',20,'M001'),(10,'Mañana y noche 1 tba','3',21,'M015');
/*!40000 ALTER TABLE `indicaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamento`
--

DROP TABLE IF EXISTS `medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento` (
  `Codigo` varchar(45) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Presentacion` varchar(45) NOT NULL,
  `Estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`Codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicamento`
--

LOCK TABLES `medicamento` WRITE;
/*!40000 ALTER TABLE `medicamento` DISABLE KEYS */;
INSERT  IGNORE INTO `medicamento` VALUES ('M001','Paracetamol','500 mg',1),('M002','Ibuprofeno','400 mg',1),('M003','Amoxicilina','250 mg cápsulas',1),('M004','Loratadina','10 mg tabletas',1),('M005','Omeprazol','20 mg cápsulas',1),('M006','Metformina','850 mg tabletas',1),('M007','Losartán','50 mg tabletas',1),('M008','Enalapril','10 mg tabletas',1),('M009','Claritromicina','500 mg tabletas',1),('M010','Diclofenaco','50 mg tabletas',1),('M011','Cefalexina','500 mg cápsulas',1),('M012','Azitromicina','500 mg tabletas',1),('M013','Prednisona','5 mg tabletas',1),('M014','Hidroclorotiazida','25 mg tabletas',1),('M015','Furosemida','40 mg tabletas',1),('M016','Salbutamol','Inhalador 100 mcg/dosis',1),('M017','Insulina Glargina','100 UI/ml solución inyectable',1),('M018','Ranitidina','150 mg tabletas',1),('M019','Clopidogrel','75 mg tabletas',1),('M020','Aspirina','100 mg tabletas',1),('M021','Atorvastatina','20 mg tabletas',1),('M022','Simvastatina','20 mg tabletas',1),('M023','Levotiroxina','50 mcg tabletas',1),('M024','Diazepam','10 mg tabletas',1),('M025','Alprazolam','0.5 mg tabletas',1),('M026','Sertralina','50 mg tabletas',1),('M027','Escitalopram','10 mg tabletas',1),('M028','Fluoxetina','20 mg cápsulas',1),('M029','Quetiapina','100 mg tabletas',1),('M030','Olanzapina','10 mg tabletas',1),('M031','Gabapentina','300 mg cápsulas',1),('M032','Tramadol','100 mg cápsulas',1),('M033','Morfina','10 mg/ml solución inyectable',1),('M034','Ketorolaco','10 mg tabletas',1);
/*!40000 ALTER TABLE `medicamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medico`
--

DROP TABLE IF EXISTS `medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medico` (
  `Cedula` varchar(45) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Especialidad` varchar(45) NOT NULL,
  `Clave` varchar(45) NOT NULL,
  `Estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`Cedula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medico`
--

LOCK TABLES `medico` WRITE;
/*!40000 ALTER TABLE `medico` DISABLE KEYS */;
INSERT  IGNORE INTO `medico` VALUES ('123','Juan Pérez','Cardiología','2',1),('M002','María López','Pediatría','clave456',1),('M003','Carlos Sánchez','Neurología','clave789',1),('M004','Ana Torres','Ginecología','clave321',1),('M089','Pedro','Cabezas','M089',1);
/*!40000 ALTER TABLE `medico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paciente`
--

DROP TABLE IF EXISTS `paciente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paciente` (
  `Cedula` varchar(45) NOT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Telefono` varchar(45) NOT NULL,
  `Fecha Nacimiento` date NOT NULL,
  `Estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`Cedula`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paciente`
--

LOCK TABLES `paciente` WRITE;
/*!40000 ALTER TABLE `paciente` DISABLE KEYS */;
INSERT  IGNORE INTO `paciente` VALUES ('1010','Ricardo Torres','60101234','2003-03-22',1),('1212','Valeria Soto','60112345','1994-07-14',1),('1313','Diego Méndez','60123456','2002-11-09',1),('1414','Gabriela Ruiz','60134567','1998-06-17',1),('1515','Héctor Vargas','60145678','1997-12-25',1),('1616','Paola Castillo','60156789','2005-05-02',1),('1717','Sebastián Navarro','60167890','1996-08-27',1),('1818','Carolina Pineda','60178901','2000-01-13',1),('1919','Martín Chacón','60189012','1999-09-21',1),('2020','Daniela Cordero','60190123','2004-02-06',1),('2121','Tomás Esquivel','60201234','1995-10-10',1),('2222','David Rodríguez','60023456','2002-09-20',1),('3333','María Fernández','60034567','1997-12-03',1),('4444','José Ramírez','60045678','2004-06-28',1),('5555','Sofía Morales','60056789','1995-01-07',1),('5678','Juan Pérez','50234567','2000-07-12',1),('6548','Carlos Sanchez','80050016','2005-03-01',1),('6666','Andrés López','60067890','2001-10-19',1),('7777','Camila Vargas','60078901','1996-02-11',1),('8888','Felipe Castro','60089012','2000-04-30',1),('9101','Ana Gómez','50345678','1999-11-23',1),('9999','Isabel Herrera','60090123','1999-08-05',1);
/*!40000 ALTER TABLE `paciente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receta`
--

DROP TABLE IF EXISTS `receta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `Codigo` varchar(45) NOT NULL,
  `Estado` varchar(20) NOT NULL,
  `FechaEmision` date NOT NULL,
  `FechaRetiro` date NOT NULL,
  `Paciente_Cedula` varchar(45) NOT NULL,
  `Medico_Cedula` varchar(45) NOT NULL,
  PRIMARY KEY (`id`,`Paciente_Cedula`,`Medico_Cedula`),
  KEY `fk_Receta_Paciente1_idx` (`Paciente_Cedula`),
  KEY `fk_Receta_Medico1_idx` (`Medico_Cedula`),
  CONSTRAINT `fk_Receta_Medico1` FOREIGN KEY (`Medico_Cedula`) REFERENCES `medico` (`Cedula`),
  CONSTRAINT `fk_Receta_Paciente1` FOREIGN KEY (`Paciente_Cedula`) REFERENCES `paciente` (`Cedula`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receta`
--

LOCK TABLES `receta` WRITE;
/*!40000 ALTER TABLE `receta` DISABLE KEYS */;
INSERT  IGNORE INTO `receta` VALUES (4,'R01','CONFECCIONADA','2025-08-27','2025-08-30','5678','123'),(5,'R02','CONFECCIONADA','2025-08-28','2025-09-01','9101','M002'),(6,'R03','CONFECCIONADA','2025-09-01','2025-09-05','6548','M003'),(7,'R04','CONFECCIONADA','2025-09-02','2025-09-06','2222','M004'),(8,'R05','CONFECCIONADA','2025-09-03','2025-09-10','3333','M089'),(9,'R06','CONFECCIONADA','2025-09-04','2025-09-08','4444','123'),(10,'R07','CONFECCIONADA','2025-09-05','2025-09-15','5555','M002'),(11,'R08','CONFECCIONADA','2025-09-06','2025-09-20','6666','M003'),(12,'R09','CONFECCIONADA','2025-09-07','2025-09-14','7777','M004'),(13,'R10','CONFECCIONADA','2025-09-08','2025-09-12','8888','M089'),(14,'R11','CONFECCIONADA','2025-09-09','2025-09-24','9999','123'),(15,'R12','CONFECCIONADA','2025-09-10','2025-09-20','1010','M002'),(16,'R13','CONFECCIONADA','2025-09-11','2025-09-18','1212','M003'),(17,'R14','CONFECCIONADA','2025-09-12','2025-09-30','1313','M004'),(18,'R15','CONFECCIONADA','2025-09-13','2025-10-13','1414','M089'),(19,'R16','CONFECCIONADA','2025-09-11','2025-09-10','2222','123'),(20,'R17','CONFECCIONADA','2025-09-11','2025-09-11','4444','123'),(21,'R18','CONFECCIONADA','2025-09-14','2025-09-14','1818','123');
/*!40000 ALTER TABLE `receta` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 23:14:49
