package com.theaemogie.timble.physics.primitives;

import com.theaemogie.timble.renderer.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Line {
    private Vector2f start;
    private Vector2f end;
    private Color color;
    private int lifetime;

    public Line(Vector2f start, Vector2f end) {
        this.start = start;
        this.end = end;
    }

    public Line(Vector2f start, Vector2f end, Color color, int lifetime) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.lifetime = lifetime;
    }

    public int beginFrame() {
        lifetime--;
        return this.lifetime;
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }
}
