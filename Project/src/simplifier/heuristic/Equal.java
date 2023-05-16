package simplifier.heuristic;

import java.util.LinkedList;
import java.util.Queue;

import distance.DistanceMeasure;
import line.PolyLine;
import simplifier.LineSimplifier;
import util.Tuple;

public class Equal implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine line, DistanceMeasure distance) {
		int length = line.length();

		int[] simplification = new int[length - 2];

		// create queue
		Queue<Tuple<Integer, Integer>> queue = new LinkedList<>();
		// add middle element
		queue.add(new Tuple<>(1, length - 2));

		for (int i = 0; i < simplification.length; i++) {
			Tuple<Integer, Integer> cur = queue.poll();
			int l = cur.l;
			int r = cur.r;

			// get middle element
			int mid = (l + r) / 2;
			
			simplification[simplification.length - 1 - i] = mid;

			int ll = l;
			int lr = mid - 1;

			int rl = mid + 1;
			int rr = r;

			// check whether there are elements between
			if (rl <= rr) {
				queue.add(new Tuple<>(rl, rr));
			}
			if (ll <= lr) {
				queue.add(new Tuple<>(ll, lr));
			}

		}

		return new Tuple<>(simplification, null);
	}
	
	
	@Override
	public String toString() {
		return "Equal";
	}

}
