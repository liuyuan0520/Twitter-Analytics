import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2MySQLServlet receives parameter userid and hashtag, and return the records
 * stored in MySQL database. It instantiates a MySQLDriver object to connect to
 * MySQL database.
 */
public class Q4NonforwardingServlet extends HttpServlet {
    private Q4MySQLDriver mySQLDriver;

    private final long MAX = 3000000;
    private HashMap<String, TweetBean> cache = new HashMap<String, TweetBean>();
    private int count = 0;

    private String[] servlets = {"http://ec2-52-207-248-200.compute-1.amazonaws.com/q4?", "http://ec2-52-207-248-200.compute-1.amazonaws.com/q4?"};
    private int numberOfServlet = 2;
    private int servletId = 0;
    private String forward = "&forwarded=yes";

    private static final String TEAMID_AND_TEAM_AWS_ACCOUNT_ID = "My heart is in the work, 7891-6482-8821";

    public void init() throws ServletException {
        mySQLDriver = new Q4MySQLDriver();
        try {
            Q4MySQLDriver.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


//        if (!op.equals("set")) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.println(new StringBuilder().append(TEAMID_AND_TEAM_AWS_ACCOUNT_ID).append("\n").append("success"));
//        }
    }
}
