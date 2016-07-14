package Q3MapReduce02;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Remove Duplicate Tweets
 */
public class Q3Reducer2 {

	public static void main(String args[]) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			// Initialize Variables
			String input;
			String prevUserIdTimeWord = null;
			String currUserIdTimeWord = null;
			int prevCount = 0;
			int currCount = 0;
			String[] utw = null;
			String userId = null;
			String time = null;
			String word = null;

			// While we have input on stdin
			while ((input = br.readLine()) != null) {
				try {
					String[] parts = input.split("\t");
					// If no format is wrong, skip this line
					if (parts.length != 2) {
						continue;
					}

					currUserIdTimeWord = parts[0];
					currCount = Integer.parseInt(parts[1]);

					// We have sorted input, so check if we
					// are we on the same word?
					if (prevUserIdTimeWord != null && prevUserIdTimeWord.equals(currUserIdTimeWord)) {
						prevCount += currCount;
					} else {
						// The word has changed.
						// Is this the first word? If not, output count
						if (prevUserIdTimeWord != null) {
							utw = prevUserIdTimeWord.split(":");
							userId = utw[0];
							time = utw[1];
							word = utw[2];
							System.out.println(userId + "\t" + time + "\t" + word + "\t" + prevCount);
						}

						prevUserIdTimeWord = currUserIdTimeWord;
						prevCount = currCount;
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}

			// Print out last word if missed
			if (prevUserIdTimeWord != null && prevUserIdTimeWord.equals(currUserIdTimeWord)) {
				utw = prevUserIdTimeWord.split(":");
				userId = utw[0];
				time = utw[1];
				word = utw[2];
				System.out.println(userId + "\t" + time + "\t" + word + "\t" + prevCount);
			}

		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
