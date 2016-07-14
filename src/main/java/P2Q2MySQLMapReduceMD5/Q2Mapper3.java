package P2Q2MySQLMapReduceMD5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

/**
 * Created by Windows User on 2016/3/25.
 */
public class Q2Mapper3 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;

            while ((input = br.readLine()) != null) {
                String[] parts = input.split("\t");

                // If no hashtag is found, skip this row.
                if (parts.length < 6) {
                    continue;
                }

                String tweetId = parts[0];
                String userId = parts[1];
                String time = parts[2];
                String sentimentDensity = parts[3];
                String censoredText = parts[4];

                // Prevent duplicate tags in one tweet
                HashSet<String> tags = new HashSet<String>();
                for (int i = 5; i < parts.length; i++) {
                    tags.add(parts[i]);
                }

                for (String hashTag : tags) {
                    // cal md5 of userid+hashtag
                    String col1 = generateMD5(userId + hashTag);
                    // Rearrange columns and output to stdout
                    System.out.println(col1 + "\t" + sentimentDensity + "\t" + time + "\t" + tweetId + "\t"
                            + censoredText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateMD5(String message)  {
        return hashString(message, "MD5");
    }

    private static String hashString(String message, String algorithm) {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

            return convertByteArrayToHexString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "NoSuchAlgorithmException";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "UnsupportedEncodingException";
        }
    }
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
