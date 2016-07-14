package P2Q2HBaseMapReduce02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by Windows User on 2016/3/14.
 */

/**
 *
 *
 * Input format: ID#Hashtag \t Tweet(1) \\n Tweet(2) \\n .... \\n Tweet(n)
 * Input Folder: myheartisinthework/Output/P2Q2HBaseMapReduce/v1 (T)
 * Input Remark: Input is the 1st round output data of P2Q2HBaseMR
 * Output Format: ID \t Hashtag \t Tweet(1) \\n Tweet(2) \\n .... \\n Tweet(n)
 */
public class P2Q2HBaseMapper2 {
    public static void main(String[] args) {
        try {
//            FileReader fileReader = new FileReader("part-00029");
//            BufferedReader br = new BufferedReader(fileReader);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = br.readLine()) != null) {

                String[] parts = input.split("\t");
                String idHashtag = parts[0];

                String[] idAndHashtag = idHashtag.split("#");
                String id = idAndHashtag[0];
                String hashtag = idAndHashtag[1];

                System.out.println(id + "\t" + hashtag + "\t" + parts[1]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
