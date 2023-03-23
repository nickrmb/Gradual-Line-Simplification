package line;

public class Vertex {

	private double x, y;

	public Vertex(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the euclidean distance to another point
	 * 
	 * @param other The other point
	 * @return the distance
	 */
	public double distanceTo(Vertex other) {
		return Math.sqrt(squaredDistanceTo(other));
	}

	/**
	 * Gets the squared euclidean distance to another point
	 * 
	 * @param other The other point
	 * @return the distance
	 */
	public double squaredDistanceTo(Vertex other) {
		return (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y);
	}

	/**
	 * Returns a new point that is equal to this point minus another point
	 * 
	 * @param other The other point
	 * @return The new point
	 */
	public Vertex minus(Vertex other) {
		return new Vertex(x - other.x, y - other.y);
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
		return "(" + x + "," + y + ")";
	}

}
