package test;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasurement;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;

public class TestDistances {

	private static final String directory = "/Users/nick/Documents/University/Bachelor Project/gradual-line-simplification/data/test/";

	private static final String[] graphs = { "l1", "l2", "l3", "424", "3151", "6000" };

	private static final DistanceMeasurement[] distanceMeasurements = { new Frechet(), new FrechetApprox(),
			new Hausdorff() };

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {

		for (String graph : graphs) {

			PolyLine l = PolyLine.readLine(new File(directory + graph + ".sgpx"));

			System.out.println("-----------------------------------------------------");

			System.out.println("Graph: " + graph + "\n");

			for (DistanceMeasurement distance : distanceMeasurements) {

				long t1 = System.nanoTime();
				double error = distance.distance(l, 0, l.length() - 1);
				long t2 = System.nanoTime();
				long dif = t2 - t1;

				double timeBetween = Math.round(dif / 10000.0) / 100.0;

				System.out.println("\t" + distance.toString() + "-Distance: " + error);
				System.out.println("\t\tin: " + timeBetween + " ms");

			}

		}
	}

}
