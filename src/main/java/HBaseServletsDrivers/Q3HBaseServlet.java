package HBaseServletsDrivers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2MySQLServlet receives parameter userid and hashtag, and return the records
 * stored in MySQL database. It instantiates a MySQLDriver object to connect to
 * MySQL database.
 */
public class Q3HBaseServlet extends HttpServlet {
	private Q3HBaseDriver q3HBaseDriver;
	// private HashMap<String, ArrayList<String>> cache = new HashMap<>();
	private static final String TEAMID = "My heart is in the work";
	private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

	public void init() throws ServletException {
		q3HBaseDriver = new Q3HBaseDriver();
		try {
			q3HBaseDriver.initializeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String startDateString = request.getParameter("start_date");
		String startDate = startDateString.replace("-", "");

		String endDateString = request.getParameter("end_date");
		String endDate = endDateString.replace("-", "");

		String startId = request.getParameter("start_userid");
		String endId = request.getParameter("end_userid");
		String words = request.getParameter("words");

		String[] parts = words.split(",");

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		// Query data from database.
		int[] counts = q3HBaseDriver.query(startDate, endDate, startId, endId, parts[0], parts[1], parts[2]);

		PrintWriter out = response.getWriter();
		StringBuilder sb = new StringBuilder();
		out.println(sb.append(TEAMID).append(", ").append(TEAM_AWS_ACCOUNT_ID));
		sb = new StringBuilder().append(parts[0]).append(":").append(counts[0]).append("\n").append(parts[1])
				.append(":").append(counts[1]).append("\n").append(parts[2]).append(":").append(counts[2]);
		out.println(sb.toString());
	}
}
