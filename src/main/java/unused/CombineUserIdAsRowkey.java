package unused;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CombineUserIdAsRowkey {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			String prevUserId = null;
			StringBuilder sb = null;

			while ((input = br.readLine()) != null) {
				String[] parts = input.split("\t");

				// If no format is wrong, skip this row.
				if (parts.length != 3) {
					continue;
				}

				String currUserId = ("0000000000" + parts[0]).substring(parts[0].length());
				String date = parts[1];
				String wordCounts = parts[2];

				// We have sorted input, so check if we
				// are we on the same word?
				if (prevUserId != null && prevUserId.equals(currUserId)) {
					sb = sb.append(",").append(date).append(";").append(wordCounts);
				} else {
					// The word has changed.
					// Is this the first word? If not, output count
					if (prevUserId != null) {
						System.out.println(sb.append(";").toString());
					}

					sb = new StringBuilder().append(currUserId).append("\t").append(date).append(";")
							.append(wordCounts);
					prevUserId = currUserId;
				}
			}

			// Print out last word if missed
			if (prevUserId != null) {
				System.out.println(sb.append(";").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
