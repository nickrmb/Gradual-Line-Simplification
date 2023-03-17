package util;

import distance.DistanceMeasure;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import function.SumActiveSum;
import function.Max;
import function.MaxActiveSum;
import function.MaxTotalSum;
import function.OptimizationFunction;
import function.Sum;
import function.WeightedSum;
import line.PolyLine;
import simplifier.EqualSimplifier;
import simplifier.MinSumSimplifier;
import simplifier.GreedyBUSimplifier;
import simplifier.GreedyDifferenceSimplifier;
import simplifier.GreedyTDSimplifier;
import simplifier.InOrderSimplifier;
import simplifier.LineSimplifier;
import simplifier.MinMaxActiveSumHeuristicSimplifier;
import simplifier.MinMaxActiveSumSimplifier;
import simplifier.MinMaxSimplifier;
import simplifier.MinMaxTotalSumHeuristicSimplifier;
import simplifier.MinMaxTotalSumSimplifier;
import simplifier.RandomSimplifier;

/**
 * @author nick
 *
 */
public class Util {

	private static final DistanceMeasure[] distances = { new Hausdorff(), new Frechet(), new FrechetApprox() };
	private static final LineSimplifier[] simplifiers = { new MinSumSimplifier(), new MinMaxSimplifier(),
			new MinMaxActiveSumSimplifier(), new MinMaxTotalSumSimplifier(), new MinMaxTotalSumHeuristicSimplifier(),
			new MinMaxActiveSumHeuristicSimplifier(), new GreedyBUSimplifier(), new GreedyDifferenceSimplifier(),
			new GreedyTDSimplifier(), new InOrderSimplifier(), new EqualSimplifier(), new RandomSimplifier() };
	public static final OptimizationFunction[] optFunctions = { new Max(), new Sum(), new MaxActiveSum(),
			new MaxTotalSum(), new SumActiveSum(), new WeightedSum(), };

	/**
	 * Returns the error in each simplification step from a removal sequence
	 * 
	 * @param simplification      The removal sequence
	 * @param l                   The polyline
	 * @param distanceMeasurement The distance measure
	 * @return The summed error in each simplification step
	 */
	public static double[] errorFromSimplification(int[] simplification, PolyLine l,
			DistanceMeasure distanceMeasurement) {

		int length = l.length();
		int numPointsBetween = length - 2;

		double[] error = new double[numPointsBetween];

		// initialize nodes
		Node[] nodes = createNodeArray(length);

		// calculate error
		for (int i = 0; i < numPointsBetween; i++) {
			int cur = simplification[i];
			error[i] = nodes[cur].calculateError(l, distanceMeasurement);
			nodes[cur].updateAtRemove();
		}

		return error;
	}

	/**
	 * Gets a distance instance of a distance measure from a string
	 * 
	 * @param s
	 * @return
	 */
	public static DistanceMeasure fromStringToDistance(String s) {

		for (DistanceMeasure distance : distances)
			if (s.equalsIgnoreCase(distance.toString()))
				return distance;
		return null;

	}

	/**
	 * Gets a simplifier instance from a string
	 * 
	 * @param s
	 * @return
	 */
	public static LineSimplifier fromStringToSimplifier(String s) {

		for (LineSimplifier simplifier : simplifiers)
			if (s.equalsIgnoreCase(simplifier.toString()))
				return simplifier;
		return null;

	}

	/**
	 * Gets a measure instance from a string
	 * 
	 * @param s
	 * @return
	 */
	public static OptimizationFunction fromStringToMeasure(String s) {

		for (OptimizationFunction measure : optFunctions)
			if (s.equalsIgnoreCase(measure.toString()))
				return measure;
		return null;

	}

	/**
	 * Gets a string containing a list of names of available distance measures
	 * 
	 * @return
	 */
	public static String getAvailableDistances() {
		String list = "";
		for (int i = 0; i < distances.length; i++) {
			list += distances[i].toString();
			if (i != distances.length - 1)
				list += ", ";
		}
		return list;
	}

	/**
	 * Gets a string containing a list of names of available simplifiers
	 * 
	 * @return
	 */
	public static String getAvailableSimplifiers() {
		String list = "";
		for (int i = 0; i < simplifiers.length; i++) {
			list += simplifiers[i].toString();
			if (i != simplifiers.length - 1)
				list += ", ";
		}
		return list;
	}

	/**
	 * Gets a string containing a list of names of available measures
	 * 
	 * @return
	 */
	public static String getAvailableMeasures() {
		String list = "";
		for (int i = 0; i < optFunctions.length; i++) {
			list += optFunctions[i].toString();
			if (i != optFunctions.length - 1)
				list += ", ";
		}
		return list;
	}

	/**
	 * Computes the error of a simplifier applied on a line
	 * 
	 * @param line       The polyline
	 * @param simplifier The simplifier
	 * @param distance   The distance measure used
	 * @return a tuple containing the error (left) and time (right)
	 */
	public static Tuple<Double, Double> computeWithTime(PolyLine line, LineSimplifier simplifier,
			DistanceMeasure distance) {
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

	public static Node[] createNodeArray(int length) {
		Node[] nodes = new Node[length];
		nodes[0] = new Node(0);
		for (int i = 1; i < length; i++) {
			nodes[i] = new Node(i);
			nodes[i].left = nodes[i - 1];
		}
		for (int i = 0; i < length - 1; i++) {
			nodes[i].right = nodes[i + 1];
		}
		return nodes;
	}

}
