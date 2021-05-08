package com.theaemogie.timble.tiles;

import org.joml.Vector2f;

import java.util.Arrays;

public class TiledMapLayer {
	private final int id;
	private final int[] data;
//	private final SpriteSheet spriteSheet;
	//TODO:	private int type;
	private final boolean visible;
	private final float opacity;
	private final int width; //Towards positive X (Right)
	private final int height; //Towards positive Y (Up)
	private final int offsetX;   //Position of left bottom corner
	private final int offsetY;   //   ""    ""  ""    ""     ""
	
	public TiledMapLayer(int id, int[] data, boolean visible, float opacity, Vector2f scale, Vector2f offset) {
		this.id = id;
		this.data = visible ? data : new int[data.length];
		this.visible = visible;
		this.opacity = opacity;
		this.width = (int) scale.x;
		this.height = (int) scale.y;
		this.offsetX = (int) offset.x;
		this.offsetY = (int) offset.y;
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("Layer ").append(getId()).append("\n");
		out.append("\t").append("Visible: ").append(visible).append("\n");
		out.append("\t").append("Scale: ").append("X = ").append(getWidth()).append(" - ").append("Y = ").append(getHeight()).append("\n");
		if (visible) out.append("\t").append("Data: ").append(Arrays.toString(data));
		return out.toString();
	}
}
