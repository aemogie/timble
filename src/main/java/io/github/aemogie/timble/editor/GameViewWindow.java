package io.github.aemogie.timble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import io.github.aemogie.timble.eventhandlers.MouseListener;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.TimbleMath;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class GameViewWindow {
	
	private float leftX, rightX, topY, bottomY;
	
	public void imGui(Window window) {
		ImGui.begin("Game View Window", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
		
		ImVec2 windowSize = getLargestSizeForViewPort(window);
		ImVec2 windowPos = getCenteredPositionForViewPort(windowSize);
		
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		
		ImVec2 topLeft = new ImVec2();
		ImGui.getCursorScreenPos(topLeft);
		topLeft.x -= ImGui.getScrollX();
		topLeft.y -= ImGui.getScrollY();
		
		leftX = topLeft.x;
		bottomY = topLeft.y;
		rightX = topLeft.x + windowSize.x;
		topY = topLeft.y + windowSize.y;
		
		int textureID = window.getFrameBuffer().getTextureID();
		ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);
		
		MouseListener.setGameViewPortPos(new Vector2f(topLeft.x, topLeft.y));
		MouseListener.setGameViewPortSize(new Vector2f(windowSize.x, windowSize.y));
		
		ImGui.end();
	}
	
	public boolean getWantCaptureMouse() {
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX && MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
	}
	
	private ImVec2 getLargestSizeForViewPort(Window window) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		Vector2i aspectSize = TimbleMath.generateXnYFromRatio(window, (int) windowSize.x, (int) windowSize.y);
		
		return new ImVec2(aspectSize.x, aspectSize.y);
	}
	
	private ImVec2 getCenteredPositionForViewPort(ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
		float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
		
		return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
	}
}
