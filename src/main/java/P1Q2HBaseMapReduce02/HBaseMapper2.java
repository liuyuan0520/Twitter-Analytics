package P1Q2HBaseMapReduce02;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Windows User on 2016/3/14.
 */

/**
 *
 * This mapper is used to merge the userId and Hash tag into one column
 * And use the combined string as rowkey in Hbase.
 * Also, in the reducer HBaseReducer2, it combined all the other info into one column and format them as required.
 *
 * Input Folder: 15619phase1q2/MapReduce2Output/complete (Y)
 */
public class HBaseMapper2 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");
                String userIdHashtag = parts[0] + parts[1];

                System.out.println(userIdHashtag + "\t" + parts[2] + "\t" + parts[3] + "\t" + parts[4] + "\t" + parts[5]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
