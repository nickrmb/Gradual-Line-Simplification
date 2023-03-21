package util;

public class SC {
	public int i, j;

	public SC() {
	}

	public SC(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	@Override
	public String toString() {
		return "(" + i + "," + j + ")";
	}
}