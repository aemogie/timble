package com.theaemogie.timble.tiles;

import com.theaemogie.timble.components.Component;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;

public class Tile extends GameObject {
	public Tile(int x, int y, Transform transform) {
		super("Tile " + x + " : " + y, transform);
	}
	
	@Override
	public Tile addComponent(Component c) {
		return (Tile) super.addComponent(c);
	}
}