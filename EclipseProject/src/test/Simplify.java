package test;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import distance.DistanceMeasure;
import distance.FrechetApprox;
import function.OptimizationFunction;
import function.Sum;
import line.PolyLine;
import simplifier.LineSimplifier;
import util.Tuple;
import util.Util;

public class Simplify {

	public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {
		Tuple<Tuple<PolyLine, LineSimplifier>, DistanceMeasure> fromArgs = Simplify.getFromArgs(args);

		PolyLine line = fromArgs.l.l;
		LineSimplifier simplifier = fromArgs.l.r;
		DistanceMeasure distance = fromArgs.r;

		long t1 = System.nanoTime();
		Tuple<int[], double[]> solution = simplifier.simplify(line, distance);
		long t2 = System.nanoTime();

		long dif = t2 - t1;

		double timeBetweenMS = Math.round(dif / 10000.0) / 100.0;

		if (solution.r == null) {
			solution.r = Util.errorFromSimplification(solution.l, line, distance);
		}
		

		double time = timeBetweenMS;
		int[] simplification = solution.l;

		String lineName = new File(args[0]).getName();

		System.out.println("Line: " + lineName);
		
		for(OptimizationFunction func : Util.optFunctions) {

			double[] sequence = func.measure(simplification, solution.r);
			System.out.println(distance.toString() + "-Distance under " + func + ": " + sequence[sequence.length - 1]);
			
		}
		System.out.println("Computed in: " + time + " ms");

		boolean printsRemoval = false;

		for (String s : args) {
			if (s.equalsIgnoreCase("removal")) {
				printsRemoval = true;
				break;
			}
		}

		if (printsRemoval) {
			System.out.print("Removal sequence: ");
			for (int i = 0; i < simplification.length; i++) {
				System.out.print(simplification[i]);
				if (i != simplification.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}

	}

	protected static Tuple<Tuple<PolyLine, LineSimplifier>, DistanceMeasure> getFromArgs(String[] args)
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

		DistanceMeasure distance = Util.fromStringToDistance(errorType);
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
