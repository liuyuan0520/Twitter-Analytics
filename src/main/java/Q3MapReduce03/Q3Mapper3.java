package Q3MapReduce03;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//
public class Q3Mapper3 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // Initialize Variables
            String input;
            String userId = null;
            String time = null;
            String newString = null;
            String[] wordCount = null;
            String word = null;
            String count = null;
            String year = null;
            String month = null;
            String day = null;

            while ((input = br.readLine()) != null) {

                String[] parts = input.split("\t");
                // If no format is wrong, skip this line
                if (parts.length != 4) {
                    continue;
                }

                // Ignore tweetId
                // tweetId = parts[0];

                time = parts[1];

                // 2014-01-08
                year = time.substring(0, 4);
                month = time.substring(5, 7);
                day = time.substring(8);

                newString = new StringBuilder().append(parts[0]).append(":").append(year).append(month).append(day).append("\t").append(parts[2]).append(":").append(parts[3]).toString();
                System.out.println(newString.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
