package com.theaemogie.timble.editor;

import com.theaemogie.timble.components.EditorCamera;
import com.theaemogie.timble.components.GridLines;
import com.theaemogie.timble.components.MouseControls;
import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.tiles.SpriteSheet;
import com.theaemogie.timble.tiles.TileSet;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.AssetPool;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {
	
	private GameObject levelEditorComponents = null;
	
	public LevelEditorScene() {
	
	}
	
	@Override
	public void init(Window window) {
		super.init(window);
		if (levelEditorComponents == null) {
			levelEditorComponents = new GameObject("Level Editor Components", new Transform());
			levelEditorComponents.addComponent(new MouseControls());
			levelEditorComponents.addComponent(new GridLines());
			levelEditorComponents.addComponent(new EditorCamera());
		}
		levelEditorComponents.getComponent(EditorCamera.class).init(this.camera);
		
		loadResources();
		SpriteSheet landscapeSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/tilemap/landscapes.png");
		landscapeSpriteSheet.setLayerID(0);
		SpriteSheet decorSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/tilemap/manmade.png");
		decorSpriteSheet.setLayerID(1);
		SpriteSheet natureSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/tilemap/nature.png");
		natureSpriteSheet.setLayerID(2);
		
		new TileSet("src/main/resources/assets/tilemap/map.json", landscapeSpriteSheet, 100, 100, 32, 32, this);
		new TileSet("src/main/resources/assets/tilemap/map.json", decorSpriteSheet, 100, 100, 32, 32, this);
		new TileSet("src/main/resources/assets/tilemap/map.json", natureSpriteSheet, 100, 100, 32, 32, this);
	}
	
	@Override
	protected void loadResources() {
		//region Landscapes SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/tilemap/landscapes.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/tilemap/landscapes.png"),
						16,
						16,
						256,
						0
				)
		);
		//endregion
		//region Decor SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/tilemap/manmade.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/tilemap/manmade.png"),
						16,
						16,
						64,
						0
				)
		);
		//endregion
		//region Nature SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/tilemap/nature.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/tilemap/nature.png"),
						16,
						16,
						64,
						0
				)
		);
		//endregion
		super.loadResources();
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		levelEditorComponents.update(window, deltaTime);
		super.update(window, deltaTime);
	}
	
	@Override
	public void render(Window window) {
		this.renderer.render(window);
	}
	
	@Override
	public void saveExit() {
		super.saveExit();
		try {
			FileWriter editorComponentsFile = new FileWriter(".run/editorComponents.dat");
			editorComponentsFile.write(gson.toJson(levelEditorComponents));
			editorComponentsFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void load() {
		String editorComponentsFile = "";
		try {
			editorComponentsFile = new String(Files.readAllBytes(Paths.get(".run/editorComponents.dat")));
		} catch (IOException ignored) {}
		if (!(editorComponentsFile.equals("") || editorComponentsFile.equals("{}"))) {
			this.levelEditorComponents = gson.fromJson(editorComponentsFile, GameObject.class);
		}
	}
}