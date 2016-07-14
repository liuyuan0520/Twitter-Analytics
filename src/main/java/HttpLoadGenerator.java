import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class HttpLoadGenerator {
	public static void main(String[] args) {
		while (true) {
			sendQ2();
		}
		
//		getContent("http://ec2-52-207-248-200.compute-1.amazonaws.com/q4?tweetid=122&op=set&seq=1&fields=useragent&payload=MzMxO&forwarded=yes");
	}
	
	private static void sendQ4() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("input_q4"));
			String input = "";
			while ((input = br.readLine()) != null) {
				long start = System.currentTimeMillis();
				getContent("http://phase3-1974502136.us-east-1.elb.amazonaws.com/q4?" + input);
				System.out.println(System.currentTimeMillis() - start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendQ3() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("input_q3"));
			String input = br.readLine();
			String[] keys = input.split("\t");
			while ((input = br.readLine()) != null) {
				String[] values = input.split("\t");
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (int i = 0; i < keys.length; i++) {
					params.add(new BasicNameValuePair(keys[i], values[i]));
				}

				String paramString = URLEncodedUtils.format(params, "utf-8");
				long start = System.currentTimeMillis();
				getContent("http://phase3-1974502136.us-east-1.elb.amazonaws.com/q3?" + paramString);
				System.out.println(System.currentTimeMillis() - start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendQ2() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("input_q2"));
			String input = br.readLine();
			String[] keys = input.split("\t");
			while ((input = br.readLine()) != null) {
				String[] values = input.split("\t");
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (int i = 0; i < keys.length; i++) {
					params.add(new BasicNameValuePair(keys[i], values[i]));
				}

				String paramString = URLEncodedUtils.format(params, "utf-8");
				long start = System.currentTimeMillis();
				getContent("http://phase3-1974502136.us-east-1.elb.amazonaws.com/q2?" + paramString);
				System.out.println(System.currentTimeMillis() - start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendQ1() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("input_q1"));
			String input = br.readLine();
			String[] keys = input.split("\t");
			while ((input = br.readLine()) != null) {
				String[] values = input.split("\t");
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (int i = 0; i < keys.length; i++) {
					params.add(new BasicNameValuePair(keys[i], values[i]));
				}

				String paramString = URLEncodedUtils.format(params, "utf-8");
				long start = System.currentTimeMillis();
				getContent("http://phase3-1974502136.us-east-1.elb.amazonaws.com/q1?" + paramString);
				System.out.println(System.currentTimeMillis() - start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getContent(String url) {
		try {

			HttpGet httpget = new HttpGet(url);

			// Execute get
			int statusCode = 0;
			HttpResponse response = null;

			while (statusCode != HttpStatus.SC_OK) {
				try {
					System.out.print(httpget.toString() + "... ");
					HttpClient httpclient = HttpClients.createDefault();
					response = httpclient.execute(httpget);
					statusCode = response.getStatusLine().getStatusCode();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			InputStream in = response.getEntity().getContent();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String input;
				while ((input = br.readLine()) != null) {
					System.out.print(input + " ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}