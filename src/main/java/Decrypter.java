import java.math.BigInteger;

/**
 * This is a helper class to decrypt the message given key and cipher.
 */
public class Decrypter {
	private static BigInteger SECRET_KEY = new BigInteger(
			"64266330917908644872330635228106713310880186591609208114244758680898150367880703152525200743234420230");

	public String decrypt(String keyString, String message) {
		BigInteger key = new BigInteger(keyString);
		BigInteger gcd = key.gcd(SECRET_KEY);

		int interKey = 1 + (gcd.intValue() % 25);

		StringBuilder cipherText = spiralToOrigin(message, (int) Math.sqrt(message.length()));

		StringBuilder decryptedText = shiftBack(cipherText.toString(), interKey);
		return decryptedText.toString();
	}

	private StringBuilder shiftBack(String input, int steps) {
		char[] result = input.toCharArray();
		for (int i = 0; i < result.length; i++) {
			result[i] = (char) ('A' + (26 + (result[i] - 'A') - steps) % 26);
		}
		return new StringBuilder().append(result);
	}

	private StringBuilder spiralToOrigin(String input, int z) {
		int len = input.length();
		char[] spiral = input.toCharArray();
		int side = (int) Math.sqrt(len);
		char[] result = new char[len];
		// i and j are the current 2D index;
		for (int i = 0, j = 0, k = 0; k < side * side; k++) {
			// the kth character in origin should be the (i*n+j)th in the spiral
			result[k] = spiral[i * side + j];

			// observe diagonal in the matrix
			// move right
			if (i <= j + 1 && i + j < side - 1) {
				j++;
			}
			// move down
			else if (i < j && i + j >= side - 1) {
				i++;
			}
			// move left
			else if (i >= j && i + j >= side) {
				j--;
			}
			// move up
			else {
				i--;
			}
		}
		return new StringBuilder().append(result);
	}
}
