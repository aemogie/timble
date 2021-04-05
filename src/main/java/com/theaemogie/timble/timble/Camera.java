package com.theaemogie.timble.timble;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Camera {
    private transient Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
    private float zoom = 1.0f;

    public Vector2f position;
    private transient Vector2f projectionSize;

    public void init(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        this.projectionSize = new Vector2f(32.0f * 40.0f,32.0f * 21.0f);
        adjustProjection();
    }
    

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

        this.viewMatrix.identity();
        this.viewMatrix = this.viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);

        this.viewMatrix.invert(inverseViewMatrix);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return this.inverseProjectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }
    
    public float getZoom() {
        return zoom;
    }
    
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    public void addZoom(float addZoom) {
        this.zoom += addZoom;
    }
    
    public void transform(float x, float y) {
        this.position.add(x, y);
    }
    
    public void transform(double x, double y) {
        this.transform((float) x,(float) y);
    }
    
    public Vector2f getProjectionSize() {
        return projectionSize;
    }
}
