import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ShannonFano {

	private static final int ASCII_LENGTH = 7;

	private String originalString;
	private int originalStringLength;
	private HashMap<Character, String> compressedResult;
	private HashMap<Character, Integer> characterFrequency;
	private double entropy;
	private double averageLengthBefore;
	private double averageLengthAfter;

	public ShannonFano(String str) {
		super();
		originalString = str;
		originalStringLength = str.length();
		characterFrequency = new HashMap<Character, Integer>();
		compressedResult = new HashMap<Character, String>();
		entropy = 0.0;
		averageLengthBefore = 0.0;
		averageLengthAfter = 0.0;

		this.calculateFrequency();
		this.compressString();
		this.calculateEntropy();
		this.calculateAverageLengthBeforeCompression();
		this.calculateAverageLengthAfterCompression();
	}

	private void compressString() {
		List<Character> charList = new ArrayList<Character>();

		Iterator<Entry<Character, Integer>> entries = characterFrequency.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Character, Integer> entry = entries.next();
			charList.add(entry.getKey());
		}

		appendBit(compressedResult, charList, true);
	}

	private void appendBit(HashMap<Character, String> result, List<Character> charList, boolean up) {
		String bit = "";
		if (!result.isEmpty()) {
			bit = (up) ? "0" : "1";
		}

		for (Character c : charList) {
			String s = (result.get(c) == null) ? "" : result.get(c);
			result.put(c, s + bit);
		}

		if (charList.size() >= 2) {
			int separator = (int) Math.floor((float) charList.size() / 2.0);

			List<Character> upList = charList.subList(0, separator);
			appendBit(result, upList, true);
			List<Character> downList = charList.subList(separator, charList.size());
			appendBit(result, downList, false);
		}
	}

	private void calculateFrequency() {
		for (int i = 0; i < originalString.length(); i++) {
			char c = originalString.charAt(i);
			Integer val = characterFrequency.get(new Character(c));
			if (val != null) {
				characterFrequency.put(c, new Integer(val + 1));
			} else {
				characterFrequency.put(c, 1);
			}
		}
	}

	private void calculateEntropy() {
		double probability = 0.0;
		for (Character c : originalString.toCharArray()) {
			probability = 1.0 * characterFrequency.get(c) / originalStringLength;
			entropy += probability * (Math.log(1.0 / probability) / Math.log(2));
		}
	}

	private void calculateAverageLengthBeforeCompression() {
		double probability = 0.0;
		for (Character c : originalString.toCharArray()) {
			probability = 1.0 * characterFrequency.get(c) / originalStringLength;
			averageLengthBefore += probability * ASCII_LENGTH;
		}
	}

	private void calculateAverageLengthAfterCompression() {
		double probability = 0.0;
		for (Character c : originalString.toCharArray()) {
			probability = 1.0 * characterFrequency.get(c) / originalStringLength;
			averageLengthAfter += probability * compressedResult.get(c).length();
		}
	}

	@Override
	public String toString() {
		String str = "";
		str += "Symbol\tWeight\tShannon-Fano Code\tASCII Code\n";
		str += "--------------------------------------------------\n";

		for (Character c : compressedResult.keySet()) {
			str += "'" + c + "'" + "\t" + characterFrequency.get(c) + "\t" + compressedResult.get(c) + "\t\t\t"
					+ Integer.toBinaryString((int) c);
			str += "\n";
		}
		str += "--------------------------------------------------\n";
		str += "Efficiency before Compression: " + 100 * (Math.round((entropy / averageLengthBefore) * 100.0) / 100.0)
				+ "%\n";
		str += "Efficiency after Compression: " + 100 * (Math.round((entropy / averageLengthAfter) * 100.0) / 100.0)
				+ "%\n";
		return str;
	}
}