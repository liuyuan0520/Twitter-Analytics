import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2MySQLServlet receives parameter userid and hashtag, and return the records
 * stored in MySQL database. It instantiates a MySQLDriver object to connect to
 * MySQL database.
 */
public class Q2MySQLServletPhase2V2 extends HttpServlet {
    private Q2MySQLDriverPhase2V2 mySQLDriver;


    private static final String TEAMID = "My heart is in the work";
    private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

    public void init() throws ServletException {
        mySQLDriver = new Q2MySQLDriverPhase2V2();
        try {
            Q2MySQLDriverPhase2V2.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        count++;

        String userId = request.getParameter("userid");
        String hashtag = request.getParameter("hashtag");

        String key = new StringBuilder().append(userId).append(hashtag).toString();

        // Check if the key is already in cache. If yes, query directly from
        // cache. If not, query from database and put in cache.
        String tweets = mySQLDriver.query(key);


        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        out.println(TEAMID + ", " + TEAM_AWS_ACCOUNT_ID);
        out.println(tweets);
        System.out.println(userId);
        out.print(";");
    }
}
