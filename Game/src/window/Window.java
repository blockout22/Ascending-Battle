package window;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Window {
	
	private static long windowID;
	private static int WIDTH;
	private static int HEIGHT;
	private static boolean shouldClose = false;
	
	private static long variableYieldTime, lastTime;
	//Time class (delta)
	private static long last_time;
	private static int frames = 0, fps = 0;
	private static boolean fpsChanged = false;
	
	public static void createWindow(int width, int height, String title)
	{
		Window.WIDTH = width;
		Window.HEIGHT = height;
		if(glfwInit() != GL_TRUE)
		{
			throw new IllegalStateException();
		}
		
		windowID = glfwCreateWindow(width, height, title, NULL, NULL);
		System.out.println("Window Created");
		
		if(windowID == NULL){
			System.err.println("Window returned NULL");
			System.exit(-1);
		}
		
		glfwMakeContextCurrent(windowID);
		glfwShowWindow(windowID);
		createCapabilities();
		glClearColor(0, 0, 1, 1);
		glViewport(0, 0, width, height);
		last_time = Time.getTime();
	}
	
	public static boolean isCloseRequested()
	{
		Time.setDelta();
		if(Time.getTime() - last_time >= 1000)
		{
			last_time += 1000;
			setFPS(frames);
			frames = 0;
			fpsChanged = true;
		}
		frames++;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		int w = WIDTH;
		int h = HEIGHT;
		
		int wCheck = Window.getWidth();
		int hCheck = Window.getHeight();
		
		if(w !=  wCheck|| h != hCheck)
		{
			glViewport(0, 0, wCheck, hCheck);
		}
		return glfwWindowShouldClose(Window.getWindowID()) == GL_TRUE | shouldClose;
	}
	
	public static void requestClose()
	{
		shouldClose = true;
	}
	
	private static void setFPS(int f)
	{
		fps = f;
	}
	
	public static int getFPS()
	{
		fpsChanged = false;
		return fps;
	}
	
	public static boolean hasFPSupdated()
	{
		return fpsChanged;
	}
	
	public static int getWidth() {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(windowID, width, height);
		Window.WIDTH = width.get();

		return Window.WIDTH;
	}

	public static int getHeight() {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(windowID, width, height);
		Window.HEIGHT = height.get();

		return Window.HEIGHT;
	}
	
	public static void sync(int fps) {
        if (fps <= 0) return;
          
        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by
          
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
                  
                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
             
            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }
	
	public static void enableDepthBuffer()
	{
		glEnable(GL_DEPTH_TEST);
	}
	
	public static void disableDepthBuffer()
	{
		glDisable(GL_DEPTH_TEST);
	}
	
	public static void update()
	{
		glfwSwapBuffers(windowID);
		glfwPollEvents();
	}
	
	public static void grabCursor()
	{
		glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	public static void releaseCursor()
	{
		glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public static void close(){
		glfwDestroyWindow(windowID);
		glfwTerminate();
	}

	public static long getWindowID()
	{
		return windowID;
	}
}
