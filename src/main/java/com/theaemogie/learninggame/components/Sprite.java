package com.theaemogie.learninggame.components;

import com.theaemogie.learninggame.renderer.Texture;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Sprite {
    private Texture texture;
    private Vector2f[] textureCoords;

    public Sprite(Texture texture) {
        this.texture = texture;
        Vector2f[] textureCoords = {
                new Vector2f(1,1),
                new Vector2f(1,0),
                new Vector2f(0,0),
                new Vector2f(0,1)
        };
        this.textureCoords = textureCoords;
    }

    public Sprite(Texture texture, Vector2f[] textureCoords) {
        this.texture = texture;
        this.textureCoords = textureCoords;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTextureCoords() {
        return this.textureCoords;
    }
}
