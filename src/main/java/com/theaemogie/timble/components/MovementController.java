package com.theaemogie.timble.components;

import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.Window;

import static com.theaemogie.timble.eventhandlers.KeyListener.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class MovementController extends Component {
	
	private transient Scene scene;
	
	public MovementController setScene(Scene scene) {
		this.scene = scene;
		return this;
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		float movementSpeed = (float) (200 * deltaTime);
		
		if (isControlPressed()) movementSpeed *= 2f;
		if (isShiftPressed()) movementSpeed /= 2f;
		
		if (isKeyPressed(GLFW_KEY_W)) {
			if (gameObject.transform.position.y + movementSpeed < (scene.getMapHeight() * scene.getTileHeight()) - scene.getTileHeight())
				gameObject.transform.position.y += movementSpeed;
		}
		if (isKeyPressed(GLFW_KEY_A)) {
			if (gameObject.transform.position.x - movementSpeed >= 0) gameObject.transform.position.x -= movementSpeed;
		}
		if (isKeyPressed(GLFW_KEY_S)) {
			if (gameObject.transform.position.y - movementSpeed >= 0) gameObject.transform.position.y -= movementSpeed;
		}
		if (isKeyPressed(GLFW_KEY_D)) {
			if (gameObject.transform.position.x + movementSpeed < (scene.getMapWidth() * scene.getTileWidth()) - scene.getTileWidth())
				gameObject.transform.position.x += movementSpeed;
		}
		
	}
}
