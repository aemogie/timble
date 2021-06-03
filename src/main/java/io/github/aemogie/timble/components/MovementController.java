package io.github.aemogie.timble.components;

import io.github.aemogie.timble.scenes.Scene;
import io.github.aemogie.timble.timble.Window;
import io.github.aemogie.timble.eventhandlers.KeyListener;

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
		
		if (KeyListener.isControlPressed()) movementSpeed *= 2f;
		if (KeyListener.isShiftPressed()) movementSpeed /= 2f;
		
		if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
			if (gameObject.transform.position.y + movementSpeed < (scene.getMapHeight() * scene.getTileHeight()) - scene.getTileHeight())
				gameObject.transform.position.y += movementSpeed;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
			if (gameObject.transform.position.x - movementSpeed >= 0) gameObject.transform.position.x -= movementSpeed;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
			if (gameObject.transform.position.y - movementSpeed >= 0) gameObject.transform.position.y -= movementSpeed;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
			if (gameObject.transform.position.x + movementSpeed < (scene.getMapWidth() * scene.getTileWidth()) - scene.getTileWidth())
				gameObject.transform.position.x += movementSpeed;
		}
		
	}
}
