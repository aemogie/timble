package com.theaemogie.learninggame.gameengine.scenes;

import com.theaemogie.learninggame.components.SpriteRenderer;
import com.theaemogie.learninggame.gameengine.Camera;
import com.theaemogie.learninggame.gameengine.GameObject;
import com.theaemogie.learninggame.gameengine.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float) (600 - xOffset * 2);
        float totalHeight = (float) (300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;
        float padding = 3;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX) + (padding * x);
                float yPos = yOffset + (y * sizeY) + (padding * y);

                GameObject go = new GameObject(
                        "Object " + x + "" + y,
                        new Transform(
                                new Vector2f(xPos, yPos), //Position
                                new Vector2f(sizeX, sizeY) //Scale
                        )
                );

                go.addComponent(
                        new SpriteRenderer(
                                new Vector4f(
                                        xPos / totalWidth,
                                        yPos / totalHeight,
                                        1,
                                        1
                                )
                        )
                );
                this.addGameObjectToScene(go);
            }
        }

    }

    @Override
    public void update(double deltaTime) {
        for (GameObject go : gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}