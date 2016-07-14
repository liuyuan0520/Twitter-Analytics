import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * Q2MySQLServlet receives parameter userid and hashtag, and return the records
 * stored in MySQL database. It instantiates a MySQLDriver object to connect to
 * MySQL database.
 */
public class Q4Servlet extends HttpServlet {
	private Q4MySQLDriver mySQLDriver;

	private ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>(16, 0.75f, 1);

    private final long MAX = 3000000;
    private LinkedHashMap<String, TweetBean> cache = new LinkedHashMap<String, TweetBean>(1000, 0.75F, false) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX;
        }
    };

	private String[] servlets = {"http://ec2-52-207-248-200.compute-1.amazonaws.com/q4?", "http://ec2-54-175-200-249.compute-1.amazonaws.com/q4?"};
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
		System.out.println("========== Start doGet ========== doGet " + "\t" + System.nanoTime() + "\t" + Thread.currentThread().getId());
		System.out.println(request.getParameter("tweetid") + "\t" + request.getParameter("seq") + "\t" + request.getParameter("forwarded"));
		String tweetid = request.getParameter("tweetid");
		String result;
		
		if (request.getParameter("forwarded") == null) {
			int whichServlet = hash(tweetid);
			if (whichServlet != servletId) {
				System.out.println("========== Start Forward ========== doGet " + "\t" + System.nanoTime());
				result = forward(whichServlet, request.getQueryString());
				System.out.println("========== End Forward ========== doGet " + "\t" + System.nanoTime());

				response.setContentType("text/plain");
				response.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				out.println(result);
				return;
			}
		}

		System.out.println("========== Start Parsing Parameters ========== doGet " + "\t" + System.nanoTime());
		ParameterBean parameters = new ParameterBean();
		parameters.setTweetid(request.getParameter("tweetid"));
		parameters.setOp(request.getParameter("op"));
		parameters.setSeq(Integer.parseInt(request.getParameter("seq")));
		parameters.setFields(request.getParameter("fields"));
		parameters.setPayload(request.getParameter("payload").replace(" ", "+"));
		System.out.println("========== End Parsing Parameters ========== doGet " + "\t" + System.nanoTime());		
		

		
		if (parameters.getOp().equals("set")) {
			System.out.println("========== Start Setting ========== doGet " + "\t" + System.nanoTime());
			set(parameters);
			result = "success";
			System.out.println("========== End Setting ========== doGet " + "\t" + System.nanoTime());
		} else {
			System.out.println("========== Start Getting ========== doGet " + "\t" + System.nanoTime());
			result = get(parameters);
			result = new StringBuilder().append(result).toString();
			System.out.println("========== End Getting ========== doGet " + "\t" + System.nanoTime());
		}

		System.out.println("========== Start to Respond ========== doGet " + "\t" + System.nanoTime());
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		
		PrintWriter out = response.getWriter();
		out.println(new StringBuilder().append(TEAMID_AND_TEAM_AWS_ACCOUNT_ID).append("\n").append(result));
		System.out.println("========== End Respond ========== doGet " + "\t" + System.nanoTime());
		
		System.out.println("========== Finish doGet ========== doGet " + "\t" + System.nanoTime());
	}

	private void set(ParameterBean parameters) {
		System.out.println("========== Start Set ========== set " + "\t" + System.nanoTime());
        // Get different parameters from the parameter bean.
		String tweetid = parameters.getTweetid();
		String[] fields = parameters.getFields().split(",");
		String[] payload = parameters.getPayload().split(",");

		int fieldsLength = fields.length;
		int payloadLength = payload.length;
		System.out.println("========== Preparing SQL ========== set " + "\t" + System.nanoTime());
        // Put fields and payload into a list to pass it to the database driver.
		List<Content> list = new ArrayList<>();
		for (int i = 0; i < payloadLength ; i++) {
			list.add(new Content(fields[i], payload[i]));
		}
		for (int i = payloadLength; i < fieldsLength; i++) {
			list.add(new Content(fields[i], ""));
		}
		System.out.println("========== End Preparing SQL ========== set " + "\t" + System.nanoTime());
        // Locking mechanism.
		int currSeq = parameters.getSeq();
		lockMap.putIfAbsent(tweetid, new Lock());
		Lock lock = lockMap.get(tweetid);

		System.out.println("========== Pick Lock ========== set " + "\t" + System.nanoTime());
		try {
			synchronized (lock) {
				boolean ready = (currSeq - 1 == lock.getLockSeq());
				while (!ready) {
					lock.wait();
					ready = (currSeq - 1 == lock.getLockSeq());
				}

				System.out.println("========== Write to SQL ========== set " + "\t" + System.nanoTime());
				mySQLDriver.set(tweetid, list);
				System.out.println("========== End Writing SQL ========== set " + "\t" + System.nanoTime());
                // Put or update cache
//                if (cache.containsKey(tweetid)) {
//                    TweetBean tweetBean =  cache.get(tweetid);
//                    for (int i = 1; i < list.size(); i++) {
//                        tweetBean.setCache(list.get(i).getColumn(), list.get(i).getValue());
//                    }
//                } else {
//                    TweetBean tweetBean = new TweetBean();
//                    for (int i = 1; i < list.size(); i++) {
//                        tweetBean.setCache(list.get(i).getColumn(), list.get(i).getValue());
//                    }
//                    cache.put(tweetid, tweetBean);
//                }

				lock.setLockSeq(currSeq);
				lock.notifyAll();
				System.out.println("========== Drop Lock ========== set " + "\t" + System.nanoTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("========== End Set ========== set " + "\t" + System.nanoTime());
	}

	private String get(ParameterBean parameters) {
		System.out.println("========== Start Get ========== get " + "\t" + System.nanoTime());
		String tweetid = parameters.getTweetid();
		String field = parameters.getFields();

		int currSeq = parameters.getSeq();
		lockMap.putIfAbsent(tweetid, new Lock());
		Lock lock = lockMap.get(tweetid);
		String response = "";
		System.out.println("========== Pick Lock ========== get " + "\t" + System.nanoTime());
		try {
			synchronized (lock) {

				boolean ready = (currSeq - 1 == lock.getLockSeq());
				while (!ready) {
					lock.wait();
					ready = (currSeq - 1 == lock.getLockSeq());
				}

//                if (cache.containsKey(tweetid)) {
//                    response = cache.get(tweetid).getCache(field);
//                } else {
				System.out.println("========== Write to SQL ========== get " + "\t" + System.nanoTime());
                    response = mySQLDriver.get(tweetid, field);
                    System.out.println("========== End Writing SQL ========== get " + "\t" + System.nanoTime());
//                }

				lock.setLockSeq(currSeq);
				lock.notifyAll();
				System.out.println("========== Drop Lock ========== get " + "\t" + System.nanoTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("========== End Get ========== get " + "\t" + System.nanoTime());
		return response;
	}

	private int hash (String key) {
		// Calculate the sum of the acsii codes of each character in the string, then decide which data center it should go to.
		int ascii = 0;
		for (int i = 0; i < key.length(); i++) {
			ascii = ascii + (int) key.charAt(i);
		}
		return ascii % numberOfServlet;
	}

//	private void forward (int whichServlet, String parameters) throws ServletException, IOException {
//		String urlAsString;
//		URL url;
//		switch (whichServlet) {
//			case 0:
//				urlAsString = new StringBuilder().append(servlets[0]).append(parameters).append(forward).toString();
//				url = new URL(urlAsString);
//				url.openStream();
//				return;
//			case 1:
//				urlAsString = new StringBuilder().append(servlets[1]).append(parameters).append(forward).toString();
//				url = new URL(urlAsString);
//				url.openStream();
//		}
//	}
	
	private String forward (int whichServlet, String queryString) throws ServletException, IOException {
		System.out.println("========== Start forward ========== forward " + "\t" + System.nanoTime());
		String urlAsString = "";
		switch (whichServlet) {
			case 0:
				urlAsString = new StringBuilder().append(servlets[0]).append(queryString).append(forward).toString();
				break;
			case 1:
				urlAsString = new StringBuilder().append(servlets[1]).append(queryString).append(forward).toString();
		}
		
//		HttpGet httpget = new HttpGet(urlAsString);
//		int statusCode = 0;
//		HttpResponse forwardResponse = null;
//		
//		System.out.println("Forward: " + urlAsString);
//
//		while (statusCode != HttpStatus.SC_OK) {
//			try {
//				HttpClient httpclient = HttpClients.createDefault();
//				forwardResponse = httpclient.execute(httpget);
//				statusCode = forwardResponse.getStatusLine().getStatusCode();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		String response = "";
//		InputStream in = forwardResponse.getEntity().getContent();
		
		String response = "";
		URL url = new URL(urlAsString);
		HttpURLConnection con = null;
		InputStream in = null;
		BufferedReader br = null;
		int responseCode = 0;
        while (true) {
		try {
			System.out.println("========== Start Forwarding Request ========== forward " + "\t" + System.nanoTime());
			while (responseCode != HttpURLConnection.HTTP_OK) {
                Thread.sleep(2);
                System.out.println("Waiting: " + urlAsString);
                con = (HttpURLConnection) url.openConnection();
//    			con.setRequestMethod("GET");
//    			con.setConnectTimeout(120 * 1000);
//    			con.setReadTimeout(120 * 1000);
                responseCode = con.getResponseCode();
            }
			in = con.getInputStream();
			System.out.println("========== Receive Forwarding Request ========== forward " + "\t" + System.nanoTime());
		
			System.out.println("========== Read from InputStream ========== forward " + "\t" + System.nanoTime());
			br = new BufferedReader(new InputStreamReader(in));
			String input;
			while ((input = br.readLine()) != null) {
				response += input + "\n";
			}
			response = response.substring(0, response.length() - 1);
			
			System.out.println("========== End Reading InputStream ========== forward " + "\t" + System.nanoTime());
			System.out.println("========== End forward ========== forward " + "\t" + System.nanoTime());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
			if (in != null) {
		        try {
		            in.close();
		        } catch (IOException e) {
		        }
		    }
		    if (con != null) {
		        con.disconnect();
		    }
		}
        }
	}

	private String forwardSet (int whichServlet, String queryString) throws ServletException, IOException {
		System.out.println("========== Start forward ========== forwardGet " + "\t" + System.nanoTime());
		String urlAsString = "";
		switch (whichServlet) {
			case 0:
				urlAsString = new StringBuilder().append(servlets[0]).append(queryString).append(forward).toString();
				break;
			case 1:
				urlAsString = new StringBuilder().append(servlets[1]).append(queryString).append(forward).toString();
		}
		
		String response = "";
		try {
			System.out.println("========== Start Forwarding Request ========== forwardGet " + "\t" + System.nanoTime());
			URL url = new URL(urlAsString);
			System.out.println("Waiting: " + urlAsString);
			InputStream in = url.openStream();
			System.out.println("========== Receive Forwarding Request ========== forwardGet " + "\t" + System.nanoTime());
		
		
			System.out.println("========== Read from InputStream ========== forwardGet " + "\t" + System.nanoTime());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String input;
			while ((input = br.readLine()) != null) {
				response += input + "\n";
			}
			response = response.substring(0, response.length() - 1);
			System.out.println("========== End Reading InputStream ========== forwardGet " + "\t" + System.nanoTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("========== End forward ========== forwardGet " + "\t" + System.nanoTime());
		return response;
	}
	
	public class Lock {
		int lockSeq = 0;

		public int getLockSeq() {
			return lockSeq;
		}

		public void setLockSeq(int lockSeq) {
			this.lockSeq = lockSeq;
		}
	}
}
