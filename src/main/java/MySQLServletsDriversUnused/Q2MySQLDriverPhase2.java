package MySQLServletsDriversUnused;

import java.beans.PropertyVetoException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class Q2MySQLDriverPhase2 {

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
        Statement stmt = null;

        try {

            conn = cpds.getConnection();
            stmt = conn.createStatement();

            String md5Hash = generateMD5(key);

            String sql = "SELECT output FROM q2 WHERE `MD5` = '" + md5Hash + "'";
            ResultSet rs = stmt.executeQuery(sql);
            String result = null;
            if (rs.next()) {
                result = rs.getString("output");
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


    public static String generateMD5(String message)  {
        return hashString(message, "MD5");
    }

    private static String hashString(String message, String algorithm) {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "NoSuchAlgorithmException";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "UnsupportedEncodingException";
        }
    }
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
