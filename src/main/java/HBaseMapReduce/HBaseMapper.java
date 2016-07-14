package HBaseMapReduce;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This mapper is deprecated. Use HBaseMapper2.java instead.
 */
public class HBaseMapper {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");

				// // If no hashtag is found, skip this row.
				// if (parts.length < 6) {
				// continue;
				// }

				String userIdHashtag = parts[0] + parts[1];

				// Rearrange columns and output to stdout
				System.out
						.println(userIdHashtag + "\t" + parts[2] + "\t" + parts[3] + "\t" + parts[4] + "\t" + parts[5]);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
