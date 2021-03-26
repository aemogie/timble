package com.theaemogie.learninggame.util;

import com.theaemogie.learninggame.components.SpriteSheet;
import com.theaemogie.learninggame.renderer.Shader;
import com.theaemogie.learninggame.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourcePath) {
        File file = new File(resourcePath);
        if (!AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            Shader shader = new Shader(resourcePath);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
        }
        return AssetPool.shaders.get(file.getAbsolutePath());
    }

    public static Texture getTexture(String resourcePath) {
        File file = new File(resourcePath);
        if (!AssetPool.textures.containsKey(file.getAbsolutePath())) {
            Texture texture = new Texture(resourcePath);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
        }
        return AssetPool.textures.get(file.getAbsolutePath());
    }

    public static void addSpriteSheet(String resourcePath, SpriteSheet spriteSheet) {
        File file = new File(resourcePath);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(String resourcePath) {
        File file = new File(resourcePath);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access SpriteSheet '" + resourcePath + "' and it has not been added to asset pool.";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}