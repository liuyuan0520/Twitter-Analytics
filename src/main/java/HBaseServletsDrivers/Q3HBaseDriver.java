package HBaseServletsDrivers;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This is a helper class to configure HBase connections, and query the rows in
 * HBase database.
 */
public class Q3HBaseDriver {
	/**
	 * The private IP address of HBase master node.
	 */
	private static String zkAddr = "172.31.0.115";
	/**
	 * The name of your HBase table.
	 */
	private static String tableName = "q3id";
	/**
	 * HTable handler.
	 */
	private static HTableInterface q3Table;
	/**
	 * HBase connection.
	 */
	private static HConnection conn;
	/**
	 * Byte representation of column family.
	 */
	private static byte[] bColFamily = Bytes.toBytes("c");
	/**
	 * Logger.
	 */
	private final static Logger logger = Logger.getRootLogger();

	/**
	 * Initializes database connection.
	 *
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void initializeConnection() throws IOException {
		logger.setLevel(Level.ERROR);
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.master", zkAddr + ":60000");
		conf.set("hbase.zookeeper.quorum", zkAddr);
		conf.set("hbase.zookeeper.property.clientport", "2181");
		if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
			System.out.print("HBase not configured!");
			return;
		}
		conn = HConnectionManager.createConnection(conf);
		q3Table = conn.getTable(Bytes.toBytes(tableName));
	}

	// Query data from HBase
	public int[] query(String startDate, String endDate, String startId, String endId, String word1, String word2,
			String word3) throws IOException {

		int[] result = { 0, 0, 0 };

		int wordLength1 = word1.length();
		int wordLength2 = word2.length();
		int wordLength3 = word3.length();

		try {
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes(leadingZeros(startId)));
			// add more digit to include endDate
			scan.setStopRow(Bytes.toBytes(leadingZeros(endId) + "0"));

			byte[] bColWc = Bytes.toBytes("t");
			scan.addColumn(bColFamily, bColWc);

			scan.setBatch(10);
			ResultScanner rs = q3Table.getScanner(scan);
			for (Result r = rs.next(); r != null; r = rs.next()) {
				String wc = Bytes.toString(r.getValue(bColFamily, bColWc));
				String[] dateWcs = wc.split(",");
				for (String dateWc : dateWcs) {
					String date = dateWc.substring(0, 8);
					// Not in the range of start date and end date
					if (date.compareTo(startDate) < 0 || date.compareTo(endDate) > 0) {
						continue;
					}

					String text = dateWc.substring(8);
					// System.out.println(text);
					int length = text.length();

					result[0] += getCount(word1, text, wordLength1, length);
					result[1] += getCount(word2, text, wordLength2, length);
					result[2] += getCount(word3, text, wordLength3, length);
				}
			}
			// System.out.println();
			rs.close();

			return result;
		} finally {
			q3Table.close();
		}
	}

	public static int getCount(String word, String text, int wordLength, int textLength) {
		int index1 = text.indexOf(new StringBuilder().append(";").append(word).append(":").toString());
		if (index1 != -1) {
			StringBuilder sb = new StringBuilder();
			int index = index1 + wordLength + 2;
			if (index + 1 >= textLength || text.charAt(index + 1) == ';') {
				return Character.getNumericValue(text.charAt(index));
			} else {
				do {
					sb.append(text.charAt(index));
					index++;
				} while (index + 1 < textLength && text.charAt(index + 1) != ';');
				sb.append(text.charAt(index));
				return Integer.parseInt(sb.toString());
			}
		}

		index1 = text.indexOf(new StringBuilder().append(word).append(":").toString());
		if (index1 == 0) {
			StringBuilder sb = new StringBuilder();
			int index = wordLength + 1;
			if (index + 1 >= textLength || text.charAt(index + 1) == ';') {
				return Character.getNumericValue(text.charAt(index));
			} else {
				do {
					sb.append(text.charAt(index));
					index++;
				} while (index + 1 < textLength && text.charAt(index + 1) != ';');
				sb.append(text.charAt(index));
				return Integer.parseInt(sb.toString());
			}
		}
		return 0;
	}

	private String leadingZeros(String s) {
		return ("0000000000" + s).substring(s.length());
	}
}
