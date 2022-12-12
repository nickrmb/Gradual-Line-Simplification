package util;

import distance.DistanceMeasurement;
import line.PolyLine;

public class Node{
	public Node left;
	public Node right;
	public int index;

	public Node(int index) {
		this.index = index;
	}

	public double calculateError(PolyLine l, DistanceMeasurement distance) {
		if (left == null || right == null) {
			return Double.MAX_VALUE;
		}

		return distance.distance(l, left.index, right.index);
	}
	
	public void updateAtRemove() {
		left.right = right;
		right.left = left;
	}

}
