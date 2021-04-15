package com.theaemogie.timble.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theaemogie.timble.components.Component;
import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.renderer.Renderer;
import com.theaemogie.timble.timble.Camera;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.util.AssetPool;
import com.theaemogie.timble.util.typeadapter.ComponentTypeAdapter;
import com.theaemogie.timble.util.typeadapter.GameObjectTypeAdapter;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Scene {
	
	protected transient Renderer renderer = new Renderer();
	protected Camera camera = null;
	protected List<GameObject> gameObjects = new ArrayList<>();
	protected transient boolean cameraLoaded = false;
	private transient boolean isRunning = false;
	private List<GameObject> tiles = new ArrayList<>();
	protected int mapWidth, mapHeight;
	protected int tileWidth, tileHeight;
	
	public Scene() {
	}
	
	public void init(Window window) {
		if (!cameraLoaded) {
			camera = new Camera();
			camera.init(new Vector2f());
		}
		camera.adjustProjection();
	}
	
	public void start() {
		tiles.forEach(GameObject::start);
		tiles.forEach(tile -> renderer.add(tile));
		gameObjects.forEach(GameObject::start);
		gameObjects.forEach(gameObject -> renderer.add(gameObject));
		isRunning = true;
	}
	
	public void addTilesToScene(GameObject tile) {
		if (!isRunning) {
			tiles.add(tile);
		} else {
			tiles.add(tile);
			tile.start();
			this.renderer.add(tile);
		}
	}
	
	public void addGameObjectToScene(GameObject gameObject) {
		if (!isRunning) {
			gameObjects.add(gameObject);
		} else {
			gameObjects.add(gameObject);
			gameObject.start();
			this.renderer.add(gameObject);
		}
	}
	
	protected void loadResources() {
		for (GameObject tile : tiles) {
			if (tile.getComponent(SpriteRenderer.class) != null) {
				SpriteRenderer spriteRenderer = tile.getComponent(SpriteRenderer.class);
				if (spriteRenderer.getTexture() != null) {
					spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
				}
			}
		}
		for (GameObject gameObject : gameObjects) {
			if (gameObject.getComponent(SpriteRenderer.class) != null) {
				SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
				if (spriteRenderer.getTexture() != null) {
					spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
				}
			}
		}
	}
	
	public void preFrame(Window window) {}
	
	public void update(Window window, double deltaTime) {
		tiles.forEach(tile -> tile.update(window, deltaTime));
		gameObjects.forEach(gameObject -> gameObject.update(window, deltaTime));
	}
	
	public void render(Window window) {
		this.renderer.render(window);
	}
	
	public void postFrame(Window window, double deltaTime) {}
	
	public void end(Window window) {
		saveExit();
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public GameObject getGameObject(int ID) {
		Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUUID() == ID).findFirst();
		Optional<GameObject> tileResult = this.tiles.stream().filter(tile -> tile.getUUID() == ID).findFirst();
		return result.orElse(tileResult.orElse(null));
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	//region GSON stuff.
	
	//region GSON variable for usage.
	protected static Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Component.class, new ComponentTypeAdapter())
			.registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
			.create();
	//endregion
	
	//region Serialization.
	public void saveExit() {
		try {
			FileWriter cameraFile = new FileWriter(".run/camera.dat");
			cameraFile.write(gson.toJson(this.camera));
			cameraFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//endregion
	
	//region Deserialization
	public void load() {
		String cameraFile = "";
		try {
			cameraFile = new String(Files.readAllBytes(Paths.get(".run/camera.dat")));
		} catch (IOException ignored) {}
		if (!(cameraFile.equals("") || cameraFile.equals("{}"))) {
			this.camera = gson.fromJson(cameraFile, Camera.class);
			camera.init(camera.position);
			cameraLoaded = true;
		}
	}
	//endregion
	
	//endregion
}