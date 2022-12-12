package util;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasurement;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;
import simplification.ExactSimplification;
import simplification.GreedySimplification;
import simplification.InOrderSimplification;
import simplification.LineSimplifier;
import simplification.RandomSimplification;
import test.Visualizer;

public class TestVisualizer {

	private static final String directory = "/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/data/test/";

	private static final String[] graphs = { "l1", "l2", "l3", "424", "3151", "6000" };

	private static final LineSimplifier[] simplifiers = { new ExactSimplification(), new GreedySimplification(),
			new RandomSimplification(), new InOrderSimplification() };

	private static final DistanceMeasurement[] distanceMeasurements = { new Frechet(), new FrechetApprox(),
			new Hausdorff() };

	private static final int graph = 4;
	private static final int simplifier = 1;
	private static final int distanceMeasurement = 2;

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {

		PolyLine l = PolyLine.readLine(new File(directory + graphs[graph] + ".sgpx"));

		Tuple<int[], double[]> solution = simplifiers[simplifier].simplify(l,
				distanceMeasurements[distanceMeasurement]);

		new Visualizer(l, solution.l, solution.r);
	}

}
