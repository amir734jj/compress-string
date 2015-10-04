import java.util.HashMap;
import java.util.PriorityQueue;

abstract class HuffmanTree implements Comparable<HuffmanTree> {
	public final int frequency;

	public HuffmanTree(int freq) {
		frequency = freq;
	}

	public int compareTo(HuffmanTree tree) {
		return frequency - tree.frequency;
	}
}

class HuffmanLeaf extends HuffmanTree {
	public final char value;

	public HuffmanLeaf(int freq, char val) {
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

	private String originalString;
	private int originalStringLength;
	private HashMap<Character, String> compressedResult;
	private HashMap<Character, Integer> characterFrequency;
	private double entropy;
	private PriorityQueue<HuffmanTree> huffmanTrees;
	HuffmanTree mainTree;
	private double averageLengthBefore;
	private double averageLengthAfter;

	public HuffmanCode(String str) {
		super();
		originalString = str;
		originalStringLength = str.length();
		characterFrequency = new HashMap<Character, Integer>();
		compressedResult = new HashMap<Character, String>();
		entropy = 0.0;
		averageLengthBefore = 0.0;
		averageLengthAfter = 0.0;
		huffmanTrees = new PriorityQueue<HuffmanTree>();

		this.calculateFrequency();
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

		assert huffmanTrees.size() >= 1; // Invariant

		while (huffmanTrees.size() >= 2) {
			HuffmanTree a = huffmanTrees.poll();
			HuffmanTree b = huffmanTrees.poll();

			huffmanTrees.offer(new HuffmanNode(a, b));
		}
		mainTree = huffmanTrees.poll();
	}

	private void buildString(HuffmanTree tree, StringBuffer prefix, HashMap<Character, String> result) {
		assert tree != null; // Invariant
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
		str += "Symbol\tWeight\tHuffman Code\t\tASCII Code\n";
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