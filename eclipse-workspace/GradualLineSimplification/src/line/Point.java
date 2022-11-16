package line;

public class Point {
	
	private double x,y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the eucledian distance to another point
	 * @param other The other point
	 * @return the distance
	 */
	public double distanceTo(Point other) {
		return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + (100 * Math.round(x)) / 100 + "," + (100 * Math.round(y)) / 100 + ")";
	}
	
}
