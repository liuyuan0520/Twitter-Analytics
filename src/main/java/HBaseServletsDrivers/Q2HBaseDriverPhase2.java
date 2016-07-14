package HBaseServletsDrivers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This is a helper class to configure HBase connections, and get the row with
 * corresponding RowKey in HBase database.
 */
public class Q2HBaseDriverPhase2 {

    /**
     * The private IP address of HBase master node.
     */
    private static String zkAddr = "172.31.0.157";
    /**
     * The name of your HBase table.
     */
    private static String tableName = "q2id";

    /**
     * HTable handler.
     */

    private static HTableInterface songsTable;
    /**
     * HBase connection.
     */
    private static HConnection conn;
    /**
     * Byte representation of column family.
     */
    private static byte[] bColFamily = Bytes.toBytes("c");
    private static byte[] contents = Bytes.toBytes("t");
    private static byte[] tableNameInBytes = Bytes.toBytes(tableName);
    private static HTable table;
    /**
     * Logger.
     */
    private final static Logger logger = Logger.getRootLogger();
    /**
     * Configurations.
     */
    private static Configuration conf;

    /**
     * Initialize HBase connection.
     *
     * @throws IOException
     */
    public static void initializeConnection() throws IOException {
        // Remember to set correct log level to avoid unnecessary output.
        logger.setLevel(Level.ERROR);
        conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":60000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
            System.out.print("HBase not configured!");
            return;
        }
        conn = HConnectionManager.createConnection(conf);
        // songsTable = conn.getTable(Bytes.toBytes(tableName));
        table = new HTable(conf, tableNameInBytes);
    }

    public String query(String key) throws IOException {

        // conn = HConnectionManager.createConnection(conf);

        byte[] rowKey = Bytes.toBytes(key);

        // Get result using RowKey
        Get get = new Get(rowKey);
        Result result = table.get(get);

        String resultList = Bytes.toString(result.getValue(bColFamily, contents));

        return resultList;
    }
}
