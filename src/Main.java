public class Main {

	public static void main(String[] args) {

		String givenString = "what are the indications for getting a digoxin level?";

		ShannonFano sfc = new ShannonFano(givenString);
		System.out.println(sfc);

		HuffmanCode hfc = new HuffmanCode(givenString);
		System.out.println(hfc);
	}

}
