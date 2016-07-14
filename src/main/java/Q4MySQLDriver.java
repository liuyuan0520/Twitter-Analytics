import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is a helper class to configure MySQL connections, and query the rows in
 * MySQL database.
 */
public class Q4MySQLDriver {

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
    public void set(String tweetid, List<Content> list) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = generateSql(tweetid, list);

        // INSERT INTO `user`(`username`, `password`) VALUES ('ersks','Nepal') ON DUPLICATE KEY UPDATE `username`='master',`password`='Nepal';
        // INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');
        try {
            conn = cpds.getConnection();
            // Query data from database.
            // String sql = new StringBuilder().append("INSERT INTO q4 (").append(columnBuilder).append(") VALUES (").append(valueBuilder).append(")").toString(); //append(") ON DUPLICATE KEY UPDATE ").append(updateBuilder).toString();
            //System.out.println("update sql: " + sql);
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
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
    }

    public void batchSet (List<String> sqls) {
        Connection conn = null;
        Statement stmt = null;

        // INSERT INTO `user`(`username`, `password`) VALUES ('ersks','Nepal') ON DUPLICATE KEY UPDATE `username`='master',`password`='Nepal';
        // INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');
        try {
            conn = cpds.getConnection();
            stmt = conn.createStatement();
            for (String s : sqls) {
                stmt.addBatch(s);
            }
            // Query data from database.
            // String sql = new StringBuilder().append("INSERT INTO q4 (").append(columnBuilder).append(") VALUES (").append(valueBuilder).append(")").toString(); //append(") ON DUPLICATE KEY UPDATE ").append(updateBuilder).toString();
            stmt.executeBatch();
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
    }

    public void update(List<Content> list) {
        Connection conn = null;
        PreparedStatement stmt = null;

        StringBuilder primaryKeyBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();
        int size = list.size();

        primaryKeyBuilder.append(" WHERE `tweetid` = ").append(list.get(0).getValue());

        for (int i = 1; i < list.size() - 1; i++) {
            String column = list.get(i).getColumn();
            String value = list.get(i).getValue();
            updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("',");
        }

        String column = list.get(size - 1).getColumn();
        String value = list.get(size - 1).getValue();
        updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("'");

        // INSERT INTO `user`(`username`, `password`) VALUES ('ersks','Nepal') ON DUPLICATE KEY UPDATE `username`='master',`password`='Nepal';
        // INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country) VALUES ('Cardinal','Tom B. Erichsen','Skagen 21','Stavanger','4006','Norway');
        // UPDATE Customers SET ContactName='Alfred Schmidt', City='Hamburg' WHERE CustomerName='Alfreds Futterkiste';
        try {
            conn = cpds.getConnection();

            // Query data from database.
            String sql = new StringBuilder().append("UPDATE q4 SET ").append(updateBuilder).append(primaryKeyBuilder).toString();
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate(sql);
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
    }

    public static String generateSql (String tweetid, List<Content> list) {
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();
        int size = list.size();

        columnBuilder.append("`tweetid`").append(",");
        valueBuilder.append(tweetid).append(", ");

        for (int i = 0; i < list.size() - 1; i++) {
            String column = list.get(i).getColumn();
            String value = list.get(i).getValue();
            columnBuilder.append("`").append(column).append("`,");
            valueBuilder.append("'").append(value).append("',");
            updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("',");
        }

        String column = list.get(size - 1).getColumn();
        String value = list.get(size - 1).getValue();
        columnBuilder.append("`").append(column).append("`");
        valueBuilder.append("'").append(value).append("'");
        updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("'");

        String sql = new StringBuilder().append("INSERT INTO q4 (").append(columnBuilder).append(") VALUES (").append(valueBuilder).append(") ON DUPLICATE KEY UPDATE ").append(updateBuilder).toString();
        return sql;
    }

    public String get (String tweetid, String field) {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = cpds.getConnection();

            String sql = new StringBuilder().append("SELECT ").append(field).append(" FROM q4 WHERE `tweetid` = ").append(Long.parseLong(tweetid)).toString();
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            String result = "";
            if (rs.next()) {
                result = rs.getString(1);
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
        return "";
    }
}
