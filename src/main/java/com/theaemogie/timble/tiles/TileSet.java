package com.theaemogie.timble.tiles;

import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.renderer.Color;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TileSet {
	private static int prevSize = 0;
	private GameObject[][] gameObjects;
	private TiledMap tiledMap;
	
	public TileSet(String mapFilePath, int tileWidth, int tileHeight, Scene scene) {
		
		tiledMap = new TiledMap(mapFilePath);
		
		for (TiledMapLayer tml : tiledMap.getMaps()) {
			
			gameObjects = new GameObject[tml.getScaleX()][tml.getScaleY()];
			
			for (int y = 0; y < tml.getScaleY(); y++) for (int x = 0; x < tml.getScaleY(); x++) {
				int texPos = 1;
				texPos = getAt(tml, prevSize, x, y);
				if (texPos == 0) continue;
				//region Create GameObject.
				gameObjects[x][y] =
						new GameObject(
								"Tile " + x + ":" + y,
								new Transform(
										new Vector2f(
												(x * tileWidth) + tml.getTranslationX(),
												(y * tileWidth) + tml.getTranslationY()
										),
										new Vector2f(
												tileWidth,
												tileHeight
										)
								))
								.addComponent(new SpriteRenderer().setSprite(
										tml.getSpriteSheet().getSprite(texPos)
								).setColor(
										new Color(1, 1, 1, tml.getOpacity(), true)
								));
				//endregion
				scene.addTilesToScene(gameObjects[x][y]);
			}
			prevSize += tml.getSpriteSheet().size();
		}
	}
	
	public int getAt(TiledMapLayer map, int totalSprites, int x, int y) {
		int index = (map.getScaleX() * ((map.getScaleY() - 1) - y)) + x; // width * height - 1 - current y
		int output = map.getData()[index] - 1 - totalSprites;
		output = Math.max(output, 0);
		return output;
//		return 1;
	}
}
