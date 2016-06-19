package texture;

import org.lwjgl.opengl.GL11;

public class Texture {
	
	public int ID;
	public int width;
	public int height;
	
	public Texture(int ID, int width, int height) {
		this.ID = ID;
		this.width = width;
		this.height = height;
	}
	
	public int getID() {
		return ID;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

	public void cleanup() {
		GL11.glDeleteTextures(ID);
	}
}
