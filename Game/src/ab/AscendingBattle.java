package ab;

import input.Input;
import model.Mesh;
import shader.Vector3f;
import texture.Texture;
import texture.TextureLoader;
import window.Window;

public class AscendingBattle {

	private int width = 800;
	private int height = 600;
	private String title = "Ascending Battle";

	private Camera camera;
	private WorldShader shader;
	// private Player player;
	private Texture playerTexture;
	private Mesh quadModel;

	public AscendingBattle() {
		init();
		ClientConnection.connect();
		// ClientConnection.players.add(player);
		loop();
		close();
	}

	private void init() {
		Window.createWindow(width, height, title);
		Window.enableDepthBuffer();
		playerTexture = TextureLoader.loadTexture("background.png");
		shader = new WorldShader();
		camera = new Camera(70f, 0.1f, 10000f);
		// player = new Player("blockout22", new Vector3f(0, 0f, -15f), new
		// Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		// player.setTextureID(playerTexture.getID());

		quadModel = new Mesh(Mesh.MESH_3D);
		quadModel.add(Quad3D.getVertices(), Quad3D.getTexCoords(), Quad3D.getIndices());

		shader.bind();
		shader.loadMatrix(shader.getProjectionMatrix(), camera.getProjectionMatrix());
		shader.unbind();
	}

	private void loop() {
		while (!Window.isCloseRequested()) {
			shader.bind();
			{
				shader.loadViewMatrix(camera);
				quadModel.enable();
				{
					// quadModel.render(shader, shader.getModelMatrix(),
					// player);
					for (int i = 0; i < ClientConnection.players.size(); i++) {
						quadModel.render(shader, shader.getModelMatrix(), ClientConnection.players.get(i));
					}
				}
				quadModel.disable();
			}
			shader.unbind();

			// player.getPosition().x = ClientConnection.x;

			// player.move();
			// player.setTranslation(ClientConnection.players.get(0));
			// System.out.println(player.getPosition());
			if (ClientConnection.players.size() > 0) {
				ClientConnection.players.get(0).move();
			}
			camera.update();
			Input.update(Window.getWindowID());
			Window.update();
			Window.sync(60);
		}
	}

	private void close() {
		playerTexture.cleanup();
		shader.cleanup();
		quadModel.cleanup();
		Window.close();
		ClientConnection.disconnect();
	}

	public static void main(String[] args) {
		new AscendingBattle();
	}
}
