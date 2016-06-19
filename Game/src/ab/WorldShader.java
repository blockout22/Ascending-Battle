package ab;

import shader.Matrix4;
import shader.Shader;
import shader.Vector3f;

public class WorldShader extends Shader{
	
	private static String VERTEX_SHADER = "worldVertexShader.glsl";
	private static String FRAGMENT_SHADER = "worldFragmentShader.glsl";
	
	private int modelMatrix, viewMatrix, projectionMatrix;

	public WorldShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		bindAttribLocation(0, "position");
		bindAttribLocation(1, "texCoords");
		linkAndValidate();
		
		modelMatrix = getUniformLocation("modelMatrix");
		viewMatrix = getUniformLocation("viewMatrix");
		projectionMatrix = getUniformLocation("projectionMatrix");
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4 matrix = createViewMatrix(camera);
		loadMatrix(viewMatrix, matrix);
	}
	
	public static Matrix4 createViewMatrix(Camera camera) {
		Matrix4 viewMatrix = new Matrix4();
		viewMatrix.setIdentity();
		Matrix4.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4.translate(negativeCameraPos, viewMatrix, viewMatrix);

		return viewMatrix;
}

	public int getModelMatrix() {
		return modelMatrix;
	}

	public int getViewMatrix() {
		return viewMatrix;
	}

	public int getProjectionMatrix() {
		return projectionMatrix;
	}

}
