import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class Q4Servlet2 extends HttpServlet {
	private Q4MySQLDriver mySQLDriver;

	private ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>(524288, 0.75f, 1);

	private final long MAX = 3000000;
	private LinkedHashMap<String, TweetBean> cache = new LinkedHashMap<String, TweetBean>(1000, 0.75F, false) {
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX;
		}
	};

	private String[] servlets = { 
			"http://ec2-52-201-239-238.compute-1.amazonaws.com/q4?",
			"http://ec2-54-84-215-178.compute-1.amazonaws.com/q4?",
			"http://ec2-54-165-201-4.compute-1.amazonaws.com/q4?",
			"http://ec2-54-164-89-61.compute-1.amazonaws.com/q4?",
			"http://ec2-54-89-161-120.compute-1.amazonaws.com/q4?",
			"http://ec2-52-90-149-186.compute-1.amazonaws.com/q4?"};
	private int servletId = 1;
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
		//System.out.println("========== Start doGet ========== doGet " + "\t" + System.nanoTime() + "\t"
				//+ Thread.currentThread().getId());
		//System.out.println(request.getParameter("tweetid") + "\t" + request.getParameter("seq") + "\t"
				//+ request.getParameter("forwarded"));
		String tweetid = request.getParameter("tweetid");
		String result;

		if (request.getParameter("forwarded") == null) {
			int whichServlet = hash(tweetid);
			if (whichServlet != servletId) {
				//System.out.println("========== Send Redirect ========== doGet " + "\t" + System.nanoTime());
				response.sendRedirect(servlets[whichServlet] + request.getQueryString() + forward);
				//System.out.println("========== End Redirect ========== doGet " + "\t" + System.nanoTime());
				return;
			}
		}

		//System.out.println("========== Start Parsing Parameters ========== doGet " + "\t" + System.nanoTime());
		ParameterBean parameters = new ParameterBean();
		parameters.setTweetid(request.getParameter("tweetid"));
		parameters.setOp(request.getParameter("op"));
		parameters.setSeq(Integer.parseInt(request.getParameter("seq")));
		parameters.setFields(request.getParameter("fields"));
		parameters.setPayload(request.getParameter("payload").replace(" ", "+"));
		//System.out.println("========== End Parsing Parameters ========== doGet " + "\t" + System.nanoTime());

		if (parameters.getOp().equals("set")) {
			//System.out.println("========== Start Setting ========== doGet " + "\t" + System.nanoTime());
			set(parameters);
			result = "success";
			//System.out.println("========== End Setting ========== doGet " + "\t" + System.nanoTime());
		} else {
			//System.out.println("========== Start Getting ========== doGet " + "\t" + System.nanoTime());
			result = get(parameters);
			result = new StringBuilder().append(result).toString();
			//System.out.println("========== End Getting ========== doGet " + "\t" + System.nanoTime());
		}

		//System.out.println("========== Start to Respond ========== doGet " + "\t" + System.nanoTime());
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		StringBuilder sb = new StringBuilder().append(TEAMID_AND_TEAM_AWS_ACCOUNT_ID).append("\n").append(result);
		out.println(sb);
		//System.out.println("========== End Respond ========== doGet " + "\t" + System.nanoTime());

		System.out.println(result + "233333");
		//System.out.println("Output: " + sb.toString());
		//System.out.println("========== Finish doGet ========== doGet " + "\t" + System.nanoTime());
	}

	private void set(ParameterBean parameters) {
		//System.out.println("========== Start Set ========== set " + "\t" + System.nanoTime());
		// Get different parameters from the parameter bean.
		String tweetid = parameters.getTweetid();
		String[] fields = parameters.getFields().split(",");
		String[] payload = parameters.getPayload().split(",");

		int fieldsLength = fields.length;
		int payloadLength = payload.length;
		//System.out.println("========== Preparing SQL ========== set " + "\t" + System.nanoTime());
		// Put fields and payload into a list to pass it to the database driver.
		List<Content> list = new ArrayList<>();
		for (int i = 0; i < payloadLength; i++) {
			list.add(new Content(fields[i], payload[i]));
		}
		for (int i = payloadLength; i < fieldsLength; i++) {
			list.add(new Content(fields[i], ""));
		}
		//System.out.println("========== End Preparing SQL ========== set " + "\t" + System.nanoTime());
		// Locking mechanism.
		int currSeq = parameters.getSeq();
		lockMap.putIfAbsent(tweetid, new Lock());
		Lock lock = lockMap.get(tweetid);

		//System.out.println("========== Pick Lock ========== set " + "\t" + System.nanoTime());
		try {
			synchronized (lock) {
				boolean ready = (currSeq - 1 == lock.getLockSeq());
				while (!ready) {
					lock.wait();
					ready = (currSeq - 1 == lock.getLockSeq());
				}

				//System.out.println("========== Write to SQL ========== set " + "\t" + System.nanoTime());
				mySQLDriver.set(tweetid, list);
				//System.out.println("========== End Writing SQL ========== set " + "\t" + System.nanoTime());
				// Put or update cache
				// if (cache.containsKey(tweetid)) {
				// TweetBean tweetBean = cache.get(tweetid);
				// for (int i = 1; i < list.size(); i++) {
				// tweetBean.setCache(list.get(i).getColumn(),
				// list.get(i).getValue());
				// }
				// } else {
				// TweetBean tweetBean = new TweetBean();
				// for (int i = 1; i < list.size(); i++) {
				// tweetBean.setCache(list.get(i).getColumn(),
				// list.get(i).getValue());
				// }
				// cache.put(tweetid, tweetBean);
				// }

				lock.setLockSeq(currSeq);
				lock.notifyAll();
				//System.out.println("========== Drop Lock ========== set " + "\t" + System.nanoTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("========== End Set ========== set " + "\t" + System.nanoTime());
	}

	private String get(ParameterBean parameters) {
		//System.out.println("========== Start Get ========== get " + "\t" + System.nanoTime());
		String tweetid = parameters.getTweetid();
		String field = parameters.getFields();

		int currSeq = parameters.getSeq();
		lockMap.putIfAbsent(tweetid, new Lock());
		Lock lock = lockMap.get(tweetid);
		String response = "";
		//System.out.println("========== Pick Lock ========== get " + "\t" + System.nanoTime());
		try {
			synchronized (lock) {

				boolean ready = (currSeq - 1 == lock.getLockSeq());
				while (!ready) {
					lock.wait();
					ready = (currSeq - 1 == lock.getLockSeq());
				}

				// if (cache.containsKey(tweetid)) {
				// response = cache.get(tweetid).getCache(field);
				// } else {
				//System.out.println("========== Write to SQL ========== get " + "\t" + System.nanoTime());
				response = mySQLDriver.get(tweetid, field);
				//System.out.println("========== End Writing SQL ========== get " + "\t" + System.nanoTime());
				// }

				lock.setLockSeq(currSeq);
				lock.notifyAll();
				//System.out.println("========== Drop Lock ========== get " + "\t" + System.nanoTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("========== End Get ========== get " + "\t" + System.nanoTime());
		return response;
	}

	private int hash(String key) {
		// Calculate the sum of the acsii codes of each character in the string,
		// then decide which data center it should go to.
		int hc = key.hashCode();
		if (hc >= 0) {
			return hc % servlets.length;
		} else {
			return -hc % servlets.length;
		}
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
