package Q3MapReduce03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Remove Duplicate Tweets
 */
public class Q3Reducer3 {

    public static void main(String args[]) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // Initialize Variables
            String input;
            String prevUserIdTime = null;
            String currUserIdTime = null;

            String wordsCounts = null;

            String[] idAndTime = null;
            String userId = null;
            String time = null;


            StringBuilder sb = new StringBuilder();

            // While we have input on stdin
            while ((input = br.readLine()) != null) {
                try {
                    String[] parts = input.split("\t");
                    // If no format is wrong, skip this line
                    if (parts.length != 2) {
                        continue;
                    }
                    currUserIdTime = parts[0];
                    wordsCounts = parts[1];

                    // We have sorted input, so check if we
                    // are we on the same word?
                    if (prevUserIdTime != null && prevUserIdTime.equals(currUserIdTime)) {
                        sb = sb.append(";").append(wordsCounts);
                    } else {
                        // The word has changed.
                        // Is this the first word? If not, output count
                        if (prevUserIdTime != null) {
                            System.out.println(sb.toString());
                        }
                        idAndTime = currUserIdTime.split(":");
                        userId = idAndTime[0];
                        time = idAndTime[1];
                        sb = new StringBuilder().append(userId).append("\t").append(time).append("\t").append(wordsCounts);
                    }
                    prevUserIdTime = currUserIdTime;
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            // Print out last word if missed
            if (prevUserIdTime != null) {
                System.out.println(sb.toString());
            }

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
