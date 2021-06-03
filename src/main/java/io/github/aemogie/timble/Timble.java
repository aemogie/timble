package io.github.aemogie.timble;

import io.github.aemogie.timble.scenes.LevelScene;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.PresetsSettings;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Timble {
	public static Window mainWindow;
	public static void main(String[] args) {
		mainWindow = Window.create("Timble Game Engine (By aemogie.)", PresetsSettings.GREY, false, true);
		mainWindow.run(new LevelScene(mainWindow));
	}
}