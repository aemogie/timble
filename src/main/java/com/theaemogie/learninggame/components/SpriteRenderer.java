package com.theaemogie.learninggame.components;

import org.joml.Vector4f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class SpriteRenderer extends Component {

    private Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(double deltaTime) {

    }
}
