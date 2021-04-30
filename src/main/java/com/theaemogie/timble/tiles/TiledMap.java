package com.theaemogie.timble.tiles;

import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.theaemogie.timble.tiles.JsonParser.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TiledMap {
	
	private String source = "";
	
	public TiledMap(String filepath) {
		try {
			source = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TiledMapLayer[] getMaps() {
		String[] mapLayers = getArrayValue(source, "layers", "nextlayerid", "}\\s*,\\s*\\{|\\s*}]");
		String[] tsxLayers = getArrayValue(source, "tilesets", "tilewidth", "}\\s*,\\s*\\{|\\s*}]");
		
		List<TiledMapLayer> tiledMapLayers = new ArrayList<>();
		
		for (int i = 0; i < mapLayers.length; i++) {
			String mapLayer = mapLayers[i];
			String tsxLayer = tsxLayers[i];
			
			List<String> dataStringList = Arrays.asList(getArrayValue(mapLayer, "data"));
			List<Integer> dataList = new ArrayList<>();
			dataStringList.forEach(dataString -> dataList.add(Integer.parseInt(dataString)));
			int[] data = dataList.stream().mapToInt(integer -> integer).toArray();
			
			tiledMapLayers.add(new TiledMapLayer(
					getIntegerValue(mapLayer, "id"),
					data,
					getStringValue(tsxLayer, "source").replace(".tsx", ".png"),
					getBooleanValue(mapLayer, "visible"),
					getFloatValue(mapLayer, "opacity"),
					new Vector2f(),
					new Vector2f(
							getIntegerValue(mapLayer, "width"),
							getIntegerValue(mapLayer, "height")
					),
					new Vector2f(
							getIntegerValue(mapLayer, "x"),
							getIntegerValue(mapLayer, "y")
					),
					new Vector2f(
							getIntegerValue(source, "tilewidth"),
							getIntegerValue(source, "tileheight")
					)
			));
		}
//		System.gc();
		return tiledMapLayers.toArray(new TiledMapLayer[0]);
	}
	
}
