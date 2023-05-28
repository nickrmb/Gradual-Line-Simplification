package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.DataFormatException;

import distance.DistanceMeasure;
import function.ObjectiveFunction;
import line.PolyLine;
import simplifier.LineSimplifier;
import util.Tuple;
import util.Util;

public class TestSimplifiers {

	static Tuple<int[], double[]> result = null;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws DataFormatException, IOException, InterruptedException {
		System.out.println();
		if (args.length != 4) {
			throw new IllegalArgumentException(
					"Arguments needed: <dirPath> <simplifiers> <objectiveFunctions> <distanceType>");
		}

		String dirPath = args[0];
		String simplifiersString = args[1];
		String objectiveFunctions = args[2];
		String distanceType = args[3];

		// Confirm Folder
		File directory = new File(dirPath);
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
					directory.getAbsolutePath() + "\nDirectory path is not a directory (dirPath)");
		}

		// Confirm Simplifiers
		String[] simplSplit = simplifiersString.split(",");
		List<LineSimplifier> simplifiersList = new ArrayList<>();
		for (String s : simplSplit) {
			LineSimplifier simplifier = Util.fromStringToSimplifier(s);
			if (simplifier == null) {
				throw new IllegalArgumentException(
						"The simplifier \"" + s + "\" does not exist!\nAvailable: " + Util.getAvailableSimplifiers());
			}
			simplifiersList.add(simplifier);
		}
		LineSimplifier[] simplifiers = simplifiersList.toArray(new LineSimplifier[0]);

		// Confirm Objective Functions
		String[] objSplit = objectiveFunctions.split(",");
		List<ObjectiveFunction> objectivesList = new ArrayList<>();
		for (String s : objSplit) {
			ObjectiveFunction objective = Util.fromStringToMeasure(s);
			if (objective == null) {
				throw new IllegalArgumentException("The objective function \"" + s + "\" does not exist!\nAvailable: "
						+ Util.getAvailableObjectives());
			}
			objectivesList.add(objective);
		}
		ObjectiveFunction[] objectives = objectivesList.toArray(new ObjectiveFunction[0]);

		// Confirm distance type
		DistanceMeasure distance = Util.fromStringToDistance(distanceType);
		if (distance == null) {
			throw new IllegalArgumentException("The geometric distance metric \"" + distanceType
					+ "\" does not exist!\nAvailable: " + Util.getAvailableDistances());
		}

		// retrieve graph files from directory
		File[] graphs = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String[] split = file.getName().split("\\.");
				if (split.length <= 1) {
					return false;
				}
				String extension = split[split.length - 1];
				return extension.equals("sgpx");
			}
		});

		if (graphs.length == 0) {
			throw new IllegalArgumentException(directory.getAbsolutePath() + "\nDirectory contains no .sgpx files!");
		}

		// convert to line obejects
		PolyLine[] lines = new PolyLine[graphs.length];
		for (int i = 0; i < lines.length; i++) {
			try {
				lines[i] = PolyLine.readLine(graphs[i]);
			} catch (NumberFormatException | IOException | DataFormatException e) {
				throw new DataFormatException("Error at reading file " + graphs[i].getAbsolutePath());
			}
		}

		Arrays.sort(lines, new Comparator<PolyLine>() {

			@Override
			public int compare(PolyLine o1, PolyLine o2) {
				return Integer.compare(o1.length(), o2.length());
			}
		});

		File output = new File("output.csv");
		int c = 1;
		while (output.exists()) {
			output = new File("output-" + (c++) + ".csv");
		}
		output.createNewFile();

		BufferedWriter out = new BufferedWriter(new FileWriter(output));
		out.write("file,length");
		for (LineSimplifier simp : simplifiers) {
			out.write("," + simp.toString() + " Time");
			for (ObjectiveFunction obj : objectives) {
				out.write("," + simp.toString() + " " + obj.toString());
			}
		}
		out.write("\n");

		boolean[] hasFailed = new boolean[simplifiers.length];

		int counter = 1;
		for (PolyLine line : lines) {
			out.write(line.getName() + "," + line.length());

			for (int i = 0; i < simplifiers.length; i++) {
				LineSimplifier simplifier = simplifiers[i];
				if (!hasFailed[i]) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							result = simplifier.simplify(line, distance);
						}
					});

					long t1 = System.nanoTime();
					t.start();
					t.join(20 * 60 * 1000);
					long t2 = System.nanoTime();
					if (t.isAlive()) {
						t.stop();
						hasFailed[i] = true;
					} else {
						if (result.r == null) {
							result.r = Util.errorFromSimplification(result.l, line, distance);
						}

						long time = t2 - t1;
						out.write("," + (((double) time) / 1000.0));

						for (ObjectiveFunction objective : objectives) {
							double[] error = objective.measure(result.l, result.r);
							out.write("," + error[error.length - 1]);
						}

						continue;
					}
				}

				out.write(",-");
				for (int j = 0; j < objectives.length; j++) {
					out.write(",-");
				}

			}

			System.out.println(counter++ + "/" + lines.length + "\t");
			out.write("\n");
			out.flush();

		}

		out.close();

	}

}
