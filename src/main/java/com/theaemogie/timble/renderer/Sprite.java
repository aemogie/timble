package com.theaemogie.timble.renderer;

import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Sprite {
    private Texture texture = null;
    private Vector2f[] textureCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    private float width, height;

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTextureCoords() {
        return this.textureCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTexId() {
        return texture == null ? -1 : texture.getTextureID();
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTextureCoords(Vector2f[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder("Class: " + this.getClass().getCanonicalName() + "\n");
        outputString.append("Texture: \n" + this.texture.toString().replaceAll("(?m)^", "\t") + "\n");
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    outputString.append("Bottom Right:");
                    break;
                case 1:
                    outputString.append("Top Right:\t");
                    break;
                case 2:
                    outputString.append("Top Left:\t");
                    break;
                case 3:
                    outputString.append("Bottom Left:");
                    break;
            }
            outputString.append("\tx: " + textureCoords[i].x);
            outputString.append("\ty: " + textureCoords[i].y);
            outputString.append("\n");
        }
        return outputString.toString();
    }
}
