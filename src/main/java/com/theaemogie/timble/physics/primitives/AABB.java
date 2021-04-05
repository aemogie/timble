package com.theaemogie.timble.physics.primitives;

import com.theaemogie.timble.physics.rigidbody.RigidBody;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
//Axis Aligned Bounding Box
public class AABB {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private RigidBody rigidBody = null;

    public AABB() {
        this.halfSize = new Vector2f(size).div(2);
    }

    public AABB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).div(2);
    }

    public Vector2f getMin() {
        return new Vector2f(rigidBody.getPosition()).sub(halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(rigidBody.getPosition()).add(halfSize);
    }
}
