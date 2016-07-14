DROP TABLE IF EXISTS `q4`;
CREATE TABLE `q4` (
  `tweetid` BIGINT NOT NULL,
  `userid` TEXT,
  `username` TEXT,
  `timestamp` TEXT,
  `text` TEXT,
  `hashtag` TEXT,
  `ip` TEXT,
  `coordinates` TEXT,
  `repliedby` TEXT,
  `reply_count` TEXT,
  `mentioned` TEXT,
  `mentioned_count` TEXT,
  `favoritedby` TEXT,
  `favorite_count` TEXT,
  `useragent` TEXT,
  `filter_level` TEXT,
  `lang` TEXT,
  PRIMARY KEY (`tweetid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
