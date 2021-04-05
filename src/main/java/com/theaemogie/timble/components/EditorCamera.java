package com.theaemogie.timble.components;

import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.timble.Camera;
import com.theaemogie.timble.timble.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class EditorCamera extends Component {
	
	private float dragDebounce = 0.032f;
	private transient int button = GLFW_MOUSE_BUTTON_RIGHT;
	private transient Camera levelEditorCamera;
	private transient Vector2f clickOrigin;
	private float dragSensitivity = 10.0f;
	private float scrollSensitivity = 0.1f;
	
	public EditorCamera() {
	}
	
	public EditorCamera init(Camera levelEditorCamera) {
		this.levelEditorCamera = levelEditorCamera;
		this.clickOrigin = new Vector2f();
		return this;
	}
	
	private boolean pressed() {
		return MouseListener.isButtonPressed(button);
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		if (pressed() && dragDebounce > 0) {
			this.clickOrigin = new Vector2f(new Vector2f(MouseListener.getOrthoX(window), MouseListener.getOrthoY(window)));
			dragDebounce -= deltaTime;
			return;
		} else if (pressed()) {
			Vector2f mousePosition = new Vector2f(MouseListener.getOrthoX(window), MouseListener.getOrthoY(window));
			Vector2f delta = new Vector2f(mousePosition).sub(clickOrigin);
			levelEditorCamera.position.sub(delta.mul((float) deltaTime).mul(dragSensitivity));
			this.clickOrigin.lerp(mousePosition, (float) deltaTime);
		}
		
		if (dragDebounce <= 0 && !pressed()) {
			dragDebounce = 0.032f;
		}
		
		if (MouseListener.getScrollY() != 0.0f) {
			float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / levelEditorCamera.getZoom());
			addValue *= -Math.signum(MouseListener.getScrollY());
			levelEditorCamera.addZoom(addValue);
			levelEditorCamera.adjustProjection();
		}
	}
}
