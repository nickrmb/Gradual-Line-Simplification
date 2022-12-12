package util;

import distance.DistanceMeasurement;
import line.PolyLine;

public class HeapNode implements Comparable<HeapNode> {
	public HeapNode left;
	public HeapNode right;
	public int index;
	protected int heapIndex;
	public double error = 0;

	public HeapNode(int index) {
		this.index = index;
		heapIndex = index;
	}

	public void calculateError(PolyLine l, DistanceMeasurement distance) {
		if (left == null || right == null) {
			error = Double.MAX_VALUE;
			return;
		}

		if (distance != null)
			error = distance.distance(l, left.index, right.index);
	}

	public void updateAtRemove(PolyLine l, DistanceMeasurement distance) {
		left.right = right;
		right.left = left;

		left.calculateError(l, distance);
		right.calculateError(l, distance);
	}

	@Override
	public int compareTo(HeapNode o) {
		if (o == null)
			return 1;
		return (error < o.error) ? -1 : ((error == o.error) ? 0 : 1);
	}

}
