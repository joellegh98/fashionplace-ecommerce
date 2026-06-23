/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.1.2-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: ex4
-- ------------------------------------------------------
-- Server version	12.1.2-MariaDB-ubu2404

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Current Database: `ex4`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ex4` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;

USE `ex4`;

--
-- Table structure for table `activity_log`
--

DROP TABLE IF EXISTS `activity_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_log`
--

LOCK TABLES `activity_log` WRITE;
/*!40000 ALTER TABLE `activity_log` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `activity_log` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `app_user`
--

DROP TABLE IF EXISTS `app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `disabled` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1j9d9a06i600gd43uu3km82jw` (`email`),
  UNIQUE KEY `UK3k4cplvh82srueuttfkwnylq0` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_user`
--

LOCK TABLES `app_user` WRITE;
/*!40000 ALTER TABLE `app_user` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `app_user` VALUES
(1,'42 Maple Avenue, Rivertown','2026-06-23 11:38:12.913700',0x00,'alice@example.com','$2a$10$FPeXrxLYYNJ2yeAJp1N8UOxS.MrStkbz4ZauxLTl5qUEFwQE43FuC','ADMIN','alice'),
(2,'7 Oak Lane, Hillside','2026-06-23 11:38:12.958078',0x00,'bob@example.com','$2a$10$FPeXrxLYYNJ2yeAJp1N8UOxS.MrStkbz4ZauxLTl5qUEFwQE43FuC','USER','bob'),
(3,'1 Market Street, Springfield','2026-06-23 11:38:13.340727',0x00,'admin@fashionplace.com','$2a$10$K7L8uHqhHKWPUPejIGBs1uVOKP5HbdCVL0wc4oz8qq5khQMfcz4u6','ADMIN','admin');
/*!40000 ALTER TABLE `app_user` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `price_at_purchase` decimal(38,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  KEY `FK551losx9j75ss5d6bfsqvijna` (`product_id`),
  CONSTRAINT `FK551losx9j75ss5d6bfsqvijna` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `total_price` decimal(38,2) NOT NULL,
  `buyer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK70kwbm46jx27dad2knne9s22n` (`buyer_id`),
  CONSTRAINT `FK70kwbm46jx27dad2knne9s22n` FOREIGN KEY (`buyer_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `item_condition` varchar(255) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_content_type` varchar(255) DEFAULT NULL,
  `image_data` longblob DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `seller_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKthco7cl7o18ewrdcav6n9llbe` (`seller_id`),
  CONSTRAINT `FKthco7cl7o18ewrdcav6n9llbe` FOREIGN KEY (`seller_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `product` VALUES
(1,'Earrings','New',0x00,'Classic 14k gold-plated hoop earrings, lightweight and perfect for everyday wear.',NULL,NULL,'https://www.longsjewelers.com/cdn/shop/products/GEH2588A.jpg?v=1715249533',45.99,5,'ACTIVE','Gold Hoop Earrings',1),
(2,'Necklace','New',0x00,'Delicate sterling silver chain with a minimalist pendant.',NULL,NULL,'https://prya.co.uk/cdn/shop/products/IMG_92992-PRYA-Necklaces.jpg?v=1619772139',62.50,3,'ACTIVE','Silver Chain Necklace',1),
(3,'Shirts','Used',0x00,'Genuine black leather jacket with zip front and quilted shoulders.',NULL,NULL,'https://cdn-images.farfetch-contents.com/17/81/19/24/17811924_37690679_600.jpg',189.00,1,'ACTIVE','Leather Biker Jacket',2),
(4,'Jeans','Used',0x00,'High-waisted straight-leg jeans in a faded blue wash.',NULL,NULL,'https://www.bigw.com.au/medias/sys_master/images/images/h85/ha0/116332415582238.jpg',55.00,4,'ACTIVE','Vintage Denim Jeans',2),
(5,'Bracelet','New',0x00,'Freshwater pearl bracelet with a silver clasp, elegant and timeless.',NULL,NULL,'https://www.jerseypearl.com/wp-content/uploads/2023/07/Zara-Freshwater-Pearl-Multi-Natural-Bracelet.jpg',38.75,2,'ACTIVE','Pearl Bracelet',1),
(6,'Bottoms','New',0x00,'Floor-length emerald silk dress, ideal for formal occasions.',NULL,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXa7eixb7OwHn5k8EXd7pUGDlSBk-do_q0drqZG6QfmMw-FXohQmFVNnN1&s=10',220.00,0,'SOLD','Silk Evening Dress',2);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(2000) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int(11) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `reviewer_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiyof1sindb9qiqr9o8npj8klt` (`product_id`),
  KEY `FKdksd7fbjmi2jpt8dsg27etnh2` (`reviewer_id`),
  CONSTRAINT `FKdksd7fbjmi2jpt8dsg27etnh2` FOREIGN KEY (`reviewer_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FKiyof1sindb9qiqr9o8npj8klt` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `review` VALUES
(1,'Beautiful earrings, exactly as described!','2026-06-23 11:38:13.166374',5,1,2),
(2,'Lovely quality, slightly smaller than I expected.','2026-06-23 11:38:13.170180',4,1,1),
(3,'not real pearls','2026-06-23 11:48:12.801107',4,5,1),
(4,'happy with it','2026-06-23 11:51:50.202187',3,3,1);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `support_message`
--

DROP TABLE IF EXISTS `support_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `body` tinytext NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `sender_type` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq3s5kpya27qmhydndo62a6fw6` (`user_id`),
  CONSTRAINT `FKq3s5kpya27qmhydndo62a6fw6` FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `support_message`
--

LOCK TABLES `support_message` WRITE;
/*!40000 ALTER TABLE `support_message` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `support_message` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `wishlist_item`
--

DROP TABLE IF EXISTS `wishlist_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `owner_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3l5uqdvb5butdsu6ph72l35eh` (`owner_id`),
  KEY `FK5s5jxai41c8tqklyy111ngqh7` (`product_id`),
  CONSTRAINT `FK3l5uqdvb5butdsu6ph72l35eh` FOREIGN KEY (`owner_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `FK5s5jxai41c8tqklyy111ngqh7` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist_item`
--

LOCK TABLES `wishlist_item` WRITE;
/*!40000 ALTER TABLE `wishlist_item` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `wishlist_item` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Dumping routines for database 'ex4'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-06-23  9:21:00
