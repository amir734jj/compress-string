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
	private HashMap<Character, Double> characterFrequency;
	private double entropy;
	private double averageLengthBefore;
	private double averageLengthAfter;
	private boolean probabilityIsGiven;

	public ShannonFano(String str) {
		super();
		originalString = str;
		originalStringLength = str.length();
		characterFrequency = new HashMap<Character, Double>();
		compressedResult = new HashMap<Character, String>();
		entropy = 0.0;
		averageLengthBefore = 0.0;
		averageLengthAfter = 0.0;
		probabilityIsGiven = false;

		this.calculateFrequency();
		this.compressString();
		this.calculateEntropy();
		this.calculateAverageLengthBeforeCompression();
		this.calculateAverageLengthAfterCompression();

	}

	public ShannonFano(String str, HashMap<Character, Double> probablity) {
		super();
		originalString = str;
		originalStringLength = str.length();

		characterFrequency = new HashMap<Character, Double>();

		double checkPoint = 0;
		for (Character c : originalString.toCharArray()) {
			checkPoint += probablity.get(c);
			characterFrequency.put(c, originalStringLength * probablity.get(c));
		}

		assert checkPoint == 1.0; // Invariant, make sure sum of probabilities
									// is 1

		compressedResult = new HashMap<Character, String>();
		entropy = 0.0;
		averageLengthBefore = 0.0;
		averageLengthAfter = 0.0;
		probabilityIsGiven = true;

		this.compressString();
		this.calculateEntropy();
		this.calculateAverageLengthBeforeCompression();
		this.calculateAverageLengthAfterCompression();

	}

	private void compressString() {
		List<Character> charList = new ArrayList<Character>();

		Iterator<Entry<Character, Double>> entries = characterFrequency.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Character, Double> entry = entries.next();
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
		for (Character c : originalString.toCharArray()) {
			if (characterFrequency.containsKey(c)) {
				characterFrequency.put(c, new Double(characterFrequency.get(c) + 1.0));
			} else {
				characterFrequency.put(c, 1.0);
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

	@SuppressWarnings("unchecked")
	public HashMap<Character, Double> getCharacterFrequency() {
		return (HashMap<Character, Double>) characterFrequency.clone();
	}

	@SuppressWarnings("unchecked")
	public HashMap<Character, String> getCompressedResult() {
		return (HashMap<Character, String>) compressedResult.clone();
	}

	@Override
	public String toString() {
		String str = "";
		str += "*** Probability is" + (probabilityIsGiven ? " " : " Not ") + "Given. "
				+ (probabilityIsGiven ? "We did not calculate the probability."
						: "Probability was calculated using frequency of each character in the given String.")
				+ "\n";
		str += "Original String: \"" + originalString + "\"\n";
		str += "------------------------------------------------------------------------\n";
		str += "Symbol\t\tFrequency\tProbability\tShannon-F Code\tASCII Code\n";
		str += "------------------------------------------------------------------------\n";

		for (Character c : compressedResult.keySet()) {
			str += "'" + c + "'" + "\t\t" + Math.round(characterFrequency.get(c) * 100.0) / 100.0 + "\t\t"
					+ Math.round(characterFrequency.get(c) / originalStringLength * 10000.0) / 10000.0 + "\t\t"
					+ compressedResult.get(c) + "\t\t" + Integer.toBinaryString((int) c);
			str += "\n";
		}
		str += "------------------------------------------------------------------------\n";
		str += "Efficiency before Compression: " + 100 * (Math.round((entropy / averageLengthBefore) * 100.0) / 100.0)
				+ "%\n";
		str += "Efficiency after Compression: " + 100 * (Math.round((entropy / averageLengthAfter) * 100.0) / 100.0)
				+ "%\n";
		str += "------------------------------------------------------------------------\n";
		return str;
	}
}