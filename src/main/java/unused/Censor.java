package unused;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

public class Censor {

    private static HashSet<String> set = new HashSet<>();
    /**
     * Get and output the answer to Q2.
     */
    public static void main(String[] args) {
        createCensor();
    }

    private static String censor (String daxinwen) {
        String lowercase = daxinwen.toLowerCase();
        if (set.contains(lowercase)) {
            StringBuilder builder = new StringBuilder();
            builder.append(daxinwen.charAt(0));
            for (int i = 0; i < daxinwen.length() - 2; i++) {
                builder.append('*');
            }
            builder.append(daxinwen.charAt(daxinwen.length() - 1));
            String censoredString = builder.toString();
            System.out.print(censoredString);
            return censoredString;
        }
        else {
            System.out.print(daxinwen);
            return daxinwen;
        }
    }

    private static void createCensor () {
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
                set.add(newString);
            }
            // The 2nd element of the list is the answer to Q2.

            for (String s : set) {
                System.out.print(s + "\n");
            }
            br.close();
        } catch (IOException e) {
            System.out.println();
            System.out.println(e);
            return;
        }
    }
}
