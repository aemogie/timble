package com.theaemogie.timble.scenes;

import com.theaemogie.timble.components.MovementController;
import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.tiles.SpriteSheet;
import com.theaemogie.timble.tiles.TileSet;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.AssetPool;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelScene extends Scene {
	
	public LevelScene() {
		mapWidth = 100;
		mapHeight = 100;
		tileWidth = 32;
		tileHeight = 32;
	}
	
	@Override
	public void init(Window window) {
		super.init(window);
		loadResources();
		//region Landscapes SpriteSheet.
		SpriteSheet landscapeSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/textures/tilemap/landscapes.png");
		landscapeSpriteSheet.setLayerID(0);
		//endregion
		//region Decor SpriteSheet.
		SpriteSheet decorSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/textures/tilemap/manmade.png");
		decorSpriteSheet.setLayerID(1);
		//endregion
		//region Nature SpriteSheet.
		SpriteSheet natureSpriteSheet = AssetPool.getSpriteSheet("src/main/resources/assets/textures/tilemap/nature.png");
		natureSpriteSheet.setLayerID(2);
		//endregion
		
		//region Map.
		new TileSet("src/main/resources/assets/textures/tilemap/map.json", landscapeSpriteSheet, mapWidth, mapHeight, 32, 32, this);
		new TileSet("src/main/resources/assets/textures/tilemap/map.json", decorSpriteSheet, mapWidth, mapHeight, 32, 32, this);
		new TileSet("src/main/resources/assets/textures/tilemap/map.json", natureSpriteSheet, mapWidth, mapHeight, 32, 32, this);
		//endregion
		
		//region Player.
		GameObject player = new GameObject("Player", new Transform(new Vector2f(mapWidth * tileWidth - tileWidth, 0), new Vector2f(32, 32)));
		player.addComponent(new SpriteRenderer().setSprite(AssetPool.getSpriteSheet("src/main/resources/assets/textures/sprites/sprite1_spritesheet.png").getSprite(4)));
		player.addComponent(new MovementController().setScene(this));
		addGameObjectToScene(player);
		//endregion
	}
	
	@Override
	protected void loadResources() {
		//region Landscapes SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/textures/tilemap/landscapes.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/textures/tilemap/landscapes.png"),
						16,
						16,
						256,
						0
				)
		);
		//endregion
		//region Decor SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/textures/tilemap/manmade.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/textures/tilemap/manmade.png"),
						16,
						16,
						64,
						0
				)
		);
		//endregion
		//region Nature SpriteSheet.
		AssetPool.addSpriteSheet(
				"src/main/resources/assets/textures/tilemap/nature.png",
				new SpriteSheet(
						AssetPool.getTexture("src/main/resources/assets/textures/tilemap/nature.png"),
						16,
						16,
						64,
						0
				)
		);
		//endregion
		AssetPool.addSpriteSheet("src/main/resources/assets/textures/sprites/sprite1_spritesheet.png", new SpriteSheet(AssetPool.getTexture("src/main/resources/assets/textures/sprites/sprite1_spritesheet.png"), 16, 16, 16, 0));
		super.loadResources();
	}
	
	float clickDebounce = 1f;
	boolean released = true;
	
	@Override
	public void update(Window window, double deltaTime) {
		super.update(window, deltaTime);
		zoom((float) deltaTime);
	}
	
	private void zoom(float deltaTime) {
		if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT_CONTROL))) {
			if (clickDebounce <= 0) {
				if (KeyListener.isKeyPressed(GLFW_KEY_MINUS) && !KeyListener.isKeyPressed(GLFW_KEY_EQUAL) && released) {
					camera.setZoom(camera.getZoom() * 2f);
					released = false;
				} else if (KeyListener.isKeyPressed(GLFW_KEY_EQUAL) && !KeyListener.isKeyPressed(GLFW_KEY_MINUS) && released) {
					camera.setZoom(camera.getZoom() / 2f);
					released = false;
				} else {
					clickDebounce = 0.064f;
					released = true;
				}
			} else {
				clickDebounce -= deltaTime;
			}
		}
	}
}