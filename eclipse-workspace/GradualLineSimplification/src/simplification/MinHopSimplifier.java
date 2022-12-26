package simplification;

import java.util.ArrayList;
import java.util.List;

import distance.DistanceMeasurement;
import line.PolyLine;
import util.Tuple;

public class MinHopSimplifier implements LineSimplifier {

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasurement distanceMeasurement) {
		int length = l.length();

		int[] simplification = new int[length - 2];
		@SuppressWarnings("unchecked")
		List<Integer>[] levels = new List[1 + (int) Math.ceil(Math.log(length - 2) / Math.log(2))];

		for (int i = 0; i < levels.length; i++) {
			levels[i] = new ArrayList<>();
		}

		createLevelsOfTree(levels, 1, length - 2);

		int i = 0;
		for (List<Integer> level : levels) {
			for (Integer n : level) {
				simplification[i] = n;
				i++;
			}
		}

		return new Tuple<>(simplification, null);
	}

	private int createLevelsOfTree(List<Integer>[] levels, int l, int r) {

		if (l > r) {
			return 0;
		}

		if (l == r) {
			levels[0].add(0, l);
			return 1;
		}

		int mid = (l + r) / 2;
		
		int depthLeft = createLevelsOfTree(levels, l, mid - 1);
		int depthRight = createLevelsOfTree(levels, mid + 1, r);

		int max = Math.max(depthLeft, depthRight);

		levels[max].add(0, mid);

		return max + 1;
	}
	
	@Override
	public String toString() {
		return "MinHop";
	}

}
