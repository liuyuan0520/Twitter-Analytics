package P2Q2HBaseMapReduce01;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Windows User on 2016/3/14.
 */

/**
 *
 * Input Folder: 15619phase1q2/MapReduce2Output/complete2 (Y)
 * Input Format:
 * Input Remark: Input is the final data for P1Q2MySQL
 * Output Format:  ID \t Hashtag \t Tweet \\n Tweet(2) \\n .... \\n Tweet(n)
 */
public class P2Q2HBaseMapper1 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");
                String userIdHashtag = parts[0] + "#" + parts[1];

                System.out.println(userIdHashtag + "\t" + parts[2] + "\t" + parts[3] + "\t" + parts[4] + "\t" + parts[5]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
