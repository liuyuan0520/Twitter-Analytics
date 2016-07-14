package Q3MapReduce02;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Q3Mapper2 {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			// Initialize Variables
			String input;
			String userId = null;
			String time = null;
			String userIdTime = null;
			String[] wordCount = null;
			String word = null;
			String count = null;

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");
				// If no format is wrong, skip this line
				if (parts.length < 4) {
					continue;
				}

				// Ignore tweetId
				// tweetId = parts[0];

				userId = parts[1];
				time = parts[2];
				userIdTime = userId + ":" + time;

				for (int i = 3; i < parts.length; i++) {
					wordCount = parts[i].split(":");
					assert wordCount.length == 2;

					word = wordCount[0];
					count = wordCount[1];

					System.out.println(userIdTime + ":" + word + "\t" + count);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
