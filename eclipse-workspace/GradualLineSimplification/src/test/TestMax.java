package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.DataFormatException;

import distance.DistanceMeasure;
import distance.Frechet;
import distance.FrechetApprox;
import distance.Hausdorff;
import line.PolyLine;
import simplification.GreedySimplification;
import simplification.LineSimplifier;
import util.Tuple;
import util.Util;

public class TestMax {

	private static Tuple<Double, Double> solution;

	@SuppressWarnings({ "deprecation" })
	public static void main(String[] args)
			throws IOException, NumberFormatException, DataFormatException, InterruptedException {
		LineSimplifier simplifier = new GreedySimplification();

		DistanceMeasure[] distances = { new Hausdorff(), new FrechetApprox(10)};

		((FrechetApprox) distances[1]).setIterations(10);

		boolean[] failed = new boolean[distances.length];

		int nonfailedNum = distances.length;

		int currentLength = 1000000;

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/home/beckenlechner/max.csv")));
		writer.write("length,");
		for (int i = 0; i < distances.length; i++) {
			writer.write(distances[i].toString());
			if (i != distances.length - 1) {
				writer.write(",");
			}
		}
		writer.write("\n");

		while (nonfailedNum > 0) {

			File graph = createFakeGraph(currentLength);

			PolyLine line = PolyLine.readLine(graph);

			System.out.print(currentLength + ": ");
			
			writer.write(currentLength + ",");

			for (int i = 0; i < distances.length; i++) {
				System.gc();
				distances[0] = new Hausdorff();
				distances[1] = new FrechetApprox(10);
				((FrechetApprox) distances[1]).setIterations(10);
				
				solution = null;

				DistanceMeasure distance = distances[i];
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						solution = Util.computeWithTime(line, new GreedySimplification(), distance);
					}
				});

				if (!failed[i]) {

					t.start();

					t.join(900000);
				}

				if (solution == null) {
					if (t.isAlive())
						t.stop();
					System.out.print("O");
					
					if(!failed[i]) {
						failed[i] = true;
						nonfailedNum -= 1;
					}

					writer.write("-1");
				} else {
					System.out.print("|");
					double time = solution.r;

					writer.write(time + "");
				}

				if (i != distances.length - 1) {
					writer.write(",");
				}
			}

			System.out.println();
			writer.write("\n");
			
			writer.flush();

			graph.delete();

			currentLength += 1000000;

		}
		
		writer.close();

	}

	public static File createFakeGraph(int length) throws IOException {
		String graph = "/home/beckenlechner/data2/91495.sgpx";

		FileInputStream fin = new FileInputStream(new File(graph));
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

		File f = new File("/home/beckenlechner/" + length + ".sgpx");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));

		writer.write(length + "\n");

		int linesWritten = 0;
		reader.readLine();

		while (linesWritten < length) {
			String line = reader.readLine();
			if (line == null || line.equals("")) {
				fin.getChannel().position(0);
				reader.readLine();
				line = reader.readLine();
			}
			writer.write(line + "\n");
			
			linesWritten++;
		}

		reader.close();
		writer.close();

		return f;

	}

}
