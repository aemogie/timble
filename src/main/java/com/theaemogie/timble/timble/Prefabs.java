package com.theaemogie.timble.timble;

import com.theaemogie.timble.renderer.Sprite;
import com.theaemogie.timble.components.SpriteRenderer;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject tile = new GameObject("Tile Object Gen", new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        tile.addComponent(renderer);

        return tile;
    }
}
