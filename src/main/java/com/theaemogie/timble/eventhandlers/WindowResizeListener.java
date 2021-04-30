package com.theaemogie.timble.eventhandlers;

import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.TimbleMath;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class WindowResizeListener {
	public static void resizeCallback(Window window, int screenWidth, int screenHeight) {
		window.setWidth(screenWidth);
		window.setHeight(screenHeight);
		
		Vector2i aspectSize = TimbleMath.generateXnYFromRatio(window, screenWidth, screenHeight);
		
		int vpX = (screenWidth / 2) - (aspectSize.x / 2);
		int vpY = (screenHeight / 2) - (aspectSize.y / 2);
		
		glViewport(vpX, vpY, aspectSize.x, aspectSize.y);
	}
}
