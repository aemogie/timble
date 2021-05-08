package com.theaemogie.timble.tiles;

import com.theaemogie.timble.renderer.Texture;
import com.theaemogie.timble.util.AssetPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.theaemogie.timble.util.JsonParser.getIntegerValue;
import static com.theaemogie.timble.util.JsonParser.getStringValue_KC;

public class TileSet extends SpriteSheet {
	
	public TileSet(Texture texture, int spriteWidth, int spriteHeight, int numOfSprites, int spacing) {
		super(texture, spriteWidth, spriteHeight, numOfSprites, spacing);
	}
	
	public static TileSet create(Path filepath) {
		String source;
		Texture texture;
		int spriteWidth, spriteHeight;
		int numOfSprites, spacing;
		
		try {
			source = new String(Files.readAllBytes(filepath));
		} catch (IOException e) {
			System.err.println("Error while reading TileSet : " + "\"" + filepath + "\".");
			System.exit(-1);
			return null;
		}
		
		texture = AssetPool.getTexture(Paths.get(filepath.toString(), "..", getStringValue_KC(source, "image", true)));
		spriteWidth = getIntegerValue(source, "tilewidth", true);
		spriteHeight = getIntegerValue(source, "tilewidth", true);
		numOfSprites = getIntegerValue(source, "tilecount", true);
		spacing = getIntegerValue(source, "spacing", true);
		
		return new TileSet(texture, spriteWidth, spriteHeight, numOfSprites, spacing);
	}
}
