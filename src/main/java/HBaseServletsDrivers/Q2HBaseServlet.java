package HBaseServletsDrivers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2HBaseServlet receives parameter userid and hashtag, and return the records
 * stored in HBase database. It instantiates an HBaseDriver object to connect to
 * HBase database.
 */
public class Q2HBaseServlet extends HttpServlet {
	private Q2HBaseDriver hBaseDriver;
//	private long max = 3000000;
//	private long count = 0;
//	private LinkedHashMap<String, String> cache = new LinkedHashMap<String, String>(1000, 0.75F, false) {
//		// Delete the eldest entry when hash map is full to make space for new
//		// entries.
//		protected boolean removeEldestEntry(Map.Entry eldest) {
//			return size() > max;
//		}
//	};

	private static final String TEAMID = "My heart is in the work";
	private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

	public void init() throws ServletException {
		System.out.println("Initializing");
		hBaseDriver = new Q2HBaseDriver();
		try {
			System.out.println("init");
			Q2HBaseDriver.initializeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userid");
		String hashtag = request.getParameter("hashtag");

		String key = new StringBuilder().append(userId).append(hashtag).toString();

		// Check if the key is already in cache. If yes, query directly from
		// cache. If not, query from database and put in cache.
		String tweets = hBaseDriver.query(key);
		if (tweets != null) {
			tweets = tweets.replace("\\n", "\n").replace("\\", "");
		}
//		if (!cache.containsKey(key)) {
//
//			if (tweets != null) {
//				tweets = tweets.replace("\\n", "\n").replace("\\", "");
//			}
//			cache.put(key, tweets);
//		} else {
//			tweets = cache.get(key);
//		}

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
//		count++;
//		if (count % 1000 == 0) {
//			System.out.println(cache.size());
//		}
		out.println(TEAMID + ", " + TEAM_AWS_ACCOUNT_ID);
		out.println(tweets);
		out.print(";");
	}
}
