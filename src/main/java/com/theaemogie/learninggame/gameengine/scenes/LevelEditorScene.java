package com.theaemogie.learninggame.gameengine.scenes;

import com.theaemogie.learninggame.components.SpriteRenderer;
import com.theaemogie.learninggame.components.SpriteSheet;
import com.theaemogie.learninggame.gameengine.Camera;
import com.theaemogie.learninggame.gameengine.GameObject;
import com.theaemogie.learninggame.gameengine.Transform;
import com.theaemogie.learninggame.util.AssetPool;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {

    private final String spriteSheet1Path = "src/main/resources/assets/textures/sprites/sprite1_spritesheet.png";
    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        SpriteSheet sprites = AssetPool.getSpriteSheet(spriteSheet1Path);

        GameObject sprite1 = new GameObject("Sprite 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        sprite1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(sprite1);

        GameObject sprite2 = new GameObject("Sprite 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        sprite2.addComponent(new SpriteRenderer(sprites.getSprite(1)));
        this.addGameObjectToScene(sprite2);
    }

    private void loadResources() {
        AssetPool.getShader("src/main/resources/assets/shaders/default.glsl");
        AssetPool.addSpriteSheet(spriteSheet1Path, new SpriteSheet(AssetPool.getTexture(spriteSheet1Path), 16, 16, 4, 0));
    }

    @Override
    public void update(double deltaTime) {
        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}