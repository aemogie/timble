package io.github.aemogie.timble.util;

import io.github.aemogie.timble.renderer.SpriteSheet;
import io.github.aemogie.timble.renderer.Shader;
import io.github.aemogie.timble.renderer.Texture;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(Path resourcePath) {
        File file = resourcePath.toFile();
        if (!AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            Shader shader = new Shader(resourcePath);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
        }
        return AssetPool.shaders.get(file.getAbsolutePath());
    }

    public static Texture getTexture(Path resourcePath) {
        File file = resourcePath.toFile();
        if (!AssetPool.textures.containsKey(file.getAbsolutePath())) {
            Texture texture = new Texture();
            texture.init(resourcePath);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
        }
        return AssetPool.textures.get(file.getAbsolutePath());
    }

    public static void addSpriteSheet(Path resourcePath, SpriteSheet spriteSheet) {
        File file = resourcePath.toFile();
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(Path resourcePath) {
        File file = resourcePath.toFile();
        assert AssetPool.spriteSheets.containsKey(file.getAbsolutePath()) : "Error: Tried to access SpriteSheet '" + resourcePath.toAbsolutePath() + "' and it has not been added to asset pool.";
        return AssetPool.spriteSheets.get(file.getAbsolutePath());
    }
}