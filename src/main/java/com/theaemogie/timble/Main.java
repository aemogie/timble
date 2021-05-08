package com.theaemogie.timble;

import com.theaemogie.timble.timble.Window;
import org.joml.Vector3f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Main {
	
	public static Window mainWindow;
	
	public static void main(String... args) {
		mainWindow = Window.create("Timble Game Engine (By Aemogie.)", new Vector3f(0.2f, 0.2f, 0.2f));
		mainWindow.run(1);
	}
}