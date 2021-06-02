package com.theaemogie.timble.tiled;

import com.google.gson.*;
import com.theaemogie.timble.renderer.Color;
import com.theaemogie.timble.renderer.Sprite;
import com.theaemogie.timble.renderer.SpriteSheet;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.util.AssetPool;
import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.theaemogie.timble.util.StringUtils.resourcePath;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TiledMap {
	private final HashMap<Integer, SpriteSheet> spriteSheets = new HashMap<>();
	private final Tile[][][] tileArray; // [x][y][layer] - Retrieved in reverse i.e. tileArray[layer][y][x]
	private final Vector2f tileScale;
	private final List<Tile> tiles = new ArrayList<>();
	JsonObject jsonObject;
	private TiledMapLayer[] maps;
	private String source = "";
	
	public TiledMap(Path filepath, int tileWidth, int tileHeight, Scene scene) {
		load(filepath, scene);
		this.tileScale = new Vector2f(tileWidth, tileHeight);
		tileArray = new Tile[maps.length][][];
		for (int i = 0; i < maps.length; i++) {
			TiledMapLayer tml = maps[i];
			tileArray[i] = new Tile[tml.getWidth()][tml.getHeight()];
			int finalI = i;
			iterateXnY(tml.getWidth(), tml.getHeight(), (x, y) -> createTiles(x, y, tml, finalI));
		}
		scene.addTilesToScene(tiles);
	}
	
	private void load(Path filepath, Scene scene) {
		try {
			source = new String(Files.readAllBytes(filepath));
		} catch (IOException error) {
			System.err.println("Error while reading Tiled Map : " + "\"" + filepath + "\".");
			System.exit(-1);
		}
		jsonObject = new JsonStreamParser(source).next().getAsJsonObject();
		loadSpritesheets();
		loadMaps();
		
		int width = 0, height = 0;
		
		for (TiledMapLayer tml : maps) {
			if (tml.getWidth() > width) width = tml.getWidth();
			if (tml.getHeight() > height) height = tml.getHeight();
		}
		
		scene.setMapWidth(jsonObject.getAsJsonPrimitive("width").getAsInt());
		scene.setMapHeight(jsonObject.getAsJsonPrimitive("height").getAsInt());
	}
	
	private void loadMaps() {
		
		Gson gson = new Gson();
		
		JsonArray jsonMap = jsonObject.getAsJsonArray("layers");
		
		List<TiledMapLayer> tiledMapLayers = new ArrayList<>();
		
		for (JsonElement jsonElement : jsonMap) {
			JsonObject mapLayer = jsonElement.getAsJsonObject();
			tiledMapLayers.add(new TiledMapLayer(
					mapLayer.get("id").getAsInt(),
					gson.fromJson(mapLayer.get("data"), int[].class),
					mapLayer.get("visible").getAsBoolean(),
					mapLayer.get("opacity").getAsFloat(),
					new Vector2f(mapLayer.get("width").getAsInt(), mapLayer.get("height").getAsInt()),
					new Vector2f(
							mapLayer.get("offsetx") != null ? mapLayer.get("offsetx").getAsFloat() : 0,
							mapLayer.get("offsety") != null ? mapLayer.get("offsety").getAsFloat() : 0
					)
			));
		}
		maps = tiledMapLayers.toArray(new TiledMapLayer[0]);
		System.gc();
	}
	
	private void loadSpritesheets() {
		JsonArray tsxLayers = jsonObject.get("tilesets").getAsJsonArray();
		
		for (JsonElement jsonElement : tsxLayers) {
			JsonObject tsxLayer = jsonElement.getAsJsonObject();
			Path filepath = resourcePath("tilemap", tsxLayer.get("source").getAsString());
			TileSet tileSet = TileSet.create(filepath);
			
			AssetPool.addSpriteSheet(filepath, tileSet);
			
			spriteSheets.put(
					tsxLayer.get("firstgid").getAsInt(),
					AssetPool.getSpriteSheet(filepath)
			);
		}
	}
	
	private Sprite getSprite(int loc) {
		SpriteSheet spriteSheet = null;
		int[] firstIDs = spriteSheets.keySet().stream().mapToInt(i -> i).toArray();
		int firstID = 0;
		for (int i : firstIDs) {
			if (loc >= i) {
				spriteSheet = spriteSheets.get(i);
				firstID = i;
			}
		}
		return Objects.requireNonNull(spriteSheet).getSprite(loc - firstID);
	}
	
	private void iterateXnY(int width, int height, BiConsumer<Integer, Integer> xyLooper) {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				xyLooper.accept(x, y);
			}
	}
	
	private void createTiles(int x, int y, TiledMapLayer tml, int layer) {
		int texPos = tml.getData()[((tml.getWidth()) * ((tml.getHeight() - 1) - y)) + x];
		if (texPos <= 0) return;
		//region Create GameObject.
		tileArray[layer][x][y] =
				new Tile(
						x, y, layer,
						tileScale,
						tml.getOffset(),
						getSprite(texPos),
						new Color(tml.getOpacity(), true)
				);
		
		
		//endregion
		tiles.add(tileArray[layer][x][y]);
	}
}
