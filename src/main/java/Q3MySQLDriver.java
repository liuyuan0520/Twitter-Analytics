import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is a helper class to configure MySQL connections, and query the rows in
 * MySQL database.
 */
public class Q3MySQLDriver {

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
    public int[] query(int startDate, int endDate, long startId, long endId, String word1, String word2, String word3) {

        Connection conn = null;
        PreparedStatement stmt = null;
        int[] result = {0, 0, 0};

        int wordLength1 = word1.length();
        int wordLength2 = word2.length();
        int wordLength3 = word3.length();

        try {
            conn = cpds.getConnection();
            // Query data from database.
            String sql = new StringBuilder().append("SELECT text FROM q3 WHERE id BETWEEN ").append(startId).append(" AND ").append(endId).append(" AND date BETWEEN ").append(startDate).append(" AND ").append(endDate).toString();
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String text = rs.getString("text");
                int length = text.length();

                // Calculate word count.
                result[0] += getCount(word1, text, wordLength1, length);
                result[1] += getCount(word2, text, wordLength2, length);
                result[2] += getCount(word3, text, wordLength3, length);
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


    public static int getCount (String word, String text, int wordLength, int textLength) {
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
}
