package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.zip.DataFormatException;

import distance.DistanceMeasure;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;
import simplification.GreedySimplification;
import simplification.InOrderSimplification;
import simplification.LineSimplifier;
import simplification.EqualSimplifier;
import simplification.RandomSimplification;
import util.Tuple;
import util.Util;

public class SimpComp1 {

	private static DistanceMeasure[] distances = { new Hausdorff(), new FrechetApprox(10), new Frechet() };
	private static LineSimplifier[] simplifiers = { new InOrderSimplification(), new RandomSimplification(),
			new EqualSimplifier(), new GreedySimplification() };

	public static void main(String[] args) throws IOException, NumberFormatException, DataFormatException {

		int[] lengths = { 7403, 7509, 7587, 7666, 7777, 7900, 7980, 8100, 8200, 8320, 8400, 8460, 8510, 8600, 8750,
				8900, 9010, 9100, 9250, 9350, 9440, 9500, 9630, };

		String graph = "/home/beckenlechner/data2/91495.sgpx";

		for (int l : lengths) {
			BufferedReader reader = new BufferedReader(new FileReader(new File(graph)));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/home/beckenlechner/" + l + ".sgpx")));

			writer.write(l + "\n");
			int begin = new Random().nextInt(91494 - l);

			for (int i = 0; i < begin; i++) {
				reader.readLine();
			}

			for (int i = 0; i < l; i++) {
				writer.write(reader.readLine() + "\n");
			}

			reader.close();
			writer.close();

		}

		((FrechetApprox) distances[1]).setIterations(10);

		for (DistanceMeasure distance : distances) {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(new File("/home/beckenlechner/" + distance.toString() + ".out")));
			System.out.println("- - - - - " + distance + " - - - - -");
			for (int l : lengths) {
				PolyLine line = PolyLine.readLine(new File("/home/beckenlechner/" + l + ".sgpx"));

				writer.write(l + ".sgpx," + l + ",");

				System.out.print("\t" + l + ": ");

				for (int i = 0; i < simplifiers.length; i++) {
					LineSimplifier simplifier = simplifiers[i];

					Tuple<Double, Double> solution = Util.computeWithTime(line, simplifier, distance);

					double error = solution.l;
					double time = solution.r;

					writer.write(error + "," + time + ",");

					System.out.print("|");

				}
				writer.write("-1,-1\n");
				writer.flush();

				System.out.println();
			}

			System.out.println();

			writer.close();
		}

		for (int l : lengths) {
			new File("/home/beckenlechner/" + l + ".sgpx").delete();
		}

	}

}
