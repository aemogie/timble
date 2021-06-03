package io.github.aemogie.timble.timble;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Camera {
	public Vector2f position;
	private transient Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
	private float zoom = 1.0f;
	private transient Vector2f projectionSize;
	
	public void init(Vector2f position) {
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjectionMatrix = new Matrix4f();
		this.inverseViewMatrix = new Matrix4f();
		this.projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
		adjustProjection();
	}
	
	public void adjustProjection() {
		projectionMatrix.identity();
		projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100f);
//		projectionMatrix.setPerspective(90, 16f / 9, 0.5f, Float.POSITIVE_INFINITY);
		projectionMatrix.invert(inverseProjectionMatrix);
	}
	
	public Matrix4f getViewMatrix() {
		Vector2f newPosition = new Vector2f(position);//.add(new Vector2f(32 * (1/zoom)));
		Vector3f cameraFront = new Vector3f(0, 1, -1);
		Vector3f cameraUp = new Vector3f(0, 1, 0);
		Vector3f center = new Vector3f(newPosition, -1);
		center.add(cameraFront);
		this.viewMatrix.identity();
		this.viewMatrix = this.viewMatrix.lookAt(
				new Vector3f(center.x, center.y, 20),//.mul(1,1,zoom),
				center,
				cameraUp
		);
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
	
	public void smoothFollow(Window window, Transform follow, float smoothing) {
		this.smoothFollow(window.getWidth(), window.getHeight(), window.getCurrentScene().getMapWidth() * window.getCurrentScene().getTileWidth(), window.getCurrentScene().getMapHeight() * window.getCurrentScene().getTileHeight(), follow, smoothing);
	}
	
	public void smoothFollow(int windowWidth, int windowHeight, int endWidth, int endHeight, Transform follow, float smoothing) {
		Vector2f desiresPos = new Vector2f(follow.position.x, follow.position.y).add(new Vector2f(follow.scale).div(2)).sub(new Vector2f(windowWidth / 2f, windowHeight / 2f).mul(zoom));
		int endX = (int) ((endWidth - windowWidth) / zoom);
		int endY = (int) ((endHeight - windowHeight) / zoom);
		position.lerp(new Vector2f(
				desiresPos.x >= 0 ? desiresPos.x >= endX ? endX : desiresPos.x : 0,
				desiresPos.y >= 0 ? desiresPos.y >= endY ? endY : desiresPos.y : 0
		), smoothing);
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
		adjustProjection();
	}
	
	public void addZoom(float addZoom) {
		this.zoom += addZoom;
	}
	
	public void transform(float x, float y) {
		this.position.add(x, y);
	}
	
	public void transform(double x, double y) {
		this.transform((float) x, (float) y);
	}
	
	public Vector2f getProjectionSize() {
		return projectionSize;
	}
}
