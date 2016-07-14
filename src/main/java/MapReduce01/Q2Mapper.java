package MapReduce01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This mapper parses raw data from Twitter as JSON. It then filters malformed
 * tweets, calculated the sentiment density of a tweet, and censors it if it
 * contains banned words.
 *
 * This mapper took original data as input.
 */
public class Q2Mapper {
	/**
	 * Results from afinn.txt
	 */
	private HashMap<String, Integer> afinn;

	/**
	 * Results from common-english-word.txt
	 */
	private HashSet<String> stopWords;

	private HashSet<String> banned = new HashSet<>();

	private SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
	private SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	private static DecimalFormat decimalFormat = new DecimalFormat("#");

	public Q2Mapper() {
		loadAfinn();
		loadStopWords();
		createCensor();
		twitterFormat.setLenient(true);
		outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static void main(String args[]) {
		Q2Mapper mapper = new Q2Mapper();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while ((input = br.readLine()) != null) {
				JsonObject jsonObject;
				// Parse Json file
				try {
					JsonElement jsonElement = new JsonParser().parse(input);
					jsonObject = jsonElement.getAsJsonObject();
				} catch (Exception e) {
					continue;
				}

				// Get rid of the lines without time stamps
				String time = jsonObject.getAsJsonPrimitive("created_at").getAsString();
				if (time == null || time.equals("")) {
					continue;
				}
				try {
					Date date = mapper.twitterFormat.parse(time);
					time = mapper.outputFormat.format(date);
				} catch (Exception e) {
					continue;
				}

				// Get rid of lines without text.
				String text = jsonObject.getAsJsonPrimitive("text").getAsString();
				if (text == null || text.equals("")) {
					continue;
				}

				// Get rid of lines without tweet id.
				String tweetId = jsonObject.getAsJsonPrimitive("id").getAsString();
				if (tweetId == null || tweetId.equals("")) {
					tweetId = jsonObject.getAsJsonPrimitive("id_str").getAsString();
					if (tweetId == null || tweetId.equals("")) {
						continue;
					}
				}

				// Get rid of lines without user id.
				JsonObject userObject = jsonObject.getAsJsonObject("user");
				if (userObject == null || userObject.equals("")) {
					continue;
				}
				String userId = userObject.getAsJsonPrimitive("id").getAsString();
				if (userId == null || userId.equals("")) {
					userId = userObject.getAsJsonPrimitive("id_str").getAsString();
					if (userId == null || userId.equals("")) {
						continue;
					}
				}

				// Get rid of lines without hashtag.
				JsonObject entitiesObject = jsonObject.getAsJsonObject("entities");
				if (entitiesObject == null || entitiesObject.equals("")) {
					continue;
				}
				JsonArray hashtagArray = entitiesObject.getAsJsonArray("hashtags");
				StringBuilder hashTags = new StringBuilder();
				for (JsonElement hashtagElement : hashtagArray) {
					JsonObject hashtagObject = hashtagElement.getAsJsonObject();
					String tagText = hashtagObject.getAsJsonPrimitive("text").getAsString();
					if (tagText == null || tagText.equals("")) {
						continue;
					}
					hashTags.append("\t" + tagText);
				}

				// Calculate sentiment density
				String sentimentDensity = mapper.sentimentDensity(text);
				// Censor the text
				String censoredText = mapper.censorText(text);
				// Replace text to escape
				String escapeText = censoredText.replace("\\", "\\\\").replace("\t", "\\t").replace("\b", "\\\b")
						.replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f").replace("\"", "\\\"");

				System.out.println(
						tweetId + "\t" + userId + "\t" + time + "\t" + sentimentDensity + "\t" + escapeText + hashTags);
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	/**
	 * This method calculates the sentiment density of a tweet.
	 */
	private String sentimentDensity(String text) {
		// Four steps to calculate sentiment density:
		// 1. Text Split
		// 2. Calculate Sentiment Score
		// 3. Calculate Effective Word Count
		// 4. Calculate Sentiment Density

		// 1. Text Split:
		String[] parts = text.split("[^a-zA-Z0-9]+");

		// 2. Calculate Sentiment Score
		int sentimentScore = 0;
		for (String word : parts) {
			String wordLowercase = word.toLowerCase();
			// System.out.println(wordLowercase);
			if (afinn.containsKey(wordLowercase)) {
				sentimentScore += afinn.get(wordLowercase);
			}
		}

		// 3. Calculate Effective Word Count
		int effectiveWordCount = 0;
		for (String word : parts) {
			if (!word.equals("") && !stopWords.contains(word.toLowerCase())) {
				// System.out.print(word.toLowerCase() + "\t");
				effectiveWordCount++;
			}
		}

		// 4. Calculate Sentiment Density
		double sentimentDensity = effectiveWordCount == 0 ? 0.0 : 1.0 * sentimentScore / effectiveWordCount;
		String sdString = String.format("%.3f", sentimentDensity);

		return sdString;
	}

	// Load sentiment density instruction
	private void loadAfinn() {
		afinn = new HashMap<String, Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader("afinn.txt"))) {
			String input;
			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");
				String word = parts[0];
				int score = Integer.parseInt(parts[1]);
				afinn.put(word, score);
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	// Load stop words
	private void loadStopWords() {
		stopWords = new HashSet<String>();
		try (BufferedReader br = new BufferedReader(new FileReader("common-english-word.txt"))) {
			String input;
			while ((input = br.readLine()) != null) {
				String[] parts = input.split(",");
				for (String word : parts) {
					stopWords.add(word);
				}
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	// Create hashset of censored words from the given file.
	private void createCensor() {
		try {
			String fileName = "banned.txt";

			// Read the answers from the file "answers" (which is generated from
			// Filter.java) and put them in a list
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String answer;

			while ((answer = br.readLine()) != null) {
				String newString = "";
				for (int i = 0; i < answer.length(); i++) {
					int ascii = answer.charAt(i);
					if ((ascii <= 109) && (ascii >= 97)) {
						ascii = ascii + 13;
						newString = newString + Character.toString((char) ascii);
					} else if ((ascii <= 122) && (ascii >= 110)) {
						ascii = ascii - 13;
						newString = newString + Character.toString((char) ascii);
					} else {
						newString = newString + Character.toString((char) ascii);
					}
				}
				banned.add(newString);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns the censored text of a tweet. Replace inner
	 * characters by asterisks.
	 */
	private String censorText(String daxinwen) {
		// 1. Text Split:
		String[] parts = daxinwen.split("[^a-zA-Z0-9]+");
		for (String temp : parts) {
			String lowercase = temp.toLowerCase();
			if (banned.contains(lowercase)) {
				StringBuilder builder = new StringBuilder();
				builder.append(temp.charAt(0));
				for (int i = 0; i < temp.length() - 2; i++) {
					builder.append('*');
				}
				builder.append(temp.charAt(temp.length() - 1));

				String censoredString = builder.toString();
				// System.out.print(censoredString);
				// daxinwen = daxinwen.replace(temp, censoredString);

				Pattern p = Pattern.compile("[^a-zA-Z0-9]" + temp + "[^a-zA-Z0-9]");
				Matcher m = p.matcher(daxinwen);
				while (m.find()) {
					int pos = m.start();

					// check whether pos is matched to an emoji
					if (daxinwen.charAt(pos + 1) != temp.charAt(0)) {
						pos++;
					}

					daxinwen = daxinwen.substring(0, pos + 1) + censoredString
							+ daxinwen.substring(pos + 1 + temp.length(), daxinwen.length());
					m = p.matcher(daxinwen);
				}

				Pattern pStart = Pattern.compile("(^)" + temp + "([^a-zA-Z0-9])");
				Matcher mStart = pStart.matcher(daxinwen);
				if (mStart.find()) {
					daxinwen = censoredString + daxinwen.substring(temp.length(), daxinwen.length());
				}

				Pattern pEnd = Pattern.compile("[^a-zA-Z0-9]" + temp + "($)");
				Matcher mEnd = pEnd.matcher(daxinwen);
				if (mEnd.find()) {
					int pos = mEnd.start();

					// check whether pos is matched to an emoji
					if (daxinwen.charAt(pos + 1) != temp.charAt(0)) {
						pos++;
					}

					daxinwen = daxinwen.substring(0, pos + 1) + censoredString;
				}

				Pattern pOnly = Pattern.compile("(^)" + temp + "($)");
				Matcher mOnly = pOnly.matcher(daxinwen);
				if (mOnly.find()) {
					daxinwen = censoredString;
				}
			}
		}
		return daxinwen;
	}
}