import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2MySQLServlet receives parameter userid and hashtag, and return the records
 * stored in MySQL database. It instantiates a MySQLDriver object to connect to
 * MySQL database.
 */
public class Q3MySQLServlet extends HttpServlet {
    private Q3MySQLDriver mySQLDriver;
    // private HashMap<String, ArrayList<String>> cache = new HashMap<>();

    private static final String TEAMID = "My heart is in the work";
    private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

    public void init() throws ServletException {
        mySQLDriver = new Q3MySQLDriver();
        try {
            Q3MySQLDriver.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String startDateString = request.getParameter("start_date");
        int startDate = Integer.parseInt(startDateString.replace("-", ""));

        String endDateString = request.getParameter("end_date");
        int endDate = Integer.parseInt(endDateString.replace("-", ""));

        long startId = Long.parseLong(request.getParameter("start_userid"));
        long endId = Long.parseLong(request.getParameter("end_userid"));
        String words = request.getParameter("words");

        if (words == null) {
            return;
        }

        String[] parts = words.split(",");
        if (parts.length != 3) {
            return;
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        int[] counts = mySQLDriver.query(startDate, endDate, startId, endId, parts[0], parts[1], parts[2]);

        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        out.println(sb.append(TEAMID).append(", ").append(TEAM_AWS_ACCOUNT_ID));
        sb = new StringBuilder().append(parts[0]).append(":").append(counts[0]).append("\n").append(parts[1]).append(":").append(counts[1]).append("\n").append(parts[2]).append(":").append(counts[2]);
        System.out.println(counts[0]);
        out.println(sb.toString());
    }
}
