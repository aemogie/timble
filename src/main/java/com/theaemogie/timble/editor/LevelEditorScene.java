package com.theaemogie.timble.editor;

import com.theaemogie.timble.components.EditorCamera;
import com.theaemogie.timble.components.GridLines;
import com.theaemogie.timble.components.MouseControls;
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

import static com.theaemogie.timble.util.StringUtils.resourcePath;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {
	
	private GameObject levelEditorComponents = null;
	
	public LevelEditorScene() {
		super(100, 100, 32, 32);
	}
	
	@Override
	public void init(Window window) {
		super.init(window);
		if (levelEditorComponents == null) {
			levelEditorComponents = new GameObject("Level Editor Components", new Transform())
					.addComponent(new MouseControls())
					.addComponent(new GridLines())
					.addComponent(new EditorCamera());
		}
		
		levelEditorComponents.getComponent(EditorCamera.class).init(this.camera);
		//region Biomes SpriteSheet.
		SpriteSheet biomesSpriteSheet = AssetPool.getSpriteSheet(resourcePath("tilemap/biomes.png"));
		//endregion
		//region Terrain SpriteSheet.
		SpriteSheet terrainSpriteSheet = AssetPool.getSpriteSheet(resourcePath("tilemap/terrain.png"));
		//endregion
		//region Decor SpriteSheet.
		SpriteSheet decorSpriteSheet = AssetPool.getSpriteSheet(resourcePath("tilemap/manmade.png"));
		//endregion
		//region Nature SpriteSheet.
		SpriteSheet natureSpriteSheet = AssetPool.getSpriteSheet(resourcePath("tilemap/nature.png"));
		//endregion
		
		//region Map.
//		new TileSet(resourcePath("tilemap/map.json"), biomesSpriteSheet, mapWidth, mapHeight, 32, 32, this);
		new TileSet(resourcePath("tilemap/map.json"), 32, 32, this);
//		new TileSet(resourcePath("tilemap/map.json"), decorSpriteSheet, 32, 32, this);
//		new TileSet(resourcePath("tilemap/map.json"), natureSpriteSheet, 32, 32, this);
		//endregion
	}
	
	@Override
	protected void loadResources() {
		//region Biomes SpriteSheet.
		AssetPool.addSpriteSheet(
				resourcePath("tilemap/biomes.png"),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("tilemap/biomes.png")),
						16,
						16,
						4,
						0
				)
		);
		//endregion
		// region Terrain SpriteSheet.
		AssetPool.addSpriteSheet(
				resourcePath("tilemap/terrain.png"),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("tilemap/terrain.png")),
						16,
						16,
						256,
						0
				)
		);
		//endregion
		//region Decor SpriteSheet.
		AssetPool.addSpriteSheet(
				resourcePath("tilemap/manmade.png"),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("tilemap/manmade.png")),
						16,
						16,
						64,
						0
				)
		);
		//endregion
		//region Nature SpriteSheet.
		AssetPool.addSpriteSheet(
				resourcePath("tilemap/nature.png"),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("tilemap/nature.png")),
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
	public void preFrame(Window window) {
		window.getFrameBuffer().bind();
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		levelEditorComponents.update(window, deltaTime);
		super.update(window, deltaTime);
	}
	
	@Override
	public void postFrame(Window window, double deltaTime) {
		window.getFrameBuffer().unbind();
		window.imGuiLayer.update(window, (float) deltaTime, this);
	}
	
	@Override
	public void end(Window window) {
		window.imGuiLayer.disposeImGui();
		super.end(window);
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
		super.load();
		String editorComponentsFile = "";
		try {
			editorComponentsFile = new String(Files.readAllBytes(Paths.get(".run/editorComponents.dat")));
		} catch (IOException ignored) {}
		if (!(editorComponentsFile.equals("") || editorComponentsFile.equals("{}"))) {
			this.levelEditorComponents = gson.fromJson(editorComponentsFile, GameObject.class);
		}
	}
}