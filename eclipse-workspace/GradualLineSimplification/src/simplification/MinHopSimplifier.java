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

		createBinTree(levels, 1, length - 2);

		int i = 0;
		for (List<Integer> level : levels) {
			for (Integer n : level) {
				simplification[i] = n;
				i++;
			}
		}

		return new Tuple<>(simplification, null);
	}

	private int createBinTree(List<Integer>[] levels, int l, int r) {

		if (l > r) {
			return 0;
		}

		if (l == r) {
//			System.out.println(i + ": " + l + " " + r + " " + l);
//			System.out.println(i + " val: " + l);
			levels[0].add(0, l);
			return 1;
		}

		int mid = (l + r) / 2;

//		System.out.println(i + ": " + l + " " + r + " " + mid);
//
//		System.out.println(i + " val: " + mid);
//
//		System.out.println(i + "\t" + l + " " + (mid-1));
		int depthLeft = createBinTree(levels, l, mid - 1);

//		System.out.println(i + "\t" + (mid + 1) + " " + r);
		int depthRight = createBinTree(levels, mid + 1, r);

		int max = Math.max(depthLeft, depthRight);

		levels[max].add(0, mid);

		return max + 1;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MinHop";
	}

}