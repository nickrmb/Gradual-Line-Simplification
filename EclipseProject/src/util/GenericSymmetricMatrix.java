package util;

import java.util.Arrays;

public class GenericSymmetricMatrix {

	private Object[] array;
	private int n;

	public GenericSymmetricMatrix(int n, Object fill) {
		this.n = n;
		array = new Object[gauss(n)];
		if (fill != null) {
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
	public Object getValue(int i, int j) {
		return array[indexOf(i, j)];
	}

	/**
	 * Sets the value at some 2d index
	 * 
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setValue(int i, int j, Object value) {
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

}