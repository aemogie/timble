package com.theaemogie.timble.tiles;

import com.theaemogie.timble.renderer.Sprite;
import com.theaemogie.timble.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class SpriteSheet {
    
    private List<Sprite> sprites;
    
    protected SpriteSheet() {}
    
    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numOfSprites, int spacing) {
        
        this.sprites = new ArrayList<>();
    
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        
        for (int i = 0; i < numOfSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] textureCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(texture);
            sprite.setTextureCoords(textureCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);


            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        if (index <= -1) {
            return new Sprite();
        } else {
            return this.sprites.get(index);
        }
    }

    public int size() {
        return this.sprites.size();
    }
    
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder(this.getClass().getCanonicalName() + ":\n");
        outputString.append("Size: ").append(sprites.size()).append("\n");
        for (Sprite sprite : sprites) {
            outputString.append("\nAdded sprite ");
            outputString.append("at index " + this.sprites.indexOf(sprite) + ";");
            outputString.append("\n" + sprite.toString().replaceAll("(?m)^", "\t"));
        }
        return outputString.toString();
    }
}
