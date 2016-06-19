package ab;

public class Vector3f {

	public float x;
	public float y;
	public float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
	}

	public Vector3f(float value) {
		this.x = value;
		this.y = value;
		this.z = value;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public String toString() {
		return x + "," + y + "," + z;
	}
}
