DROP TABLE IF EXISTS `twitter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `twitter` (
  `tweet_id` varchar(30) NOT NULL,
  `user_id` text,
  `create_at` text,
  `sentiment_density` double DEFAULT NULL,
  `text` text,
  `hashtags` text,
  PRIMARY KEY (`tweet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;