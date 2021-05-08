package com.theaemogie.timble.tiles;

import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.renderer.Color;
import com.theaemogie.timble.renderer.Sprite;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.util.AssetPool;
import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

import static com.theaemogie.timble.util.JsonParser.*;
import static com.theaemogie.timble.util.StringUtils.resourcePath;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TiledMap {
	private final HashMap<Integer, SpriteSheet> spriteSheets = new HashMap<>();
	private final Tile[][][] tileArray;
	private final int tileWidth, tileHeight;
	private TiledMapLayer[] maps;
	private String source = "";
	private final List<Tile> tiles = new ArrayList<>();
	
	public TiledMap(Path filepath, int tileWidth, int tileHeight, Scene scene) {
		load(filepath, scene);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
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
		loadSpritesheets();
		loadMaps();
		
		int width = 0, height = 0;
		
		for (TiledMapLayer tml : maps) {
			if (tml.getWidth() > width) width = tml.getWidth();
			if (tml.getHeight() > height) height = tml.getHeight();
		}
		
		scene.setMapWidth(width);
		scene.setMapHeight(height);
	}
	
	private void loadMaps() {
		
		String[] mapLayers = getArrayValue(source, "layers", "nextlayerid", "}\\s*,\\s*\\{|\\s*}],");
		
		List<TiledMapLayer> tiledMapLayers = new ArrayList<>();
		
		for (String mapLayer : mapLayers) {
			List<String> dataStringList = Arrays.asList(getArrayValue(mapLayer, "data"));
			List<Integer> dataList = new ArrayList<>();
			dataStringList.forEach(dataString -> dataList.add(Integer.parseInt(dataString)));
			int[] data = dataList.stream().mapToInt(integer -> integer).toArray();
			tiledMapLayers.add(new TiledMapLayer(
					getIntegerValue(mapLayer, "id", true),
					data,
					getBooleanValue(mapLayer, "visible", true),
					getFloatValue(mapLayer, "opacity", true),
					new Vector2f(
							getIntegerValue(mapLayer, "width", true),
							getIntegerValue(mapLayer, "height", true)
					),
					new Vector2f(
							getIntegerValue(mapLayer, "offsetx", false),
							getIntegerValue(mapLayer, "offsety", false)
					)
			));
		}
		System.gc();
		maps = tiledMapLayers.toArray(new TiledMapLayer[0]);
	}
	
	private void loadSpritesheets() {
		String[] tsxLayers = getArrayValue(source, "tilesets", "tilewidth", "}\\s*,\\s*\\{|\\s*}],");
		
		for (String tsxLayer : tsxLayers) {
			Path filepath = resourcePath("tilemap/" + getStringValue_KC(tsxLayer, "source", true));
			TileSet tileSet = TileSet.create(filepath);
			
			AssetPool.addSpriteSheet(filepath, tileSet);
			
			spriteSheets.put(
					getIntegerValue(tsxLayer, "firstgid", true),
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
		tileArray[layer][x][y] = new Tile(
				x, y,
				new Transform(
						new Vector2f(
								(x * tileWidth) + tml.getOffsetX(),
								(y * tileWidth) + tml.getOffsetY()
						),
						new Vector2f(tileWidth, tileHeight)
				)).addComponent(new SpriteRenderer().setSprite(getSprite(texPos)).setColor(new Color(1, 1, 1, tml.getOpacity(), true)));
				

		//endregion
		tiles.add(tileArray[layer][x][y]);
	}
}
