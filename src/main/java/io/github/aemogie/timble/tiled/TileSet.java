package io.github.aemogie.timble.tiled;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import io.github.aemogie.timble.renderer.SpriteSheet;
import io.github.aemogie.timble.renderer.Texture;
import io.github.aemogie.timble.util.AssetPool;
import io.github.aemogie.timble.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TileSet extends SpriteSheet {
	
	public TileSet(Texture texture, int spriteWidth, int spriteHeight, int spacing) {
		super(texture, spriteWidth, spriteHeight, spacing);
	}
	
	public static TileSet create(Path filepath) {
		String source = "";
		Texture texture;
		int spriteWidth, spriteHeight;
		int spacing;
		
		try {
			source = new String(Files.readAllBytes(filepath));
		} catch (IOException ignored) {
			Logger.logFatal("Error while reading TileSet : " + "\"" + filepath + "\".");
		}
		
		JsonObject jsonObject = new JsonStreamParser(source).next().getAsJsonObject();
		texture = AssetPool.getTexture(Paths.get(filepath.toString(), "..", jsonObject.getAsJsonPrimitive("image").getAsString()));
		spriteWidth = jsonObject.getAsJsonPrimitive("tilewidth").getAsInt();
		spriteHeight = jsonObject.getAsJsonPrimitive("tileheight").getAsInt();
		spacing = jsonObject.getAsJsonPrimitive("spacing").getAsInt();
		
		return new TileSet(texture, spriteWidth, spriteHeight, spacing);
	}
}
