package simplifier.greedy;

import distance.DistanceMeasure;
import distance.Frechet;
import distance.FrechetApprox;
import line.PolyLine;
import simplifier.LineSimplifier;
import util.Tuple;

public class GreedyPractical implements LineSimplifier {
	private double b, c;
	
	public static int calls;

	public GreedyPractical(double b, double c) {
		this.b = b;
		this.c = c;
	}

	@Override
	public Tuple<int[], double[]> simplify(PolyLine l, DistanceMeasure distance) {
		calls = 0;
		double eps = new Frechet().measure(l, 0, l.length() - 1);
		double upper = 2 * eps;
		double stop = eps / Math.pow(l.length(), c);

		int numPointsBetween = l.length() - 2;

		int[] simplification = new int[numPointsBetween];
		double[] error = new double[numPointsBetween];

		// create min node heap
		MinNodeHeap minHeap = new MinNodeHeap(l, upper, stop, b);

		// for every node between
		for (int i = 0; i < numPointsBetween; i++) {
			// extract
			HeapNode node = minHeap.extract();

			simplification[i] = node.index;
			error[i] = node.error;
		}

		return new Tuple<>(simplification, error);

	}

	@Override
	public String toString() {
		return "GreedyPract";
	}

	class MinNodeHeap {

		private PolyLine line;

		private HeapNode[] heap;
		private int length;
		
		private double upper, stop, b;

		public MinNodeHeap(PolyLine line, double upper, double stop, double b) {
			this.upper = upper;
			this.stop = stop;
			this.b = b;
			length = line.length();
			this.line = line;

			heap = new HeapNode[length];

			// initialize heap
			heap[0] = new HeapNode(0);
			for (int i = 1; i < length; i++) {
				heap[i] = new HeapNode(i);
				heap[i].left = heap[i - 1];
			}
			for (int i = 0; i < length - 1; i++) {
				heap[i].right = heap[i + 1];
			}

			// calculate errors
			for (int i = 0; i < length; i++) {
				heap[i].calculateError(line, upper, stop, b);
			}

			// heapify
			for (int i = length / 2 - 1; i >= 0; i--) {
				downwardsSink(i);
			}
		}

		/**
		 * Extracts the current minimum and reheapifies the heap
		 * 
		 * @return the extracted heap node
		 */
		public HeapNode extract() {
			// get smallest
			HeapNode smallest = heap[0];

			// get neighbors
			HeapNode left = smallest.left;
			HeapNode right = smallest.right;

			// get error of neighbors before
			double leftBefore = left.error;
			double rightBefore = right.error;

			// update neighbors
			smallest.updateAtRemove(line, upper, stop, b);

			// new errors of neighbors
			double leftAfter = left.error;
			double rightAfter = right.error;

			// sink depending on direction
			if (leftAfter < leftBefore)
				upwardsSink(left.heapIndex);
			if (rightAfter < rightBefore)
				upwardsSink(right.heapIndex);

			if (leftAfter > leftBefore)
				downwardsSink(left.heapIndex);
			if (rightAfter > rightBefore)
				downwardsSink(right.heapIndex);

			// update heap
			length--;
			swap(0, length);
			downwardsSink(0);

			// return extracted node
			return heap[length];
		}

		//
		private void downwardsSink(int i) {
			Tuple<Integer, Integer> children = getChildrenOf(i);
			Integer i1 = children.l;
			Integer i2 = children.r;

			if (i1 == null)
				return;

			int smaller = i1;

			if (i2 != null)
				smaller = (heap[i1].compareTo(heap[i2]) <= 0) ? i1 : i2;

			if (heap[i].compareTo(heap[smaller]) <= 0) {
				return;
			}

			swap(i, smaller);
			downwardsSink(smaller);

		}

		private void upwardsSink(int i) {
			if (i >= 0 && i <= 2)
				return;

			int upwardsIndex = (i - 1) / 2;

			if (heap[i].compareTo(heap[upwardsIndex]) < 0) {
				swap(i, upwardsIndex);
				upwardsSink(upwardsIndex);
			}
		}

		private void swap(int i1, int i2) {
			HeapNode a = heap[i1];
			heap[i1] = heap[i2];
			heap[i2] = a;

			heap[i1].heapIndex = i1;
			heap[i2].heapIndex = i2;

		}

		private Tuple<Integer, Integer> getChildrenOf(int i) {
			Integer i1 = i * 2 + 1;
			if (length <= i1)
				i1 = null;
			Integer i2 = i * 2 + 2;
			if (length <= i2) {
				i2 = null;
			}
			return new Tuple<>(i1, i2);
		}

		public boolean isSorted(int i) {
			Tuple<Integer, Integer> children = getChildrenOf(i);
			boolean childrenSorted = true;
			if (children.l != null) {
				if (heap[i].compareTo(heap[children.l]) > 0)
					return false;

				childrenSorted = isSorted(children.l);

				if (children.r != null) {
					if (heap[i].compareTo(heap[children.r]) > 0)
						return false;

					if (childrenSorted)
						childrenSorted = isSorted(children.r);

				}
			}
			return childrenSorted;
		}

		public void print(int i) {
			for (int x = i; x < length; x++) {
				System.out.print(x + ":" + heap[x].error + ", ");

			}

		}

	}

	class HeapNode implements Comparable<HeapNode> {
		public HeapNode left;
		public HeapNode right;
		public int index;
		protected int heapIndex;

		public double error = 0;

		public HeapNode(int index) {
			this.index = index;
			heapIndex = index;
		}

		/**
		 * get removal error of node
		 * 
		 * @param l        The PolyLine
		 * @param distance The distance measure
		 */
		public void calculateError(PolyLine l, double upper, double stop, double b) {
			if (left == null || right == null) {
				error = Double.MAX_VALUE;
				return;
			}
			
			error = tightened(l, left.index, right.index, upper, stop, b);
		}
		
		private double tightened(PolyLine l, int from, int to, double upper, double stop, double b) {
			GreedyPractical.calls += 1;
			double cur = upper;
			while (cur >= stop && FrechetApprox.test(l, from, to, cur / b, 0.0)) {
				cur /= b;
			}
			return cur;
		}

		/**
		 * update neighbors of neighbors and their removal error
		 * 
		 * @param l        The PolyLine
		 * @param distance The distance Measure
		 */
		public void updateAtRemove(PolyLine l, double upper, double stop, double b) {
			left.right = right;
			right.left = left;

			left.calculateError(l, upper, stop, b);
			right.calculateError(l, upper, stop, b);
		}

		@Override
		public int compareTo(HeapNode o) {
			if (o == null)
				return 1;
			return (error < o.error) ? -1 : ((error == o.error) ? 0 : 1);
		}

	}

}