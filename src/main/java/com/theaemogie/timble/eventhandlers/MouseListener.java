package com.theaemogie.timble.eventhandlers;

import com.theaemogie.timble.timble.Camera;
import com.theaemogie.timble.timble.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class MouseListener {
	
	private static MouseListener instance;
	private boolean[] isButtonPressed = new boolean[9];
	private double scrollX, scrollY;
	private double xPos, yPos, lastX, lastY;
	private boolean isDragging;
	
	private Vector2f gameViewPortPos = new Vector2f();
	private Vector2f gameViewPortSize = new Vector2f();
	
	private MouseListener() {
		
		this.scrollX = 0.0;
		this.scrollY = 0.0;
		
		this.xPos = 0.0;
		this.yPos = 0.0;
		this.lastX = 0.0;
		this.lastY = 0.0;
	}
	
	public static MouseListener get() {
		if (MouseListener.instance == null) {
			MouseListener.instance = new MouseListener();
		}
		return MouseListener.instance;
	}
	
	public static void mousePositionCallback(long window, double xPos, double yPos) {
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		get().xPos = xPos;
		get().yPos = yPos;
		get().isDragging = get().isButtonPressed[0] || get().isButtonPressed[1] || get().isButtonPressed[2];
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int mods) {
		if (button < get().isButtonPressed.length) {
			if (action == GLFW_PRESS) {
				get().isButtonPressed[button] = true;
			} else if (action == GLFW_RELEASE) {
				get().isButtonPressed[button] = false;
				get().isDragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}
	
	public static boolean isButtonPressed(int button) {
		if (button < get().isButtonPressed.length) {
			return get().isButtonPressed[button];
		} else {
			return false;
		}
	}
	
	public static boolean isDragging() {
		return get().isDragging;
	}
	
	public static float getX() {
		return (float) get().xPos;
	}
	
	public static float getY() {
		return (float) get().yPos;
	}
	
	public static float getDX() {
		return (float) (get().lastX - get().xPos);
	}
	
	public static float getDY() {
		return (float) (get().lastY - get().yPos);
	}
	
	public static float getScrollX() {
		return (float) get().scrollX;
	}
	
	public static float getScrollY() {
		return (float) get().scrollY;
	}
	
	public static float getScreenX(Window window) {
		float currentX = getX() - get().gameViewPortPos.x; //getX();
		currentX = (currentX / get().gameViewPortSize.x) * (float) window.getWidth();
		return currentX;
	}
	
	public static float getScreenY(Window window) {
		float currentY = getY() - get().gameViewPortPos.y; //getY();
		currentY = (float) window.getHeight() - ((currentY / get().gameViewPortSize.y) * (float) window.getHeight());
		return currentY;
	}
	
	public static float getOrthoX(Window window) {
		float currentX = getX() - get().gameViewPortPos.x; //getX();
		currentX = (currentX / get().gameViewPortSize.x) * 2.0f - 1.0f; // (float) Window.get().getWidth()
		Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
		
		Camera camera = window.getCurrentScene().getCamera();
		Matrix4f viewProjection = new Matrix4f();
		camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
		tmp.mul(viewProjection);
		currentX = tmp.x;
		
		return currentX;
	}
	
	public static float getOrthoY(Window window) {
		float currentY = getY() - get().gameViewPortPos.y; //Window.get().getHeight() - getY();
		currentY = -((currentY / get().gameViewPortSize.y) * 2.0f - 1.0f); //(float) Window.get().getHeight()
		Vector4f tmp = new Vector4f(0, currentY, 0, 1);
		
		Camera camera = window.getCurrentScene().getCamera();
		Matrix4f viewProjection = new Matrix4f();
		camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
		tmp.mul(viewProjection);
		currentY = tmp.y;
		
		return currentY;
	}
	
	public static void setGameViewPortPos(Vector2f gameViewPortPos) {
		get().gameViewPortPos.set(gameViewPortPos);
	}
	
	public static void setGameViewPortSize(Vector2f gameViewPortSize) {
		get().gameViewPortSize.set(gameViewPortSize);
	}
	
	public static void endFrame() {
		get().scrollX = 0;
		get().scrollY = 0;
		get().lastX = get().xPos;
		get().lastY = get().yPos;
	}
}