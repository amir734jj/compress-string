import java.util.HashMap;
import java.util.PriorityQueue;

abstract class HuffmanTree implements Comparable<HuffmanTree> {
	public final double frequency;

	public HuffmanTree(double freq) {
		frequency = freq;
	}

	public int compareTo(HuffmanTree tree) {
		return Double.compare(frequency, tree.frequency);
	}
}

class HuffmanLeaf extends HuffmanTree {
	public final char value;

	public HuffmanLeaf(double freq, char val) {
		super(freq);
		value = val;
	}
}

class HuffmanNode extends HuffmanTree {
	public final HuffmanTree left, right;

	public HuffmanNode(HuffmanTree l, HuffmanTree r) {
		super(l.frequency + r.frequency);
		left = l;
		right = r;
	}
}

public class HuffmanCode {
	private static final int ASCII_LENGTH = 7;

	public String originalString;
	public int originalStringLength;
	private HashMap<Character, String> compressedResult;
	private HashMap<Character, Double> characterFrequency;
	private double entropy;
	private PriorityQueue<HuffmanTree> huffmanTrees;
	HuffmanTree mainTree;
	private double averageLengthBefore;
	private double averageLengthAfter;
	private boolean probabilityIsGiven;

	public HuffmanCode(String str) {
		super();
		originalString = str;
		originalStringLength = str.length();
		characterFrequency = new HashMap<Character, Double>();
		compressedResult = new HashMap<Character, String>();
		entropy = 0.0;
		averageLengthBefore = 0.0;
		averageLengthAfter = 0.0;
		huffmanTrees = new PriorityQueue<HuffmanTree>();
		probabilityIsGiven = false;

		this.calculateFrequency();
		this.buildTree();
		this.buildString(mainTree, new StringBuffer(), compressedResult);
		this.calculateEntropy();
		this.calculateAverageLengthBeforeCompression();
		this.calculateAverageLengthAfterCompression();
	}

	public HuffmanCode(String str, HashMap<Character, Double> probablity) {
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
		huffmanTrees = new PriorityQueue<HuffmanTree>();
		probabilityIsGiven = true;

		this.buildTree();
		this.buildString(mainTree, new StringBuffer(), compressedResult);
		this.calculateEntropy();
		this.calculateAverageLengthBeforeCompression();
		this.calculateAverageLengthAfterCompression();
	}

	private void buildTree() {
		for (Character c : characterFrequency.keySet()) {
			huffmanTrees.offer(new HuffmanLeaf(characterFrequency.get(c), c));
		}

		assert huffmanTrees.size() >= 1; // Invariant, make sure there is at
											// least one tree exist

		while (huffmanTrees.size() >= 2) {
			HuffmanTree a = huffmanTrees.poll();
			HuffmanTree b = huffmanTrees.poll();

			huffmanTrees.offer(new HuffmanNode(a, b));
		}
		mainTree = huffmanTrees.poll();
	}

	private void buildString(HuffmanTree tree, StringBuffer prefix, HashMap<Character, String> result) {
		assert tree != null; // Invariant, make sure tree is not empty
		if (tree instanceof HuffmanLeaf) {
			HuffmanLeaf leaf = (HuffmanLeaf) tree;

			result.put(leaf.value, prefix.toString());

		} else if (tree instanceof HuffmanNode) {
			HuffmanNode node = (HuffmanNode) tree;

			prefix.append('0');
			buildString(node.left, prefix, result);
			prefix.deleteCharAt(prefix.length() - 1);

			prefix.append('1');
			buildString(node.right, prefix, result);
			prefix.deleteCharAt(prefix.length() - 1);
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
		str += "Symbol\t\tFrequency\tProbability\tHuffman Code\tASCII Code\n";
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