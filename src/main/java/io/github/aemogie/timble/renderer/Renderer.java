package io.github.aemogie.timble.renderer;

import io.github.aemogie.timble.components.SpriteRenderer;
import io.github.aemogie.timble.scenes.Scene;
import io.github.aemogie.timble.timble.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Renderer {
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spriteRenderer = go.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                Texture texture = spriteRenderer.getTexture();
                
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spriteRenderer);
        }
    }
    
    public void remove(GameObject gameObject) {
        if (gameObject == null) return;
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) {
            for (RenderBatch batch : batches) {
                batch.removeSprite(spriteRenderer);
            }
        }
    }
    
    public static void bindShader(Shader shader) {
        currentShader = shader;
    }
    
    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render(Scene scene) {
        currentShader.use();
        for (RenderBatch batch : batches) {
            batch.render(scene);
        }
    }
}