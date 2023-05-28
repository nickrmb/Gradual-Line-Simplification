package function;

public interface ObjectiveFunction {
	
	
	/**
	 * @param simplification
	 * @param error
	 * @return
	 */
	public double[] measure(int[] simplification, double[] error);

}
