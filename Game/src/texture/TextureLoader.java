package texture;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class TextureLoader {
	
	public static Texture loadTexture(String fileName){
		try {
			InputStream in = TextureLoader.class.getResourceAsStream("/" + fileName);
			BufferedImage img = ImageIO.read(in);
			
			int[] pixels = new int[img.getWidth() * img.getHeight()];
			img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
			
			ByteBuffer buffer = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4);
			
			for(int h = 0; h<img.getHeight(); h++){
				for(int w = 0; w < img.getWidth(); w++){
					int pixel = pixels[h * img.getWidth() + w];
					
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) (pixel & 0xFF));
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
			
			buffer.flip();
			in.close();
			
			int textureID = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
			
			GL11.glEnable (GL11.GL_BLEND);
			GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			return new Texture(textureID, img.getWidth(), img.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Texture(1, 0, 0);
	}

}
