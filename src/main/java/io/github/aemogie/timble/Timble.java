package io.github.aemogie.timble;

import io.github.aemogie.timble.scenes.LevelScene;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.Logger;
import io.github.aemogie.timble.util.PresetsSettings;

import java.io.IOException;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Timble {
	public static Window mainWindow;
	public static void main(String[] args) {
		mainWindow = Window.create("Timble Game Engine (By aemogie.)", PresetsSettings.GREY, false, true);
		try {
			mainWindow.run(new LevelScene(mainWindow));
		} catch (IOException e) {
			Logger.logFatal("Invalid scene '.json' file for selected scene.");
		}
	}
}