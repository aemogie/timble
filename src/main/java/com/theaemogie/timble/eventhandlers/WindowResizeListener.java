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
		
		int vpX = (int) (((float) screenWidth / 2f) - ((float) aspectSize.x / 2f));
		int vpY = (int) (((float) screenHeight / 2f) - ((float) aspectSize.y / 2f));
		
		glViewport(vpX, vpY, aspectSize.x, aspectSize.y);
	}
}
