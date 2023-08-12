package simplifier;

import distance.DistanceMeasure;
import line.PolyLine;
import util.Node;
import util.SC;
import util.SymmetricMatrix;
import util.Tuple;
import util.Util;

public abstract class DynamicProgramSimplifier implements LineSimplifier {
	
	private SymmetricMatrix shortcutDistance;
	private SymmetricMatrix error;
	private PolyLine l;
	private DistanceMeasure distanceMeasure;
	public int numPointsBetween;
	
	public abstract void onSetup(int length);
	public abstract void onMinFound(int i, int j, int minK);
	public abstract double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance);
	public abstract int[] backtrackRemovalSeq();
	public abstract SC[] backtrackShortcutSeq();
	public abstract void onNewHop();

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		this.l = l;
		this.distanceMeasure = distance;
		
		numPointsBetween = l.length() - 2;

		this.shortcutDistance = new SymmetricMatrix(l.length(), -1.0);
		this.error = new SymmetricMatrix(l.length(), 0);
		
		onSetup(l.length());

		// iterate through all hop distances
		for (int hop = 2; hop < l.length(); hop++) {
			onNewHop();
			
			// iterate through all possible pair of vertices
			for (int i = 0; i < l.length() - hop; i++) {
				int j = i + hop;

				// get shortcut error
				double shortcutDistance = getShortcutDistance(i, j);

				// get minimal k
				double min = Double.MAX_VALUE;
				int minK = -1;
				for (int k = i + 1; k < j; k++) {
					double dist = getError(i, j, k, error.getValue(i, k), error.getValue(k, j), shortcutDistance);
					if (dist < min) {
						min = dist;
						minK = k;
					}
				}

				error.setValue(i, j, Math.max(min, shortcutDistance));
				onMinFound(i, j, minK);
			}
		}
		
		int[] simplification = backtrackRemovalSeq();
		double[] error = new double[numPointsBetween];
		
		SC[] shortcutSeq = backtrackShortcutSeq();
		
		for(int i = 0; i < shortcutSeq.length; i++) {
			error[i] = getShortcutDistance(shortcutSeq[i]);
		}

		return new Tuple<>(simplification, error);
	}

	/**
	 * This method gets the shortcut error between two vertices, if shortcut is
	 * already calculated it is accessed in constant time
	 * 
	 * @param i The vertex where the shortcut starts
	 * @param j The vertex where the shortcut ends
	 * @param l The PolyLine 
	 * @param distanceMeasure The distance measure used
	 * @return
	 */
	public double getShortcutDistance(int i, int j) {
		// check valid
		int diff = i - j;
		if (diff >= -1 && diff <= 1) {
			return 0.0;
		}

		// check if calculated
		if (shortcutDistance.getValue(i, j) == -1.0) {
			
			// calculate
			double distance = distanceMeasure.measure(l, i, j);
			shortcutDistance.setValue(i, j, distance);
		}

		return shortcutDistance.getValue(i, j);
	}
	
	public double getShortcutDistance(SC sc) {
		return getShortcutDistance(sc.i, sc.j);
	}
	
	public SC[] fromRemovalToShortcutSeq(int[] simplification) {
		SC[] shortcutSeq = new SC[simplification.length];
		
		Node[] nodes = Util.createNodeArray(simplification.length + 2);
		
		for(int i = 0; i < simplification.length; i++) {
			int removal = simplification[i];
			shortcutSeq[i] = new SC(nodes[removal].left.index, nodes[removal].right.index);
			nodes[removal].updateAtRemove();
		}
		
		return shortcutSeq;
	}
	
	public void updateError(int i, int j, double err) {
		error.setValue(i, j, err);
	}

	@Override
	public String toString() {
		return "MinMax";
	}
	
	
}
