package MapReduce02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * This mapper removes rows without hastag or leaves only one row if a tweet
 * contains same hashtags.
 *
 * Input Folder: 15619phase1q2/complete-6 (Y)
 */
public class Q2Mapper2 {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");

				// If no hashtag is found, skip this row.
				if (parts.length < 6) {
					continue;
				}

				String tweetId = parts[0];
				String userId = parts[1];
				String time = parts[2];
				String sentimentDensity = parts[3];
				String censoredText = parts[4];

				// Prevent duplicate tags in one tweet
				HashSet<String> tags = new HashSet<String>();
				for (int i = 5; i < parts.length; i++) {
					tags.add(parts[i]);
				}

				for (String hashTag : tags) {
					// Rearrange columns and output to stdout
					System.out.println(userId + "\t" + sentimentDensity + "\t" + time + "\t" + tweetId + "\t"
							+ censoredText + "\t" + hashTag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
