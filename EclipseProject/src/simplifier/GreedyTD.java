package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.Node;
import util.Tuple;

public class GreedyTD implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		int numPointsBetween = l.length() - 2;

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		if (error.length == 0)
			new Tuple<>(simplification, error);


		// setup node list
		
		Node head = new Node(0);
		Node cur = head;

		double err = distance.measure(l, 0, l.length() - 1);
		for (int i = 1; i <= l.length() - 2; i++) {
			cur.right = new Node(i);
			cur.right.left = cur;
			cur = cur.right;
			cur.error = err;
			cur.leftID = 0;
			cur.rightID = l.length() - 1;
			cur.errorLeft = distance.measure(l, cur.leftID, cur.index);
			cur.errorRight = distance.measure(l, cur.index, cur.rightID);
		}
		
		// find sequence

		for (int i = error.length - 1; i >= 0; i--) {
			// find minimal summed implied shortcuts of available vertices
			double minError = Double.MAX_VALUE;
			Node minNode = null;
			
			cur = head;
			
			while((cur = cur.right) != null) {
				err = Math.max(cur.errorLeft, cur.errorRight);
				if(err < minError) {
					minError = err;
					minNode = cur;
				}
			}
			
			// update all right nodes
			cur = minNode;
			while((cur = cur.left) != head && cur.index > minNode.leftID) {
				cur.rightID = minNode.index;
				cur.errorRight = distance.measure(l, cur.index, cur.rightID);
				cur.error = minNode.errorLeft;
			}
			
			// update all left nodes
			cur = minNode;
			while((cur = cur.right) != null && cur.index < minNode.rightID) {
				cur.leftID = minNode.index;
				cur.errorLeft = distance.measure(l, cur.leftID, cur.index);
				cur.error = minNode.errorRight;
			}
			
			// add to sequence
			error[i] = minNode.error;
			simplification[i] = minNode.index;
			
			// remove from node list
			minNode.left.right = minNode.right;
			if(minNode.right != null) minNode.right.left = minNode.left;
			
		}

		return new Tuple<>(simplification, error);

	}

	@Override
	public String toString() {
		return "GreedyTD";
	}

}