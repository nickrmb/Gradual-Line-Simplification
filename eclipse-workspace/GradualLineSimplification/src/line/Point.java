package line;

public class Point {

	private double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the euclidean distance to another point
	 * 
	 * @param other The other point
	 * @return the distance
	 */
	public double distanceTo(Point other) {
		return Math.sqrt(squaredDistanceTo(other));
	}

	/**
	 * Gets the squared euclidean distance to another point
	 * 
	 * @param other The other point
	 * @return the distance
	 */
	public double squaredDistanceTo(Point other) {
		return (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y);
	}

	/**
	 * Returns a new point that is equal to this point minus another point
	 * 
	 * @param other The other point
	 * @return The new point
	 */
	public Point minus(Point other) {
		return new Point(x - other.x, y - other.y);
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
