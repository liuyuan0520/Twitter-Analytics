package MapReduce02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This reducer sorts tweets with the same userid and hashtag by sentiment
 * density, time and tweet ID, respectively. The output can be loaded into MySQL
 * database directly.
 *
 * This reducer outputted data to 15619phase1q2/MapReduce2Output/complete (Yuan) as final data for P1Q2MySQL
 *
 * Output Format:
 * Output Folder: 15619phase1q2/MapReduce2Output/complete (Y)
 */
public class Q2Reducer2 {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

	public static void main(String args[]) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			String previousId = null;

			Map<String, List<String[]>> hashtagToTweets = new HashMap<>();

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");
				String currentId = parts[0];
				String tag = parts[5];

				// Put tweets that share a same user ID in a hash map.
				if (currentId.equals(previousId)) {
					// Put tweets that share a same hashtag in an array list.
					if (hashtagToTweets.containsKey(tag)) {
						hashtagToTweets.get(tag).add(parts);
					} else {
						List<String[]> tweets = new ArrayList<>();
						tweets.add(parts);
						hashtagToTweets.put(tag, tweets);
					}
				}

				// Sort the map and create a new map if user ID is not the same
				// anymore.
				if (!currentId.equals(previousId)) {
					for (List<String[]> tweets : hashtagToTweets.values()) {
						// Sort the lists according to the provided rules and
						// output.
						Collections.sort(tweets, new TweetComparator());
						for (String[] tweet : tweets) {
							StringBuilder sb = new StringBuilder();
							String newTweet = sb.append(tweet[0]).append("\t").append(tweet[5]).append("\t")
									.append(tweet[1]).append("\t").append(tweet[2]).append("\t").append(tweet[3])
									.append("\t").append(tweet[4]).toString();
							System.out.println(newTweet);
						}
					}

					hashtagToTweets = new HashMap<>();
					List<String[]> tweets = new ArrayList<>();
					tweets.add(parts);
					hashtagToTweets.put(tag, tweets);
				}
				previousId = currentId;
			}
			// Deal with the last line
			for (List<String[]> tweets : hashtagToTweets.values()) {
				Collections.sort(tweets, new TweetComparator());
				for (String[] tweet : tweets) {
					StringBuilder sb = new StringBuilder();
					String newTweet = sb.append(tweet[0]).append("\t").append(tweet[5]).append("\t").append(tweet[1])
							.append("\t").append(tweet[2]).append("\t").append(tweet[3]).append("\t").append(tweet[4])
							.toString();
					System.out.println(newTweet);
				}
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	static class TweetComparator implements Comparator<String[]> {
		/*
		 * Sort data.
		 *
		 * @param s1, s2 two strings arrays that need to compared
		 *
		 * @return int whether the first string should be listed before the
		 * second string according to the numeric descending order of the number
		 * of accesses.
		 */
		@Override
		public int compare(String[] tweet1, String[] tweet2) {

			// Sort according to sentiment density.
			Double sentimentDensity1 = Double.parseDouble(tweet1[1]);
			Double sentimentDensity2 = Double.parseDouble(tweet2[1]);
			int compareDensity = sentimentDensity2.compareTo(sentimentDensity1);
			if (compareDensity != 0) {
				return compareDensity;
			}

			// Sort according to timestamp.
			Long time1 = new Long(0);
			Long time2 = new Long(0);
			try {
				time1 = sdf.parse(tweet1[2]).getTime();
				// System.out.print(time1);
				time2 = sdf.parse(tweet2[2]).getTime();
				// System.out.print(time2);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int compareTime = time1.compareTo(time2);
			if (compareTime != 0) {
				return compareTime;
			}

			// Sort according to Tweet ID.
			Long tweetId1 = Long.parseLong(tweet1[3]);
			Long tweetId2 = Long.parseLong(tweet2[3]);
			return tweetId1.compareTo(tweetId2);
		}
	}
}
