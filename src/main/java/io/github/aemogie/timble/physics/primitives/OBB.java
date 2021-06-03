package io.github.aemogie.timble.physics.primitives;

import io.github.aemogie.timble.physics.rigidbody.RigidBody;
import io.github.aemogie.timble.util.TimbleMath;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class OBB {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private RigidBody rigidBody = null;

    public OBB() {
        this.halfSize = new Vector2f(size).div(2);
    }

    public OBB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).div(2);
    }

    public Vector2f getMin() {
        return new Vector2f(rigidBody.getPosition()).sub(halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(rigidBody.getPosition()).add(halfSize);
    }

    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y)
        };

        if (rigidBody.getRotation() != 0.0f) {
            for (Vector2f vert : vertices) {
                //TODO: Implement this.
                //Rotates point(Vector2f) about center(Vector2f) by rotation(float in degrees)
                TimbleMath.rotate(vert, this.rigidBody.getPosition(), this.rigidBody.getRotation());
            }
        }
        return vertices;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }
}
