# 15619Phase1
**Team**  
My heart is in the work  
  
**Members**  
Dongyu Liu (dongyul1)  
Yuan Liu (yuanl3)  
Li Lin (lli2)  

## Class Descriptions

**Default Package**
- Main.java:  
This class contains the main method to initiate Undertow Servlet. Specify path such as "/q1" or "/q2" in the main method, as well as the corresponding servlet class.  

- Q1Servlet.java:  
Q1Servlet handles requests by parsing key and message, and responds with the original text. This class initiates a Decrypter instance to decrypt the message.  

- Decrypter.java:  
This is a helper class to decrypt the message given key and cipher.  

- Q2MySQLServlet.java:  
Q2MySQLServlet receives parameter userid and hashtag, and return the records stored in MySQL database. It instantiates a MySQLDriver object to connect to MySQL database.

- MySQLDriver.java:  
This is a helper class to configure MySQL connections, and query the rows in MySQL database.  

- Q2HBaseServlet.java:  
Q2HBaseServlet receives parameter userid and hashtag, and return the records stored in HBase database. It instantiates an HBaseDriver object to connect to HBase database.

- HBaseDriver.java:  
This is a helper class to configure HBase connections, and get the row with corresponding RowKey in HBase database.  

**MapReduce01**  
This package contains the mapper and reducer for the first round MapReduce. It transforms a large Twitter data around 1TB to near 50GB.
- Q2Mapper.java:  
This mapper parses raw data from Twitter as JSON. It then filters malformed tweets, calculated the sentiment density of a tweet, and censors it if it contains banned words.

- Q2Reducer.java:  
This reducer removes duplicate tweets.

**MapReduce02**  
This package contains the mapper and reducer for the second round MapReduce. It transforms 50GB data to near 15GB.
- Q2Mapper.java:  
This mapper removes rows without hastag or leaves only one row if a tweet contains same hashtags.

- Q2Reducer.java:  
This reducer sorts tweets with the same userid and hashtag by sentiment density, time and tweet ID, respectively. The output can be loaded into MySQL database directly.

**HBaseMapReduce02**  
This package contains the mapper and reducer to transform data into one that can be loaded into HBase database.
- HBaseMapper2.java:  
This mapper is used to merge the userId and Hash tag into one column and use the combined string as rowkey in Hbase. Also, in the reducer HBaseReducer2, it combined all the other info into one column and format them as required.

- HBaseMapper2.java:  
This reducer is used to combine all info exclude userid and hash tag into one column and format them as required in the output.

**HBaseMapReduce**  
This package is deprecated. Use HBaseMapReduce02 instead.
- HBaseMapper.java:  
This mapper is deprecated. Use HBaseMapper2.java instead.

- HBaseMapper.java:  
This reducer is deprecated. Use HBaseReducer2.java instead.

**unused**  
This package contains temporary classes that are not used.
