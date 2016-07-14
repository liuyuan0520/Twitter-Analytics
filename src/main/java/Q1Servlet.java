import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q1Servlet handles requests by parsing key and message, and responds with the
 * original text. This class initiates a Decrypter instance to decrypt the
 * message.
 */
public class Q1Servlet extends HttpServlet {
	private Decrypter decrypter;

	private static String TEAMID_AND_TEAM_AWS_ACCOUNT_ID = "My heart is in the work, 7891-6482-8821";
	private static long count = 0;
	private static long cacheHitCount = 0;
	private static long cacheNotHitCount = 0;
	private static final long MAX = 5000000;
	private LinkedHashMap<String, String> cache = new LinkedHashMap<String, String>(1000, 0.75F, false) {
		// Delete the eldest entry when hash map is full to make space for new
		// entries.
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX;
		}
	};

	public void init() throws ServletException {
		decrypter = new Decrypter();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		String timestamp = new Timestamp(System.currentTimeMillis() + TimeZone.getTimeZone("EST").getRawOffset())
				.toString();
		timestamp = timestamp.substring(0, timestamp.indexOf("."));

		String key = request.getParameter("key");
		String message = request.getParameter("message");

		// Decrypt the message.
		String decryptedMessage = "";
		if (key != null && message != null) {
			decryptedMessage = decrypter.decrypt(key, message);
		}
//		String cacheKey = new StringBuilder().append(key).append(message).toString();
//		if (!cache.containsKey(cacheKey)) {
//			decryptedMessage = decrypter.decrypt(key, message);
//			cache.put(cacheKey, decryptedMessage);
//			cacheNotHitCount++;
//		} else {
//			decryptedMessage = cache.get(cacheKey);
//			cacheHitCount++;
//		}
		response.setContentType("text/html");

//		if (count % 50000 == 0) {
//			System.out.println("CACHE SIZE= " + cache.size());
//			System.out.println("TOTAL COUNT= " + count);
//			System.out.println("CACHE HIT COUNT= " + cacheHitCount);
//			System.out.println("CACHE NOT HIT COUNT= " + cacheNotHitCount);
//		}

		PrintWriter out = response.getWriter();
		out.println(TEAMID_AND_TEAM_AWS_ACCOUNT_ID);
		out.println(timestamp);
		out.println(decryptedMessage);
		System.out.println(decryptedMessage + "233333");
	}
}