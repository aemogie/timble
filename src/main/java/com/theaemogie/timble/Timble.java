package com.theaemogie.timble;

import com.theaemogie.timble.timble.Window;

import static com.theaemogie.timble.util.PresetsSettings.GREY;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Timble {
	public static Window mainWindow;
	public static void main(String[] args) {
		mainWindow = Window.create("Timble Game Engine (By Aemogie.)", GREY, false, true);
		mainWindow.run(1);
	}
}