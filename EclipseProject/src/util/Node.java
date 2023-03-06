package util;

import distance.DistanceMeasure;
import line.PolyLine;

public class Node {
	public Node left;
	public Node right;
	public int index;

	public Node(int index) {
		this.index = index;
	}

	/**
	 * Calculates the shortcut error of the node in a polyline
	 * 
	 * @param l        The PolyLine
	 * @param distance The distance measure
	 * @return The error
	 */
	public double calculateError(PolyLine l, DistanceMeasure distance) {
		if (left == null || right == null) {
			return Double.MAX_VALUE;
		}

		return distance.distance(l, left.index, right.index);
	}

	/**
	 * Update neighboring nodes at removal
	 */
	public void updateAtRemove() {
		left.right = right;
		right.left = left;
	}

}
