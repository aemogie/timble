package com.theaemogie.timble.tiles;

import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TileSet {
	private GameObject[][] gameObjects;
	private ArrayList<ArrayList<Integer>> map;
	private static int prevSize = 0;
	
	public TileSet(String mapFilePath, SpriteSheet spriteSheet, int dimWidth, int dimHeight, int tileWidth, int tileHeight, Scene scene) {
		
		map = TMXParser.parseJson(mapFilePath);
		gameObjects = new GameObject[dimWidth][dimHeight];
		
		for (int y = 0; y < dimHeight; y++) { for (int x = 0; x < dimWidth; x++) {
			int texPos = getAt(
					map,
					prevSize,
					spriteSheet.getLayerID(),
					x,
					y,
					dimWidth,
					dimHeight
			);
			if (texPos == 0) continue;
			//region Create GameObject.
			gameObjects[x][y] = new GameObject(
					"Tile " + x + ":" + y,
					new Transform(
							new Vector2f(x * tileWidth, y * tileWidth),
							new Vector2f(tileWidth, tileHeight)
					)
			)
					.addComponent(new SpriteRenderer().setSprite(
							spriteSheet.getSprite(texPos)
					));
			//endregion
			scene.addTilesToScene(gameObjects[x][y]);
		}}
		prevSize += spriteSheet.size();
	}
	
	public int getAt(ArrayList<ArrayList<Integer>> map, int totalSprites, int layerID, int x, int y, int dimWidth, int dimHeight) {
//		System.out.println(totalSprites);
		int index = ((dimWidth * ((dimHeight - 1) - y)) + x);
		if (index < 0) {
			index = 0;
		}
		int output = map
				.get(layerID)
				.get(index) - 1  - (totalSprites);
		return Math.max(output, 0);
	}
}
