package io.github.aemogie.timble.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import io.github.aemogie.timble.components.Component;
import io.github.aemogie.timble.components.SpriteRenderer;
import io.github.aemogie.timble.eventhandlers.MouseListener;
import io.github.aemogie.timble.renderer.Renderer;
import io.github.aemogie.timble.tiled.Tile;
import io.github.aemogie.timble.tiled.TiledMap;
import io.github.aemogie.timble.timble.Camera;
import io.github.aemogie.timble.timble.GameObject;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.util.AssetPool;
import io.github.aemogie.timble.util.typeadapter.ComponentTypeAdapter;
import io.github.aemogie.timble.util.typeadapter.GameObjectTypeAdapter;
import io.github.aemogie.timble.util.typeadapter.PathTypeAdapter;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.aemogie.timble.util.Logger.debugLog;
import static io.github.aemogie.timble.util.StringUtils.resourcePath;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Scene {
	
	protected transient Renderer renderer = new Renderer();
	protected Camera camera = null;
	protected List<GameObject> gameObjects = new ArrayList<>();
	protected transient boolean cameraLoaded = false;
	protected Vector2i mapScale = new Vector2i();
	protected final Vector2i SCALE;
	private transient boolean isRunning = false;
	private final List<Tile> tiles = new ArrayList<>();
	private final Path MAP_PATH;
	protected final float FADE_TIME;
	private final List<Vector2f> dynLights = new ArrayList<>(128);
	float dlIntensity = 0; //0 = Off, 1 = Max
	float dlInitRadius = 256;
	protected final Window WINDOW;
	private final Path FILEPATH;
	
	@Deprecated
	public Scene(final Window WINDOW, final Vector2i SCALE, final float FADE_TIME, final Path MAP_PATH) {
		this.WINDOW = WINDOW;
		this.SCALE = SCALE;
		this.FADE_TIME = FADE_TIME;
		this.MAP_PATH = MAP_PATH;
		this.FILEPATH = null;
	}
	
	public Scene(final Window WINDOW,final String FILE) throws IOException {
		this.WINDOW = WINDOW;
		this.FILEPATH = Paths.get(".run", "scenes", FILE);
		
		String file = new String(Files.readAllBytes(this.FILEPATH));
		JsonObject jsonObject = new JsonStreamParser(file).next().getAsJsonObject();
		JsonObject scale = jsonObject.get("tileScale").getAsJsonObject();
		this.SCALE = new Vector2i(scale.get("x").getAsInt(), scale.get("y").getAsInt());
		this.FADE_TIME = jsonObject.get("fadeTime").getAsFloat();
		this.dlInitRadius = jsonObject.get("dynamicLightRadius").getAsFloat();
		this.dlIntensity = jsonObject.get("dynamicLightIntensity").getAsFloat()/100f;
		this.MAP_PATH = resourcePath(jsonObject.get("mapPath").getAsString());
	}
	
	public void init(Window window) {
		if (!cameraLoaded) {
			debugLog("Creating new camera.");
			camera = new Camera();
			camera.init(new Vector2f());
		}
		camera.adjustProjection();
		new TiledMap(MAP_PATH, SCALE.x, SCALE.y, this);
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
		return SCALE.x;
	}
	
	public int getTileHeight() {
		return SCALE.y;
	}
	
	public float getFadeTime() {
		return FADE_TIME;
	}
	
	public Window getWindow() {
		return WINDOW;
	}
	
	public void addDynLight(Vector2f light) {
		if (dynLights.size() < 128) dynLights.add(light);
	}
	
	public Vector2f[] getDynLights() {
		return dynLights.toArray(Vector2f[]::new);
	}
	
	public void setDlIntensity(float intensity) {
		this.dlIntensity = intensity;
	}
	
	public float getDlIntensity() {
		return dlIntensity;
	}
	
	public float getDLRadius() {
		return dlInitRadius;
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
		return this.getClass().getSimpleName() + " (" + FILEPATH + ")";
	}
}