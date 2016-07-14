package MapReduce01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This reducer removes duplicate tweets.
 *
 * This reducer outputted to COMPLETE6
 *
 * Output data format:
 */
public class Q2Reducer {
	public static void main(String args[]) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			String previousId = null;
			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");
				String currentId = parts[0];

				// Avoid printing duplicate tweets
				if (!currentId.equals(previousId)) {
					System.out.println(input);
				}

				previousId = currentId;
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
