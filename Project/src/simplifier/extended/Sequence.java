package simplifier.extended;

public class Sequence {
	
	private double error;
	private Removal[] seq;
	
	public Sequence(double error, Removal[] seq) {
		super();
		this.error = error;
		this.seq = seq;
	}
	public double getError() {
		return error;
	}
	public void setError(double error) {
		this.error = error;
	}
	public Removal[] getSeq() {
		return seq;
	}
	public void setSeq(Removal[] seq) {
		this.seq = seq;
	}
	
}
