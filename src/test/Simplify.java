package test;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasurement;
import distance.FrechetApprox;
import line.PolyLine;
import simplification.LineSimplifier;
import util.Tuple;
import util.Util;

public class Simplify {

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		Tuple<Tuple<PolyLine, LineSimplifier>, DistanceMeasurement> fromArgs = Simplify.getFromArgs(args);

		PolyLine line = fromArgs.l.l;
		LineSimplifier simplifier = fromArgs.l.r;
		DistanceMeasurement distance = fromArgs.r;

		long t1 = System.nanoTime();
		Tuple<int[], double[]> solution = simplifier.simplify(line, distance);
		long t2 = System.nanoTime();

		long dif = t2 - t1;

		double timeBetweenMS = Math.round(dif / 10000.0) / 100.0;

		if (solution.r == null) {
			solution.r = Util.errorFromSimplification(solution.l, line, distance);
		}

		double error = solution.r[solution.r.length - 1];
		double time = timeBetweenMS;
		int[] simplification = solution.l;

		String lineName = new File(args[0]).getName();

		System.out.println("Line: " + lineName);
		System.out.println("Summed-" + distance.toString() + "-Distance: " + error);
		System.out.println("Computed in: " + time + " ms");
		for(int i = 0; i < simplification.length; i++) {
			System.out.print(simplification[i]);
			if(i != simplification.length - 1) {
				System.out.print(", ");
			}
		}

	}

	protected static Tuple<Tuple<PolyLine, LineSimplifier>, DistanceMeasurement> getFromArgs(String[] args)
			throws NumberFormatException, IOException, DataFormatException {
		if (args.length < 3) {
			throw new IllegalArgumentException(
					"Arguments must be given by: <lineFilePath> <simplifier> <distanceType> <optimal:distanceArgument>");
		}

		String lineFilePath = args[0];
		String simplifierString = args[1];
		String errorType = args[2];

		// Validate and read input

		File file = new File(lineFilePath);
		PolyLine line = PolyLine.readLine(file);

		LineSimplifier simplifier = Util.fromStringToSimplifier(simplifierString);
		if (simplifier == null) {
			throw new IllegalArgumentException(
					"Simplifier type not found, available: " + Util.getAvailableSimplifiers());
		}

		DistanceMeasurement distance = Util.fromStringToDistance(errorType);
		if (distance == null) {
			throw new IllegalArgumentException("Distance not found, available: " + Util.getAvailableDistances());
		}

		if (args.length > 3) {
			if (distance instanceof FrechetApprox) {
				try {
					int i = Integer.parseInt(args[3]);
					if (i <= 0) {
						throw new IllegalArgumentException("distanceArgument must be greater than 0");
					}
					((FrechetApprox) distance).setIterations(i);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("distanceArgument is not an integer");
				}
			}
		}

		// validated

		return new Tuple<>(new Tuple<>(line, simplifier), distance);
	}

}
