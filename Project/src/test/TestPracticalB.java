package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasure;
import distance.Frechet;
import function.ObjectiveFunction;
import function.Sum;
import line.PolyLine;
import simplifier.LineSimplifier;
import simplifier.greedy.Greedy;
import simplifier.greedy.GreedyPractical;
import util.Tuple;

public class TestPracticalB {
	
	private static final double c = 4;

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		System.out.println();
		if (args.length != 2) {
			throw new IllegalArgumentException("Arguments needed: <linePath> <bSequence>");
		}

		String linePath = args[0];
		String bSequence = args[1];

		// Confirm Line
		File lineFile = new File(linePath);

		if (!lineFile.exists()) {
			throw new IllegalArgumentException(lineFile.getAbsolutePath() + "\nLine File not existing!");
		}

		PolyLine line = PolyLine.readLine(lineFile);

		// Confirm b sequence
		String[] split = bSequence.split(",");
		double[] bs = new double[split.length];

		for (int i = 0; i < split.length; i++) {
			try {
				bs[i] = Double.parseDouble(split[i]);
			} catch (Exception e) {
				throw new NumberFormatException("Could not parse \"" + split[i] + "\" to a double in the b-sequence.");
			}
		}

		String outName = "practB-" + lineFile.getName().split(".")[0];

		File output = new File(outName + ".csv");
		int count = 1;
		while (output.exists()) {
			output = new File(outName + "-" + (count++) + ".csv");
		}
		output.createNewFile();

		BufferedWriter out = new BufferedWriter(new FileWriter(output));
		out.write("b,pract time,pract error,tighten calls,greedy time, greedy error\n");

		LineSimplifier greedy = new Greedy();
		DistanceMeasure distance = new Frechet();

		ObjectiveFunction objective = new Sum();

		long t1 = System.nanoTime();
		Tuple<int[], double[]> greedyResult = greedy.simplify(line, distance);
		long t2 = System.nanoTime();

		double greedyTime = ((double) (t2 - t1)) / 1000.0;
		double[] greedyErrorSeq = objective.measure(greedyResult.l, greedyResult.r);
		double greedyError = greedyErrorSeq[greedyErrorSeq.length - 1];

		count = 0;
		for (double b : bs) {
			
			LineSimplifier pract = new GreedyPractical(b, c);
			
			t1 = System.nanoTime();
			Tuple<int[], double[]> practResult = pract.simplify(line, distance);
			t2 = System.nanoTime();

			double practTime = ((double) (t2 - t1)) / 1000.0;
			double[] practErrorSeq = objective.measure(practResult.l, practResult.r);
			double practError = practErrorSeq[practErrorSeq.length - 1];
			
			out.write(b + "," + practTime + "," + practError + "," + GreedyPractical.calls + "," + greedyTime + "," + greedyError + "\n");
			out.flush();
			System.out.println((++count) + "/" + bs.length);
		}
		
		out.close();

	}

}
