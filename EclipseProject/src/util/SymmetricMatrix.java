package util;

import java.util.Arrays;

public class SymmetricMatrix {

	private double[] array;
	private int n;

	public SymmetricMatrix(int n, double fill) {
		this.n = n;
		array = new double[gauss(n)];
		if (fill != 0) {
			Arrays.fill(array, fill);
		}
	}

	/**
	 * Gets value at some 2d index
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public double getValue(int i, int j) {
		return array[indexOf(i, j)];
	}

	/**
	 * Sets the value at some 2d index
	 * 
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setValue(int i, int j, double value) {
		array[indexOf(i, j)] = value;
	}

	/**
	 * Projects the 2d index to a 1d index
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private int indexOf(int i, int j) {
		if (i > j) {
			int a = i;
			i = j;
			j = a;
		}

		int index = array.length - gauss(n - i) + j - i;

		return index;
	}

	/**
	 * Gauss sum formula
	 * 
	 * @param n
	 * @return
	 */
	private int gauss(int n) {
		return n * (n + 1) / 2;
	}

	@Override
	public String toString() {
		int a = 4;
		String str = "    ";

		for (int i = 0; i < n; i++) {
			str += " | ";
			a += 4;
			str += uniform(String.valueOf(i), 3) + "                 ";
			a += 20;
		}
		str += "\n";
		for (int i = 0; i < a; i++) {
			str += "-";
		}
		str += "\n";
		for (int i = 0; i < n; i++) {
			str += uniform(i + "", 3) + " | ";
			for (int j = 0; j < n; j++) {
				str += uniform(String.valueOf(getValue(j, i)), 20);
				if (j != n - 1) {
					str += " | ";
				}
			}
			str += "\n";
		}
		return str;
	}

	private String uniform(String str, int len) {
		for (int i = str.length(); i <= len; i++) {
			str += " ";
		}
		return str;
	}

}
