package unused;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Windows User on 2016/3/25.
 */
public class Q2Reducer3 {
    public static void main(String args[]) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            For local test;
//            FileReader fileReader = new FileReader("test2.txt");
//            BufferedReader br = new BufferedReader(fileReader);
            String input;
            String previousId = null;

            Map<String, List<String[]>> hashtagToTweets = new HashMap<>();

            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");
                String idAndTag = parts[0];
                // Different UserId, add
                if (!idAndTag.equals(previousId)) {
                    if (previousId != null) {
                        //reduce current Id to the end
                        StringBuilder sb = new StringBuilder(previousId + "\t");
                        for (List<String[]> tweets : hashtagToTweets.values()) {
                            // sort tweets according to the requirement
                            Collections.sort(tweets, new TweetComparator());
                            // The last \\n should be replaced as \n in front end
                            for (String[] tweet : tweets) {
                                sb.append(tweet[1]).append(":").append(tweet[2]).append(":").append(tweet[3])
                                        .append(":").append(tweet[4]).append("\\n");
                            }
                        }
                        System.out.println(sb.substring(0, sb.length() - 2).toString());
                    }
                    hashtagToTweets = new HashMap<>();
                    List<String[]> tweets = new ArrayList<>();
                    tweets.add(parts);
                    hashtagToTweets.put(idAndTag, tweets);
                    previousId = idAndTag;
                } else {
                    hashtagToTweets.get(previousId).add(parts);
                }

            }
            StringBuilder sb = new StringBuilder(previousId + "\t");
            for (List<String[]> tweets : hashtagToTweets.values()) {
                Collections.sort(tweets, new TweetComparator());
                for (String[] tweet : tweets) {
                    sb.append(tweet[1]).append(":").append(tweet[2]).append(":")
                            .append(tweet[3]).append(":").append(tweet[4]).append("\\n");
                }
            }
            System.out.println(sb.substring(0, sb.length() - 2).toString());

        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    /**
     * Comparator used in reducer.
     */
    private static class TweetComparator implements Comparator<String[]> {
        private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        /*
         * Sort filtered data in the numeric descending order of the number of
         * accesses.
         *
         * @param s1, s2 two strings that need to compared
         *
         * @return int whether the first string should be listed before the
         * second string according to the numeric descending order of the number
         * of accesses.
         */
        @Override
        public int compare(String[] tweet1, String[] tweet2) {
            // The second element of each line indicates the number of accesses.
            Double sentimentDensity1 = Double.parseDouble(tweet1[1]);
            Double sentimentDensity2 = Double.parseDouble(tweet2[1]);
            int compareDensity = sentimentDensity2.compareTo(sentimentDensity1);
            if (compareDensity != 0) {
                return compareDensity;
            }

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

            Long tweetId1 = Long.parseLong(tweet1[3]);
            Long tweetId2 = Long.parseLong(tweet2[3]);
            return tweetId1.compareTo(tweetId2);
        }
    }

}
