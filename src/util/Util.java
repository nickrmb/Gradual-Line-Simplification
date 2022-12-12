package util;

import distance.DistanceMeasurement;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;
import simplification.ExactSimplification;
import simplification.GreedySimplification;
import simplification.InOrderSimplification;
import simplification.LineSimplifier;
import simplification.MinHopSimplifier;
import simplification.RandomSimplification;

public class Util {

	private static DistanceMeasurement[] distances = { new Hausdorff(), new Frechet(), new FrechetApprox() };
	private static LineSimplifier[] simplifiers = { new ExactSimplification(), new GreedySimplification(),
			new InOrderSimplification(), new MinHopSimplifier(), new RandomSimplification() };

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

	public static DistanceMeasurement fromStringToDistance(String s) {

		for (DistanceMeasurement distance : distances)
			if (s.equalsIgnoreCase(distance.toString()))
				return distance;
		return null;

	}

	public static LineSimplifier fromStringToSimplifier(String s) {

		for (LineSimplifier simplifier : simplifiers)
			if (s.equalsIgnoreCase(simplifier.toString()))
				return simplifier;
		return null;

	}

	public static String getAvailableDistances() {
		String list = "";
		for (int i = 0; i < distances.length; i++) {
			list += distances[i].toString();
			if (i != distances.length - 1)
				list += ", ";
		}
		return list;
	}

	public static String getAvailableSimplifiers() {
		String list = "";
		for (int i = 0; i < simplifiers.length; i++) {
			list += simplifiers[i].toString();
			if (i != simplifiers.length - 1)
				list += ", ";
		}
		return list;
	}

	public static Tuple<Double, Double> computeWithTime(PolyLine line, LineSimplifier simplifier,
			DistanceMeasurement distance) {
		long t1 = System.nanoTime();
		Tuple<int[], double[]> solution = simplifier.simplify(line, distance);
		long t2 = System.nanoTime();

		long dif = t2 - t1;

		double timeBetweenMS = Math.round(dif / 10000.0) / 100.0;

		if (solution.r == null) {
			solution.r = Util.errorFromSimplification(solution.l, line, distance);
		}

		return new Tuple<>(solution.r[solution.r.length - 1], timeBetweenMS);

	}

}
