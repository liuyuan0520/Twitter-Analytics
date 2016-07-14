package Q3MapReduce01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Windows User on 2016/3/23.
 */
public class Q3Mapper {

	private static HashSet<String> stopWords = new HashSet<String>();

	private static HashSet<String> banned = new HashSet<>();

	private SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
	private SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

	public Q3Mapper() {
		loadStopWords();
		createCensor();
		twitterFormat.setLenient(true);
		outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static void main(String[] args) {
		Q3Mapper mapper = new Q3Mapper();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		try {
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
				if (jsonObject.getAsJsonPrimitive("created_at") == null)
					continue;
				String time = jsonObject.getAsJsonPrimitive("created_at").getAsString();
				if (time == null || time.equals("")) {
					continue;
				}
				// parse the time
				try {
					Date date = mapper.twitterFormat.parse(time);
					time = mapper.outputFormat.format(date);
				} catch (Exception e) {
					continue;
				}

				// Get rid of lines without text.
				if (jsonObject.getAsJsonPrimitive("text") == null)
					continue;
				String text = jsonObject.getAsJsonPrimitive("text").getAsString();
				if (text == null || text.equals("")) {
					continue;
				}

				// Get rid of lines without tweet id.
				if (jsonObject.getAsJsonPrimitive("id") == null && jsonObject.getAsJsonPrimitive("id_str") == null) {
					continue;
				}
				String tweetId = null;
				if (jsonObject.getAsJsonPrimitive("id") == null) {
					// id_str could be null
					tweetId = jsonObject.getAsJsonPrimitive("id_str").getAsString();
					if (tweetId == null || tweetId.equals("")) {
						continue;
					}
				} else {
					tweetId = jsonObject.getAsJsonPrimitive("id").getAsString();
					if (tweetId == null || tweetId.equals("")) {
						continue;
					}
				}

				// Get rid of lines without user id.
				JsonObject userObject = jsonObject.getAsJsonObject("user");
				if (userObject == null || userObject.equals("")) {
					continue;
				}

				// if (userObject.getAsJsonPrimitive("id") == null) continue;
				if (userObject.getAsJsonPrimitive("id") == null && userObject.getAsJsonPrimitive("id_str") == null) {
					continue;
				}
				String userId = null;
				if (userObject.getAsJsonPrimitive("id") == null) {
					// id_str could be null
					userId = userObject.getAsJsonPrimitive("id_str").getAsString();
					if (userId == null || userId.equals("")) {
						continue;
					}
				} else {
					userId = userObject.getAsJsonPrimitive("id").getAsString();
					if (userId == null || userId.equals("")) {
						continue;
					}
				}

				// Get rid of lines that is not English
				if (jsonObject.getAsJsonPrimitive("lang") == null) {
					continue;
				}
				String lang = jsonObject.getAsJsonPrimitive("lang").getAsString();
				if (lang == null || !lang.equals("en")) {
					continue;
				}

				// Erase short url
				String eraseShortUrl = text.replaceAll("(https?|ftp):\\/\\/[^\\s/$.?#][^\\s]*", "");
				// split words
				String[] parts = eraseShortUrl.split("[^a-zA-Z0-9]+");

				// change all words to lower case
				for (int i = 0; i < parts.length; i++) {
					parts[i] = parts[i].toLowerCase();
				}

				Arrays.sort(parts);

				StringBuilder textOutput = new StringBuilder();
				String prev = null;
				int count = 1;
				for (String curr : parts) {
					// skip empty string, stop words or banned word
					if (curr.equals("") || stopWords.contains(curr) || banned.contains(curr)) {
						continue;
					}

					// init prev
					if (prev == null) {
						prev = curr;
						continue;
					}
					// same word
					if (curr.equals(prev)) {
						count++;
					} else {// different word
						textOutput.append(prev + ":" + count + "\t");
						count = 1;
						prev = curr;
					}
				}

				// if text consists of empty string or stop words
				// such as "Who are you?"
				if (prev == null) {
					continue;
				}

				textOutput.append(prev + ":" + count);
				System.out.println(tweetId + "\t" + userId + "\t" + time + "\t" + textOutput.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Load stop words
	private void loadStopWords() {

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

}