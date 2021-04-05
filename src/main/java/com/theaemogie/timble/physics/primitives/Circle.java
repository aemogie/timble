package com.theaemogie.timble.physics.primitives;

import com.theaemogie.timble.physics.rigidbody.RigidBody;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Circle {
    private float radius;
    private RigidBody rigidBody = null;

    public float getRadius() {
        return radius;
    }

    public Vector2f getCenter() {
        return rigidBody.getPosition();
    }
}
