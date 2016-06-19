package ab;
public class Quad3D {
	public static float[] getVertices() {
		float[] vertices = {
				//
				-1f, 1f, 0f, // 0
				-1f, -1f, 0f, // 1
				1f, -1f, 0f, // 2
				1f, 1f, 0f // 3
		};

		return vertices;
	}

	public static int[] getIndices() {
		int[] indices = {
				//
				0, 1, 2, //
				2, 3, 0 //
		};

		return indices;
	}

	public static float[] getTexCoords() {
		float[] texCoords = {
				//
				0, 0, //
				0, 1, //
				1, 1, //
				1, 0 //
		};

		return texCoords;
	}
}
