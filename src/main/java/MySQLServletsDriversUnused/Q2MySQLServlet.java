package MySQLServletsDriversUnused;

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
public class Q2MySQLServlet extends HttpServlet {
	private Q2MySQLDriver mySQLDriver;
	// private HashMap<String, ArrayList<String>> cache = new HashMap<>();
	private long max = 1000000;
	private LinkedHashMap<String, ArrayList<String>> cache = new LinkedHashMap<String, ArrayList<String>>(1000, 0.75F,
			false) {
		// Delete the eldest entry when hash map is full to make space for new
		// entries.
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > max;
		}
	};
	private static final String TEAMID = "My heart is in the work";
	private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

	public void init() throws ServletException {
		mySQLDriver = new Q2MySQLDriver();
		try {
			Q2MySQLDriver.initializeConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userId = request.getParameter("userid");
		String hashtag = request.getParameter("hashtag");

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		String key = new StringBuilder().append(userId).append(hashtag).toString();

		// Check if the key is already in cache. If yes, query directly from
		// cache. If not, query from database and put in cache.
		ArrayList<String> tweets;
		if (!cache.containsKey(key)) {
			tweets = mySQLDriver.query(userId, hashtag);
			cache.put(key, tweets);
		} else {
			tweets = cache.get(key);
		}

		PrintWriter out = response.getWriter();
		out.println(TEAMID + ", " + TEAM_AWS_ACCOUNT_ID);
		for (String tweet : tweets) {
			out.println(tweet);
		}
		out.print(";");
	}
}
