package com.theaemogie.timble.timble;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Transform {
    public Vector3f position;
    public Vector2f scale;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }
    
    public Transform(float posX, float posY,float scaleX, float scaleY) {
        init(new Vector2f(posX, posY), new Vector2f(scaleX, scaleY));
    }
    
    public Transform(Vector3f position, Vector2f scale) {
        init(position, scale);
    }
    
    public void init(Vector3f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }
    
    public void init(Vector2f position, Vector2f scale) {
        this.position = new Vector3f(position, 0);
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector3f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform to) {
        to.position.sub(this.position);
        to.scale.sub(this.scale);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Transform)) return false;

        Transform t = (Transform) obj;

        return t.position == this.position && t.scale == this.scale;
    }
}
