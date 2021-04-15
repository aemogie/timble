package com.theaemogie.timble;

import com.theaemogie.timble.timble.Window;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Main {
	
	public static Window mainWindow;
	
	public static void main(String... args) {
		mainWindow = Window.create("Timble Game Engine (By Aemogie.)");
		mainWindow.run(1);
	}
}