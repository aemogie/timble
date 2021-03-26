package com.theaemogie.learninggame.components;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found Font Renderer!");
        }
    }

    @Override
    public void update(double deltaTime) {

    }
}
