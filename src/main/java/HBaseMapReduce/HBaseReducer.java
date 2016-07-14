package HBaseMapReduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This reducer is deprecated. Use HBaseReducer2.java instead.
 */
public class HBaseReducer {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while ((input = br.readLine()) != null) {
				System.out.println(input);
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
