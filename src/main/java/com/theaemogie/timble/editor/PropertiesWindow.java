package com.theaemogie.timble.editor;

import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.tiles.PickingTexture;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Window;
import imgui.ImGui;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class PropertiesWindow {
	private GameObject activeGameObject = null;
	private final PickingTexture pickingTexture;
	
	public PropertiesWindow(PickingTexture pickingTexture) {
		this.pickingTexture = pickingTexture;
	}
	
	public void update(Window window, Scene currentScene) {
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
			SpriteRenderer spriteRenderer = activeGameObject.getComponent(SpriteRenderer.class);
			if (spriteRenderer != null) {
				Vector4f color = spriteRenderer.getColor().toNormVec4();
				ImGui.newLine();
				ImGui.sameLine(ImGui.getWindowSizeX() / 2 - 32);
				ImGui.image(spriteRenderer.getTexture().getTextureID(), 64, 64,
						spriteRenderer.getTextureCoords()[2].x,
						spriteRenderer.getTextureCoords()[0].y,
						spriteRenderer.getTextureCoords()[0].x,
						spriteRenderer.getTextureCoords()[2].y,
						color.x,
						color.y,
						color.z,
						color.w
						);
			}
			activeGameObject.imGui();
			ImGui.end();
		}
	}
}
