package unused;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CombineUserIdDate {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");

				// If no format is wrong, skip this row.
				if (parts.length != 3) {
					continue;
				}

				String userId = parts[0];
				String date = parts[1];
				String wordCounts = parts[2];

				String userIdDate = ("0000000000" + userId).substring(userId.length()) + date;

				System.out.println(userIdDate + "\t" + userId + "\t" + date + "\t" + wordCounts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
