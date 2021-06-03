package io.github.aemogie.timble.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.aemogie.timble.components.Component;
import io.github.aemogie.timble.components.SpriteRenderer;
import io.github.aemogie.timble.eventhandlers.MouseListener;
import io.github.aemogie.timble.renderer.Renderer;
import io.github.aemogie.timble.tiled.Tile;
import io.github.aemogie.timble.timble.Camera;
import io.github.aemogie.timble.timble.GameObject;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.AssetPool;
import io.github.aemogie.timble.util.typeadapter.ComponentTypeAdapter;
import io.github.aemogie.timble.util.typeadapter.GameObjectTypeAdapter;
import io.github.aemogie.timble.util.typeadapter.PathTypeAdapter;
import org.joml.Vector2f;
import org.joml.Vector2i;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.aemogie.timble.util.Logger.debugLog;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Scene {
	
	protected transient Renderer renderer = new Renderer();
	protected Camera camera = null;
	protected List<GameObject> gameObjects = new ArrayList<>();
	protected transient boolean cameraLoaded = false;
	protected Vector2i mapScale = new Vector2i();
	protected Vector2i scale;
	private transient boolean isRunning = false;
	private final List<Tile> tiles = new ArrayList<>();
	protected final float FADE_TIME;
	protected final Window WINDOW;
	
	public Scene(Window window, Vector2i scale, float fadeTime) {
		this.scale = scale;
		this.FADE_TIME = fadeTime;
		this.WINDOW = window;
	}
	
	public void init(Window window) {
		if (!cameraLoaded) {
			debugLog("Creating new camera.");
			camera = new Camera();
			camera.init(new Vector2f());
		}
		camera.adjustProjection();
	}
	
	public void start() {
		tiles.forEach(Tile::start);
		tiles.forEach(renderer::add);
		gameObjects.forEach(GameObject::start);
		gameObjects.forEach(renderer::add);
		isRunning = true;
	}
	
	public void addTilesToScene(List<Tile> tiles) {
		this.tiles.addAll(tiles);
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
	
	@SuppressWarnings("SuspiciousMethodCalls")
	public void removeGameObjectFromScene(GameObject gameObject) {
		if (gameObject == null) return;
		this.renderer.remove(gameObject);
		if (gameObjects.contains(gameObject)) {
			for (GameObject go : gameObjects) {
				if (go == null) continue;
				if (go.getUUID() == gameObject.getUUID()) {
					gameObjects.set(gameObjects.indexOf(gameObject), null);
					return;
				}
			}
		}
		if (tiles.contains(gameObject)) {
			for (GameObject tile : tiles) {
				if (tile == null) continue;
				if (tile.getUUID() == gameObject.getUUID()) tiles.set(tiles.indexOf(gameObject), null);
			}
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
	
	public void preFrame(Window window) {
		MouseListener.setGameViewPortPos(new Vector2f());
		MouseListener.setGameViewPortSize(new Vector2f(window.getWidth(), window.getHeight()));
	}
	
	public void update(double deltaTime) {
		tiles.stream().filter(Objects::nonNull).forEach(tile -> tile.update(WINDOW, deltaTime));
		gameObjects.stream().filter(Objects::nonNull).forEach(gameObject -> gameObject.update(WINDOW, deltaTime));
	}
	
	public void render() {
		this.renderer.render(this);
	}
	
	public void postFrame(Window window, double deltaTime) {}
	
	public void end(Window window) {
		saveExit();
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public GameObject getGameObject(int ID) {
		for (GameObject gameObject : gameObjects) {
			if (gameObject == null) continue;
			if (gameObject.getUUID() == ID) {
				return gameObject;
			}
		}
		for (GameObject tile : tiles) {
			if (tile == null) continue;
			if (tile.getUUID() == ID) {
				return tile;
			}
		}
		return null;
	}
	
	public int getMapWidth() {
		return mapScale.x;
	}
	
	public void setMapWidth(int x) {
		mapScale.x = x;
	}
	
	public int getMapHeight() {
		return mapScale.y;
	}
	
	public void setMapHeight(int y) {
		mapScale.y = y;
	}
	
	public int getTileWidth() {
		return scale.x;
	}
	
	public int getTileHeight() {
		return scale.y;
	}
	
	public float getFadeTime() {
		return FADE_TIME;
	}
	
	public Window getWindow() {
		return WINDOW;
	}
	
	//region GSON stuff.
	//region GSON variable for usage.
	protected static Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Component.class, new ComponentTypeAdapter())
			.registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
			.registerTypeAdapter(Path.class, new PathTypeAdapter())
			.create();
	//endregion
	//region Serialization.
	public void saveExit() {
	}
	//endregion
	//region Deserialization
	public void load() {}
	//endregion
	//endregion
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}