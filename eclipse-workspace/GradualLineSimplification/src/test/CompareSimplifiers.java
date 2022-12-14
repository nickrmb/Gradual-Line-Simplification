package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.DataFormatException;

import distance.DistanceMeasurement;
import distance.FrechetApprox;
import line.PolyLine;
import simplification.ExactSimplification;
import simplification.GreedySimplification;
import simplification.InOrderSimplification;
import simplification.LineSimplifier;
import simplification.MinHopSimplifier;
import simplification.RandomSimplification;
import util.Tuple;
import util.Util;

public class CompareSimplifiers {

	private static LineSimplifier[] simplifiers = { new InOrderSimplification(), new RandomSimplification(),
			new MinHopSimplifier(), new GreedySimplification(), new ExactSimplification() };

	private static Tuple<Double, Double> solution = null;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			throw new IllegalArgumentException(
					"Arguments must be given by: <lineDirectory> <distanceType> <optimal:distanceArgument>");
		}

		String directoryPath = args[0];
		String errorType = args[1];

		File directory = new File(directoryPath);

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("File is not a directory");
		}

		DistanceMeasurement distance = Util.fromStringToDistance(errorType);
		if (distance == null) {
			throw new IllegalArgumentException("Distance not found, available: " + Util.getAvailableDistances());
		}

		if (args.length > 2) {
			if (distance instanceof FrechetApprox) {
				try {
					int i = Integer.parseInt(args[2]);
					if (i <= 0) {
						throw new IllegalArgumentException("distanceArgument must be greater than 0");
					}
					((FrechetApprox) distance).setIterations(i);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("distanceArgument is not an integer");
				}
			}
		}

		File outFile = new File(directoryPath + File.separator + "_output.csv");
		if (outFile.exists()) {
			outFile.delete();
		}
		outFile.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

		String header = "lineFile,length,";
		for (int i = 0; i < simplifiers.length; i++) {
			LineSimplifier simplifier = simplifiers[i];
			header += simplifier.toString() + "-distance," + simplifier.toString() + "-time";

			if (i != simplifiers.length - 1) {
				header += ",";
			}
		}
		writer.write(header + "\n");

		File[] files = directory.listFiles();
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				String name1 = n1.split("\\.")[0];
				String name2 = n2.split("\\.")[0];

				int i1, i2;

				try {
					i1 = Integer.valueOf(name1);
				} catch (NumberFormatException e) {
					return 1;
				}

				try {
					i2 = Integer.valueOf(name2);
				} catch (NumberFormatException e) {
					return -1;
				}

				return Integer.compare(i1, i2);

			}
		});
		int cur = 0;
		boolean[] failed = new boolean[simplifiers.length];
		for (int i = 0; i < files.length; i++) {
			File lineFile = files[i];

			String lineName = lineFile.getName();

			if (lineName.equals("_output.csv")) {
				continue;
			}
			cur++;

			try {
				PolyLine line = PolyLine.readLine(lineFile);

				System.out.print(cur + "/" + (files.length - 1) + " ; " + lineName + ": \t");

				String output = lineName + "," + line.length() + ",";

				for (int j = 0; j < simplifiers.length; j++) {
					LineSimplifier simplifier = simplifiers[j];
					solution = null;

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							solution = Util.computeWithTime(line, simplifier, distance);
						}
					});
					
					if (!failed[j]) {

						t.start();

						t.join(900000);
					}

					if (solution == null) {
						if (t.isAlive())
							t.stop();
						System.out.print("O");
						failed[j] = true;

						output += "-1,-1";
					} else {
						System.out.print("|");

						double error = solution.l;
						double time = solution.r;

						output += error + "," + time;
					}

					if (j != simplifiers.length - 1) {
						output += ",";
					}

				}

				writer.write(output + "\n");
				writer.flush();
				System.out.print("\n");

			} catch (InterruptedException | NumberFormatException | IOException | DataFormatException e) {
				System.err.println("At " + lineName);
				e.printStackTrace();
			}
		}

		writer.close();

	}

}
