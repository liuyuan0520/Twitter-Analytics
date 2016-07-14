package unused;



import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by liuyuan on 3/6/16.
 */
public class ParseTweet {

    private static DecimalFormat decimalFormat = new DecimalFormat("#");


    public static void main(String[] args) {
        try {
            String fileName = "part-00000";

            // Read the answers from the file "answers" (which is generated from
            // Filter.java) and put them in a list
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String answer;

            int i = 0;

            while ((answer = br.readLine()) != null) {
                if (i < 1000) {

                    Map<String, Object> javaRootMapObject = new Gson().fromJson(answer, Map.class);

                    String time = (String) javaRootMapObject.get("created_at");
                    System.out.println("Time is " + time);
                    BigDecimal bigDecimal = new BigDecimal((Double) javaRootMapObject.get("id"));
                    String tweetId = decimalFormat.format(bigDecimal);
                    System.out.println("Tweet ID is " + tweetId);
                    String tweetId_str = (String) javaRootMapObject.get("id_str");
                    System.out.println("Tweet ID is " + tweetId_str);
                    String text = (String) javaRootMapObject.get("text");
                    System.out.println("Text is " + text);
                    String userId = decimalFormat.format(((Map)(javaRootMapObject.get("user"))).get("id"));
                    System.out.println("User ID is " + userId);
                    String userId_str = (String)((Map)(javaRootMapObject.get("user"))).get("id_str");
                    System.out.println("User ID is " + userId_str);

                    List<Map<String, String>> list = (ArrayList<Map<String, String>>)((Map)(javaRootMapObject.get("entities"))).get("hashtags");
                    ArrayList<String> hashtagList  = null;
                    if (list != null) {
                        for (Map map : list) {
                            hashtagList.add((String) map.get("text"));
//                            Iterator it = map.entrySet().iterator();
//                            while (it.hasNext()) {
//                                Collection c = map.values();
//                                Iterator itr = c.iterator();
//                                while (itr.hasNext()) {
//                                    System.out.println(itr.next());
//                                }
//                            }
                        }
                        System.out.println("Hashtag(s) is(are) ");
                        for (String s : hashtagList) {
                            System.out.println(s);
                        }
                    } else {
                        System.out.println("No hashtag");
                        hashtagList = null;
                    }
                    //System.out.print(answer + "\n");
                    i++;
                    System.out.println("---------------------");
                } else {
                    break;
                }
            }
            // The 2nd element of the list is the answer to Q2.
            br.close();
        } catch (IOException e) {
            System.out.println();
            System.out.println(e);
            return;
        }
    }
}

