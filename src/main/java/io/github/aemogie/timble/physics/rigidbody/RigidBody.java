package io.github.aemogie.timble.physics.rigidbody;

import io.github.aemogie.timble.components.Component;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class RigidBody extends Component {
    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    //In degrees.
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
