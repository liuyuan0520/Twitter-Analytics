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
 * Input Format: ID \t Hashtag \t Tweet \\n Tweet(2) \\n .... \\n Tweet(n)
 * Output Folder: myheartisinthework/Output/P2Q2HBaseMapReduce/v2 (T)
 * Output Format: ID \t ##Hashtag(1)##Tweet(1) \\n Tweet(2) \\n .... \\n Tweet(n)##Hashtag(1)####Hashtag(2)##Tweet(1) \\n Tweet(2) \\n .... \\n Tweet(n)##Hashtag(2)##
 */
public class P2Q2HBaseReducer2 {
    public static void main(String[] args) {
        try {

//            FileReader fileReader = new FileReader("part-00029");
//            BufferedReader br = new BufferedReader(fileReader);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String previousHashtag = null;
            String previousId = null;
            StringBuilder sb = new StringBuilder();

            String input;
            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");
                String id = parts[0];
                String hashtag = parts[1];
                String text = parts[2];

                if (id.equals(previousId)) {
                    if (hashtag.equals(previousHashtag)) {
                        sb.append(text);
                    } else {
                        sb.append("##").append(previousHashtag).append("##");
                        sb.append("##").append(hashtag).append("##");
                        sb.append(text);
                        previousHashtag = hashtag;
                    }
                } else {
                    sb.append("##").append(previousHashtag).append("##");
                    if (previousId != null) {
                        System.out.println(sb.toString());
                    }
                    sb = new StringBuilder().append(id).append("\t").append("##").append(hashtag).append("##").append(text);

                    previousHashtag = hashtag;
                    previousId = id;
                }
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
