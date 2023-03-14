package util;

import distance.DistanceMeasure;
import line.PolyLine;

public class Node {
	public Node left;
	public Node right;
	public int index;

	// used for Greedy
	public double error = 0;
	public double errorLeft = 0, errorRight = 0;
	public int leftID, rightID;

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
		return distance.measure(l, left.index, right.index);
	}

	/**
	 * Update neighboring nodes at removal
	 */
	public void updateAtRemove() {
		left.right = right;
		right.left = left;
	}

}
