package simplifier.simple;

public class MinMAX extends SimpleDP{

	@Override
	public double getError(int i, int j, int k, double error_ik, double error_kj, double shortcutDistance) {
		return Math.max(Math.max(error_ik, error_kj), shortcutDistance);
	}
	
	@Override
	public String toString() {
		return "MinMAX";
	}

}
