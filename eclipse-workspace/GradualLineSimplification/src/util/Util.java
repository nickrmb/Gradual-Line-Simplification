package util;

import distance.DistanceMeasurement;
import line.PolyLine;

public class Util {

	public static double[] errorFromSimplification(int[] simplification, PolyLine l,
			DistanceMeasurement distanceMeasurement) {

		int length = l.length();
		int numPointsBetween = length - 2;

		double[] error = new double[numPointsBetween];

		// initialize nodes
		Node[] nodes = new Node[length];
		nodes[0] = new Node(0);
		for (int i = 1; i < length; i++) {
			nodes[i] = new Node(i);
			nodes[i].left = nodes[i - 1];
		}
		for (int i = 0; i < length - 1; i++) {
			nodes[i].right = nodes[i + 1];
		}

		// calculate error
		double err = 0.0;
		for (int i = 0; i < numPointsBetween; i++) {
			int cur = simplification[i];
			err += nodes[cur].calculateError(l, distanceMeasurement);
			nodes[cur].updateAtRemove();

			error[i] = err;
		}

		return error;
	}

}
