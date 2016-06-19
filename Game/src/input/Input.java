package input;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

public class Input {

	public static ArrayList<Key> downKeys = new ArrayList<Key>();
	public static ArrayList<Key> downMouseButtons = new ArrayList<Key>();

	public static void update(long window) {
		for (int i = 0; i < downKeys.size(); i++) {
			isReleased(window, downKeys.get(i));
		}
		
		for(int i = 0; i < downMouseButtons.size(); i++)
		{
			isMouseReleased(window, downMouseButtons.get(i));
		}
		
//		System.out.println(downKeys.size());
	}

	public static boolean isKeyDown(long window, Key keyCode) {
		int key = GLFW.glfwGetKey(window, keyCode.getKeyCode());
		boolean isDown;

		if (key == GLFW.GLFW_PRESS) {
			isDown = true;
		} else {
			isDown = false;
		}

		return isDown;
	}

	public static boolean isPressed(long window, Key keyCode) {
		if (isKeyDown(window, keyCode)) {
			for (int i = 0; i < downKeys.size(); i++) {
				if (downKeys.get(i).equals(keyCode)) {
					return false;
				}
			}
			downKeys.add(keyCode);
			return true;
		}

		return false;
	}


	public static boolean isReleased(long window, Key keyCode) {
		isPressed(window, keyCode);
		if (!isKeyDown(window, keyCode)) {
			for (int i = 0; i < downKeys.size(); i++) {
				if (downKeys.get(i).equals(keyCode)) {
					downKeys.remove(i);
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isMousePressed(long window, Key keyCode)
	{
		if(isMouseDown(window, keyCode))
		{
			for(int i = 0; i < downMouseButtons.size(); i++){
				if(downMouseButtons.get(i).equals(keyCode)){
					return false;
				}
			}
			downMouseButtons.add(keyCode);
			return true;
		}
		return false;
	}
	
	public static boolean isMouseReleased(long window, Key keyCode)
	{
		isMousePressed(window, keyCode);
		if(!isMouseDown(window, keyCode))
		{
			for(int i = 0; i < downMouseButtons.size(); i++)
			{
				if(downMouseButtons.get(i).equals(keyCode))
				{
					downMouseButtons.remove(i);
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean isMouseDown(long window, Key keyCode){
		int key = GLFW.glfwGetMouseButton(window, keyCode.getKeyCode());
		boolean isDown;
		
		if(key == GLFW.GLFW_PRESS){
			isDown = true;
		}else{
			isDown = false;
		}
		
		return isDown;
	}

}
