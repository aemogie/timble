package com.theaemogie.timble.editor;

import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Window;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class PropertiesWindow {
	private GameObject activeGameObject = null;
	private PickingTexture pickingTexture;
	private Scene currentScene;
	
	public PropertiesWindow(PickingTexture pickingTexture) {
		this.pickingTexture = pickingTexture;
	}
	
	public void update(Window window, float deltaTime, Scene currentScene) {
		this.currentScene = currentScene;
		if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			int x = (int) MouseListener.getScreenX(window);
			int y = (int) MouseListener.getScreenY(window);
			int gameObjectID = pickingTexture.readPixel(x, y);
			activeGameObject = currentScene.getGameObject(gameObjectID);
		}
	}
	
	public void imGui() {
		if (activeGameObject != null) {
			ImGui.begin("Properties");
			activeGameObject.imGui();
			ImGui.end();
		}
	}
}
