package function;

import util.Node;
import util.Util;

public class SumSumActive implements OptimizationFunction {

	@Override
	public double[] measure(int[] simplification, double[] error) {
		double[] sequence = new double[error.length];
		
		Node[] nodes = Util.createNodeArray(error.length + 2);
		double[] lastError = new double[nodes.length];
		
		double sum = 0.0;
		double cur = 0.0;
		for(int i = 0; i < error.length; i++) {
			int removed = simplification[i];
			Node n = nodes[removed];
			int left = n.left.index;
			cur = cur - lastError[left] - lastError[removed] + error[i];
			sum += cur;
			lastError[left] = error[i];
			sequence[i] = sum;
			n.updateAtRemove();
		}
		
		return sequence;
	}
	
	@Override
	public String toString() {
		return "SumSumActive";
	}
	

}
