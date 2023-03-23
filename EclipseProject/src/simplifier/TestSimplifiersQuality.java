package simplifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import distance.DistanceMeasure;
import distance.Hausdorff;
import function.OptimizationFunction;
import function.SumMaxTotal;
import line.PolyLine;
import line.Vertex;
import util.Tuple;
import util.Util;

public class TestSimplifiersQuality {

	private static final LineSimplifier[] simplifiers = Util.simplifiers;

	private static final BruteForce bruteForce = new BruteForce();

	private static final OptimizationFunction[] functions = Util.optFunctions;

	public static void main(String[] args) {

		int numLines = 250;
		int lineLength = 10;
		double scala = 100;
		DistanceMeasure distance = new Hausdorff();

		PolyLine[] lines = new PolyLine[numLines];
		for (int i = 0; i < numLines; i++) {
			lines[i] = createLine(lineLength, scala);
		}

		double[][][] solutionsError = new double[simplifiers.length][lines.length][];
		int[][][] solutionsSeq = new int[simplifiers.length][lines.length][];
		for (int i = 0; i < simplifiers.length; i++) {
			if (simplifiers[i].toString().equalsIgnoreCase("bruteforce")) {
				continue;
			}
//			System.out.println(i);
			LineSimplifier simplifier = simplifiers[i];
			for (int j = 0; j < lines.length; j++) {
				Tuple<int[], double[]> sol = simplifier.simplify(lines[j], distance);
				if (sol.r == null)
					sol.r = Util.errorFromSimplification(sol.l, lines[j], distance);
				solutionsSeq[i][j] = sol.l;
				solutionsError[i][j] = sol.r;
			}
		}

		for (OptimizationFunction function : functions) {

			bruteForce.setFunction(function);
			double[] opt = new double[numLines];

			for (int i = 0; i < opt.length; i++) {
//				System.out.println(i);
				Tuple<int[], double[]> sol = bruteForce.simplify(lines[i], distance);
				double[] sopt = function.measure(sol.l, sol.r);
				opt[i] = sopt[sopt.length - 1];
			}

			System.out.println("For " + function + ":");

			HashMap<String, Double> map = new HashMap<>();

			for (int i = 0; i < simplifiers.length; i++) {
				if (simplifiers[i].toString().equalsIgnoreCase("bruteforce")) {
					continue;
				}
				double overest = 0;
				for (int j = 0; j < lines.length; j++) {
					double[] ssol = function.measure(solutionsSeq[i][j], solutionsError[i][j]);
					double val = ssol[ssol.length - 1];
					overest += val / opt[j];
					if (simplifiers[i].toString().equalsIgnoreCase("MinSumMaxTotal") && val != opt[j]) {
						System.out.println(lines[j]);
					}
				}
				map.put(simplifiers[i].toString(), overest);// numLines);
			}

			List<Entry<String, Double>> list = new ArrayList<>(map.entrySet());
			list.sort(Entry.comparingByValue());

			for (Entry<String, Double> entry : list) {
				System.out.println("\t" + uniform(String.valueOf((entry.getValue() / lines.length - 1.0)* 100) + "%", 20) + "\t" + entry.getKey());
			}

			System.out.println("\n-----------------------------------------"
					+ "-------------------------------------------------\n");

		}
	}

//	private static double round(double d) {
//		return Math.round(d * 100) / 100.0;
//	}

	private static PolyLine createLine(int length, double scala) {
		Random r = new Random();
		Vertex[] vertices = new Vertex[length];

		vertices[0] = new Vertex(0.0, 0.0);

		double magX = 0;
		double magY = 0;

		for (int i = 1; i < vertices.length; i++) {
			magX += (r.nextDouble() - 0.5) * scala / length;
			magY += (r.nextDouble() - 0.5) * scala / length;
			vertices[i] = new Vertex(vertices[i - 1].getX() + magX, vertices[i - 1].getY() + magY);
		}

		PolyLine line = new PolyLine(vertices);

		return line;
	}
	
	private static String uniform(String str, int len) {
		for (int i = str.length(); i <= len; i++) {
			str += " ";
		}
		return str;
	}

}
