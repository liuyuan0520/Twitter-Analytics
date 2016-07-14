package Q3MapReduce01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Windows User on 2016/3/23.
 */
public class Q3Reducer {
    public static void main(String args[]) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            String previousId = null;
            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");
                String currentId = parts[0];

                // Avoid printing duplicate tweets
                if (!currentId.equals(previousId) && !parts[3].equals("")) {
                    System.out.println(input);
                }

                previousId = currentId;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
