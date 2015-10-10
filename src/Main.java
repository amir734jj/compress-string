import java.util.HashMap;

public class Main {

	public static void main(String[] args) {

		String givenString = "what are the indications for getting a digoxin level?";

		/*
		 * Construct frequency based on repetition in given string
		 */
		ShannonFano sfc = new ShannonFano(givenString);
		System.out.println(sfc);

		HuffmanCode hfc = new HuffmanCode(givenString);
		System.out.println(hfc);

		/*
		 * Construct frequency based on Length of original string by given
		 * probability
		 */

		HashMap<Character, Double> probability = new HashMap<Character, Double>();

		probability.put(' ', 0.1686);
		probability.put('e', 0.1031);
		probability.put('t', 0.0796);
		probability.put('a', 0.0642);
		probability.put('o', 0.0632);
		probability.put('i', 0.0575);
		probability.put('n', 0.0574);
		probability.put('s', 0.0514);
		probability.put('r', 0.0484);
		probability.put('h', 0.0467);
		probability.put('l', 0.0321);
		probability.put('d', 0.0317);
		probability.put('u', 0.0228);
		probability.put('c', 0.0218);
		probability.put('f', 0.0208);
		probability.put('m', 0.0198);
		probability.put('w', 0.0175);
		probability.put('?', 0.0173);
		probability.put('y', 0.0164);
		probability.put('p', 0.0152);
		probability.put('g', 0.0152);
		probability.put('b', 0.0127);
		probability.put('v', 0.0083);
		probability.put('k', 0.0049);
		probability.put('x', 0.0013);
		probability.put('q', 0.0008);
		probability.put('j', 0.0008);
		probability.put('z', 0.0005);

		ShannonFano sfcx = new ShannonFano(givenString, probability);
		System.out.println(sfcx);

		HuffmanCode hfcx = new HuffmanCode(givenString, probability);
		System.out.println(hfcx);

	}

}
