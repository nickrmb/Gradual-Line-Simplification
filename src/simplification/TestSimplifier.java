package simplification;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasurement;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;
import util.Tuple;

public class TestSimplifier {

	private static final String directory = "/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/data/test/";

	private static final String[] graphs = { "l1", "l2", "l3", "424", "3151", "6000" };

	private static final LineSimplifier[] simplifiers = { new ExactSimplification(), new GreedySimplification(),
			new RandomSimplification(), new InOrderSimplification() };

	private static final DistanceMeasurement[] distanceMeasurements = { new Frechet(), new FrechetApprox(),
			new Hausdorff() };

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		for (String graph : graphs) {

			System.out.println("-----------------------------------------------------");

			System.out.println("Graph: " + graph + "\n");

			PolyLine l = PolyLine.readLine(new File(directory + graph + ".sgpx"));

			for (DistanceMeasurement distance : distanceMeasurements) {

				System.out.println();
				System.out.println("\t" + distance.toString() + "-Distance:");
				System.out.println();

				for (LineSimplifier simplifier : simplifiers) {

					long t1 = System.nanoTime();
					Tuple<int[], double[]> solution = simplifier.simplify(l, distance);
					long t2 = System.nanoTime();
					long dif = t2 - t1;

					double timeBetween = Math.round(dif / 10000.0) / 100.0;

					System.out.println(
							"\t\t" + simplifier.toString() + "-Simplification: " + solution.r[solution.r.length - 1]);
					System.out.println("\t\t\tin " + timeBetween + " ms");

				}
			}
			System.out.println();
		}
	}

}
