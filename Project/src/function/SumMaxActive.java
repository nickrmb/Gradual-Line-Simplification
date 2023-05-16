package function;

import java.util.Comparator;
import java.util.PriorityQueue;

import util.Node;
import util.Util;

public class SumMaxActive implements OptimizationFunction {
	
	private double[] errors;
	
	private final Comparator<Integer> comp = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return Double.compare(errors[o2], errors[o1]);
		}
	};

	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];

		errors = new double[simplification.length + 1];
		PriorityQueue<Integer> pq = new PriorityQueue<>(errors.length, comp);
		
		Node[] nodes = Util.createNodeArray(error.length + 2);
		
		double sum = 0.0;
		for(int i = 0; i < simplification.length; i++) {
			int cur = simplification[i];
			int left = nodes[cur].left.index;
			double err = error[i];
			
			pq.remove(left);
			pq.remove(cur);
			
			nodes[cur].updateAtRemove();
			
			errors[left] = err;
			pq.add(left);
			
			int max = pq.peek();
			sum += errors[max];
			
			sequence[i] = sum;
		}
		
		return sequence;
	}
	
	@Override
	public String toString() {
		return "SumMaxActive";
	}
	
	
}
