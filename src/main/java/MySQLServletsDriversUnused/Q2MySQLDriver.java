package MySQLServletsDriversUnused;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is a helper class to configure MySQL connections, and query the rows in
 * MySQL database.
 */
public class Q2MySQLDriver {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_NAME = "p2";
	private static final String URL = "jdbc:mysql://localhost/" + DB_NAME;

	private static final String DB_USER = "root";
	private static final String DB_PWD = "123456";

	private static ComboPooledDataSource cpds;

	/**
	 * Initializes database connection.
	 *
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void initializeConnection() throws ClassNotFoundException, SQLException {

		// Configure connection pool
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(JDBC_DRIVER);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(URL);
		cpds.setUser(DB_USER);
		cpds.setPassword(DB_PWD);
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
	}

	// Query data from MySQL
	public ArrayList<String> query(String id, String hashtag) {

		Connection conn = null;
		Statement stmt = null;

		ArrayList<String> list = new ArrayList<>();
		try {

			conn = cpds.getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM twitter WHERE `user_id` = '" + id + "' AND BINARY `hashtags` = '" + hashtag
					+ "'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String sentimental_density = rs.getString(4);
				String tweet_time = rs.getString(5);
				String tweet_id = rs.getString(6);
				String text = rs.getString(7);
				StringBuilder sb = new StringBuilder();
				sb.append(sentimental_density).append(":").append(tweet_time).append(":").append(tweet_id).append(":")
						.append(text);
				list.add(sb.toString());
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
