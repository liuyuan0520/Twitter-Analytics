package HBaseServletsDrivers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Q2HBaseServlet receives parameter userid and hashtag, and return the records
 * stored in HBase database. It instantiates an HBaseDriver object to connect to
 * HBase database.
 */
public class Q2HBaseServletPhase2V2 extends HttpServlet {
    private Q2HBaseDriverPhase2V2 hBaseDriver;
    private long max = 3000000;
    private long count = 0;
    private LinkedHashMap<String, String> cache = new LinkedHashMap<String, String>(1000, 0.75F, false) {
        // Delete the eldest entry when hash map is full to make space for new
        // entries.
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > max;
        }
    };

    private static final String TEAMID = "My heart is in the work";
    private static final String TEAM_AWS_ACCOUNT_ID = "7891-6482-8821";

    public void init() throws ServletException {
        System.out.println("Initializing");
        hBaseDriver = new Q2HBaseDriverPhase2V2();
        try {
            System.out.println("init");
            Q2HBaseDriverPhase2V2.initializeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userid");
        String hashtag = request.getParameter("hashtag");

        String key = generateMD5(new StringBuilder().append(userId).append(hashtag).toString());

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
