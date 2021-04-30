package com.theaemogie.timble.tiles;

import com.theaemogie.timble.renderer.Texture;
import com.theaemogie.timble.util.AssetPool;
import org.joml.Vector2f;

import java.util.Arrays;

import static com.theaemogie.timble.util.StringUtils.resourcePath;

public class TiledMapLayer {
	private final int id;
	private final int[] data;
	private final SpriteSheet spriteSheet;
	//TODO:	private int type;
	private final boolean visible;
	private final float opacity;
	private final int firstTileID;
	private final int scaleX; //Towards positive X (Right)
	private final int scaleY; //Towards positive Y (Up)
	private final int translationX;   //Position of left bottom corner
	private final int translationY;   //   ""    ""  ""    ""     ""
	
	public TiledMapLayer(int id, int[] data, String spritesheetPath, boolean visible, float opacity, Vector2f tileIDs, Vector2f scale, Vector2f translation, Vector2f tileSize) {
		this.id = id;
		this.data = visible ? data : new int[data.length];
		AssetPool.addSpriteSheet(
				resourcePath("tilemap/" + spritesheetPath),
				new SpriteSheet(
						AssetPool.getTexture(resourcePath("tilemap/" + spritesheetPath)),
						(int) tileSize.x,
						(int) tileSize.y,
						(int) tileIDs.y - 1,
						0
				)
		);
		this.spriteSheet = AssetPool.getSpriteSheet(resourcePath("tilemap/" + spritesheetPath));
		this.visible = visible;
		this.opacity = opacity;
		this.firstTileID = (int) tileIDs.x;
		this.scaleX = (int) scale.x;
		this.scaleY = (int) scale.y;
		this.translationX = (int) translation.x;
		this.translationY = (int) translation.y;
	}
	
	public int getId() {
		return id;
	}
	
	public int[] getData() {
		return data;
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public int getScaleX() {
		return scaleX;
	}
	
	public int getScaleY() {
		return scaleY;
	}
	
	public int getTranslationX() {
		return translationX;
	}
	
	public int getTranslationY() {
		return translationY;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("Layer ").append(getId()).append("\n");
		out.append("\t").append("Visible: ").append(visible).append("\n");
		out.append("\t").append("Scale: ").append("X = ").append(getScaleX()).append(" - ").append("Y = ").append(getScaleY()).append("\n");
		if (visible) out.append("\t").append("Data: ").append(Arrays.toString(data));
		return out.toString();
	}
	
	public int getFirstTileID() {
		return firstTileID;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
}
