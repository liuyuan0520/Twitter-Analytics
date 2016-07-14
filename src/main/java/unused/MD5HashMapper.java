package unused;
/**
 * Created by Windows User on 2016/3/16.
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to generate MD5 hash code, not used in our final solution
 */
public class MD5HashMapper {

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

    public static void main(String[] args) {
            String inputString = "583785634C14";
            System.out.println("Input String: " + inputString);

            String md5Hash = new MD5HashMapper().generateMD5(inputString);
            System.out.println("MD5 Hash: " + md5Hash);


    }
}
