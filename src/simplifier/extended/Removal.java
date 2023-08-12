package simplifier.extended;

import util.SC;

public class Removal {
	
	private int idx;
	private SC shortcut;
	private double error;
	
	public Removal(int idx, SC shortcut, double error) {
		this.idx = idx;
		this.shortcut = shortcut;
		this.error = error;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public SC getShortcut() {
		return shortcut;
	}

	public void setShortcut(SC shortcut) {
		this.shortcut = shortcut;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

}
