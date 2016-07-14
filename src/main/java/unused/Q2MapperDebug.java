package unused;
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

public class Q2MapperDebug {
	/**
	 * Results from afinn.txt
	 */
	private HashMap<String, Integer> afinn;

	/**
	 * Results from common-english-word.txt
	 */
	private HashSet<String> stopWords;

	private HashSet<String> banned = new HashSet<>();

	private HashMap<String, String> errors = new HashMap<String, String>();

	private SimpleDateFormat twitterFormat;
	private SimpleDateFormat outputFormat;

	private static DecimalFormat decimalFormat = new DecimalFormat("#");

	public Q2MapperDebug() {
		loadErrors();
		loadAfinn();
		loadStopWords();
		createCensor();
		twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
		twitterFormat.setLenient(true);

		outputFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static void main(String args[]) {
		Q2MapperDebug mapper = new Q2MapperDebug();

		System.out.println(mapper.sentimentDensity(
				"RT @demis_laugh_tho: *Lovatics won*\nBecause every Lovatic that saw this RT'ed\n#FanArmy #Lovatics #DemiLovato #iHeartAwards @iHeartRadio htt…	FanArmy	Lovatics	DemiLovato	iHeartAwards"));

		String t = "Thu May 22 15:32:10 +0000 2014";
		try {
			Date d = mapper.twitterFormat.parse(t);
			System.out.println(d);
			System.out.println(mapper.outputFormat.format(d));

		} catch (Exception e) {
			e.printStackTrace();
		}

		String s = "fucking #Porn #Sex Daddy Fucked Me  Ate My Pussy: Duration : 17 minUrl : http://t.co/AXxjSCZHq6… http://t.co/7Bf87OHqiS #Sexcam sex";
		// System.out.print(mapper.censorText(s));
		// String s1 = "@jenniferhedger: Let's go boys. #WeTheNorth #GoRaps The
		// lining of my stomach has almost been eaten away.\" Tie game! No
		// lookin back now!!";
		// System.out.println(mapper.sentimentDensity(s1));
		// String s2 = "You eat when we say you eat. You shit when we say you
		// shit, and piss when we say you piss. You got that, you maggot-dick
		// motherfucker? #ShawshankRedemption #movies #BASTARD";
		// System.out.println(mapper.censorText(s2));
		// String s3 = "This\nIs\nEscape\tTest";
		// System.out.println(s3);
		// String s3escape = StringEscapeUtils.escapeJava(s3);
		// System.out.println(s3escape);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			boolean containsHashTag = false;
			while ((input = br.readLine()) != null) {
				JsonObject jsonObject;
				try {
					JsonElement jsonElement = new JsonParser().parse(input);
					jsonObject = jsonElement.getAsJsonObject();
				} catch (Exception e) {
					continue;
				}

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

				String text = jsonObject.getAsJsonPrimitive("text").getAsString();
				if (text == null || text.equals("")) {
					continue;
				}

				String tweetId = jsonObject.getAsJsonPrimitive("id").getAsString();
				if (tweetId == null || tweetId.equals("")) {
					tweetId = jsonObject.getAsJsonPrimitive("id_str").getAsString();
					if (tweetId == null || tweetId.equals("")) {
						continue;
					}
				}
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

				// debug
				if (!mapper.errors.containsKey(userId)) {
					continue;
				}

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

					// debug
					if (mapper.errors.get(userId).equals(tagText)) {
						containsHashTag = true;
					}
					hashTags.append("\t" + tagText);
				}

				// debug
				if (!containsHashTag) {
					continue;
				}
				containsHashTag = false;

				String sentimentDensity = mapper.sentimentDensity(text);
				String censoredText = mapper.censorText(text);

				// String escapeText =
				// StringEscapeUtils.escapeJava(censoredText);
				String escapeText = censoredText.replace("\\", "\\\\").replace("\t", "\\t").replace("\b", "\\\b")
						.replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f").replace("\"", "\\\"");

				System.out.println(userId + "\t" + "Original JSON:\t" + input + "Original Text:\t" + text
						+ "Original Time:\t" + jsonObject.getAsJsonPrimitive("created_at").getAsString()
						+ "Parsed Result:\t" + tweetId + "\t" + userId + "\t" + time + "\t" + sentimentDensity + "\t"
						+ escapeText + hashTags);
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
		// System.out.println();
		// System.out.println(text);
		// System.out.println("Sentiment score: " + sentimentScore);
		// System.out.println("Effective words: " + effectiveWordCount);

		// 4. Calculate Sentiment Density
		double sentimentDensity = effectiveWordCount == 0 ? 0.0 : 1.0 * sentimentScore / effectiveWordCount;
		String sdString = String.format("%.3f", sentimentDensity);

		return sdString;
	}

	private void loadAfinn() {
		afinn = new HashMap<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("afinn.txt"));
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

	private void loadStopWords() {
		stopWords = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("common-english-word.txt"));
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

	private void loadErrors() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("errors.txt"));
			String input;
			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");
				errors.put(parts[0], parts[1]);
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

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
			// The 2nd element of the list is the answer to Q2.

			// for (String s : banned) {
			// System.out.print(s + "\n");
			// }
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

				// System.out.println(temp);
				// System.out.println(builder.toString());
				// System.out.println(daxinwen);
			}
		}
		return daxinwen;
	}
}