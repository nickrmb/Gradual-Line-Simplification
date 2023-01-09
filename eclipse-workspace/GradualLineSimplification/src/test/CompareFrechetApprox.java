package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.DataFormatException;
import distance.FrechetApprox;
import line.PolyLine;
import simplification.ExactSimplification;
import simplification.LineSimplifier;
import util.Tuple;
import util.Util;

public class CompareFrechetApprox {

	private static LineSimplifier[] simplifiers = { new ExactSimplification() };

	private static Tuple<Double, Double> solution = null;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			throw new IllegalArgumentException(
					"Arguments must be given by: <lineDirectory> <commaSeparatedK> <numberLines>");
		}

		String directoryPath = args[0];
		String[] kString = args[1].split(",");

		int numberLines = Integer.MAX_VALUE;
		if (args.length >= 3)
			numberLines = Integer.valueOf(args[2]);

		File directory = new File(directoryPath);

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("File is not a directory");
		}

		String path = directoryPath + File.separator + "_output.csv";
		File outFile;

		while ((outFile = new File(path)).exists()) {
			path += "0";
		}
		outFile.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

		String header = "lineFile,length,";
		for (int i = 0; i < simplifiers.length; i++) {
			for (int j = 0; j < kString.length; j++) {
				LineSimplifier simplifier = simplifiers[i];
				header += simplifier.toString() + "-distance at k=" + kString[j] + "," + simplifier.toString()
						+ "-time at k=" + kString[j];

				if (i != simplifiers.length - 1 || j != kString.length - 1) {
					header += ",";
				}
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
		boolean[] failed = new boolean[simplifiers.length * kString.length];
		for (int i = 0; i < Math.min(files.length, numberLines); i++) {
			File lineFile = files[i];

			String lineName = lineFile.getName();

			if (lineName.equals("_output.csv")) {
				continue;
			}
			cur++;

			try {
				PolyLine line = PolyLine.readLine(lineFile);

				System.out.print(cur + "/" + (Math.min(files.length, numberLines) - 1) + " ; " + lineName + ": \t");

				String output = lineName + "," + line.length() + ",";

				for (int j = 0; j < simplifiers.length; j++) {
					for (int curk = 0; curk < kString.length; curk++) {
						LineSimplifier simplifier = simplifiers[j];
						solution = null;

						int curK = curk;
						Thread t = new Thread(new Runnable() {
							int k = Integer.valueOf(kString[curK]);

							@Override
							public void run() {
								solution = Util.computeWithTime(line, simplifier, new FrechetApprox(k));
							}
						});

						int failedIndex = j * kString.length + curk;

						if (!failed[failedIndex]) {

							t.start();

							t.join(900000);
						}

						if (solution == null) {
							if (t.isAlive())
								t.stop();
							System.out.print("O");
							failed[failedIndex] = true;

							output += "-1,-1";
						} else {
							System.out.print("|");

							double error = solution.l;
							double time = solution.r;

							output += error + "," + time;
						}

						if (j != simplifiers.length - 1 || curk != kString.length - 1) {
							output += ",";
						}
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
