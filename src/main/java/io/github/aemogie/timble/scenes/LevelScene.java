package io.github.aemogie.timble.scenes;

import io.github.aemogie.timble.components.MovementController;
import io.github.aemogie.timble.components.SpriteRenderer;
import io.github.aemogie.timble.eventhandlers.KeyListener;
import io.github.aemogie.timble.renderer.SpriteSheet;
import io.github.aemogie.timble.timble.GameObject;
import io.github.aemogie.timble.timble.Transform;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.AssetPool;
import io.github.aemogie.timble.util.Time;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.github.aemogie.timble.util.StringUtils.resourcePath;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelScene extends Scene {
	
	final float timeInDay = 30f;
	GameObject player = null;
	float clickDebounce = 1f;
	boolean released = true;
	float currentTime = 0f;
	
	public LevelScene(Window window) throws IOException {
		super(window,"levelscene.json");
	}
	
	@Override
	public void init(Window window) {
		loadResources();
		super.init(window);
		//region Player.
		if (player == null) {
			player = new GameObject("Player", new Transform(new Vector2f(0, 0), new Vector2f(SCALE)));
			player.addComponent(new SpriteRenderer().setSprite(AssetPool.getSpriteSheet(resourcePath("sprites/sprite1_spritesheet.png")).getSprite(4)));
		}
		player.transform.scale = new Vector2f(SCALE);
		addGameObjectToScene(player);
		//endregion
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
						0
				)
		);
	}
	
	@Override
	public void preFrame(Window window) {
		super.preFrame(window);
		this.addDynLight(getDLPlayerPos());
	}
	
	@Override
	public void update(double deltaTime) {
		if (currentTime >= timeInDay) {
			currentTime = (float) deltaTime;
		} else {
			currentTime += deltaTime;
		}
		super.update(deltaTime);
		zoom((float) deltaTime);
		if (Time.getTime() > FADE_TIME && player.getComponent(MovementController.class) == null) {
			player.addComponent(new MovementController().setScene(this));
		}

//		camera.smoothFollow(WINDOW, new Transform(new Vector2f(WINDOW.getWidth()/2f, WINDOW.getHeight()/2f)), 0);
		camera.smoothFollow(WINDOW.getWidth(), WINDOW.getHeight(), mapScale.x * SCALE.x + SCALE.x, mapScale.y * SCALE.y + SCALE.y, player.transform, 0.045f);
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
	
	public Vector2f getDLPlayerPos() {
		Vector2f playerOrigin = new Vector2f(player.transform.position.x, player.transform.position.y);
		Vector2f playerCenter = new Vector2f(player.transform.scale).div(2);
		return playerOrigin.add(playerCenter);
	}
}