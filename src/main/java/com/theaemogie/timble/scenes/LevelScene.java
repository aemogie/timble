package com.theaemogie.timble.scenes;

import com.theaemogie.timble.components.MovementController;
import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.tiles.SpriteSheet;
import com.theaemogie.timble.tiles.TiledMap;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.theaemogie.timble.util.StringUtils.resourcePath;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelScene extends Scene {
	
	GameObject player = null;
	float clickDebounce = 1f;
	boolean released = true;
	
	public LevelScene() {
		super(new Vector2i(32, 32));
	}
	
	@Override
	public void init(Window window) {
		super.init(window);
		loadResources();
		//region Player.
		if (player == null) {
			player = new GameObject("Player", new Transform(new Vector2f(0, 0), new Vector2f(scale)));
			player.addComponent(new SpriteRenderer().setSprite(AssetPool.getSpriteSheet(resourcePath("sprites/sprite1_spritesheet.png")).getSprite(4)));
		}
		player.addComponent(new MovementController().setScene(this));
		addGameObjectToScene(player);
		//endregion
		new TiledMap(resourcePath("tilemap/map.json"), scale.x, scale.y, this);
		super.loadResources();
	}

	@Override
	protected void loadResources() {
		AssetPool.addSpriteSheet(
				resourcePath("sprites/sprite1_spritesheet.png"),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("sprites/sprite1_spritesheet.png")),
						16,
						16,
						16,
						0
				)
		);
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		super.update(window, deltaTime);
		zoom((float) deltaTime);
		if (KeyListener.isKeyPressed(GLFW_KEY_G)) {
			removeGameObjectFromScene(player);
		}
	}
	
	@Override
	public void postFrame(Window window, double deltaTime) {
		if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			int x = (int) MouseListener.getScreenX(window);
			int y = (int) MouseListener.getScreenY(window);
			int gameObjectID = window.getPickingTexture().readPixel(x, y);
			if (gameObjectID < 0) return;
			this.removeGameObjectFromScene(getGameObject(gameObjectID));
		}
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
	
	@Override
	public void saveExit() {
		player.removeComponent(MovementController.class);
		super.saveExit();
		try (PrintWriter playerFile = new PrintWriter(new FileWriter(".run/player.dat", false))) {
			playerFile.print(gson.toJson(player));
		} catch (IOException ignored) {
		}
	}
	
	@Override
	public void load() {
		super.load();
		String playerFile = "";
		try {
			playerFile = new String(Files.readAllBytes(Paths.get(".run/player.dat")));
		} catch (IOException ignored) {
		}
		if (playerFile.equals("") || playerFile.equals("{}")) {
			return;
		}
		this.player = gson.fromJson(playerFile, GameObject.class);
	}
}