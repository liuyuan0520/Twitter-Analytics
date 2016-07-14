import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is a helper class to configure MySQL connections, and query the rows in
 * MySQL database.
 */
public class Q2MySQLDriverPhase2V2 {

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
    }

    // Query data from MySQL
    public String query(String key) {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = cpds.getConnection();
            String sql = "SELECT text FROM q2 WHERE `idtag` = '" + key + "'";
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            String result = null;
            if (rs.next()) {
                result = rs.getString("text");
            }
            return result;
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
