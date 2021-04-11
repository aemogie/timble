package com.theaemogie.timble.components;

import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.timble.Window;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class PlayerMovement extends Component{
	
	private transient Scene scene;
	
	public PlayerMovement setScene(Scene scene) {
		this.scene = scene;
		return this;
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
			gameObject.transform.position.y += 2.5f;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
			gameObject.transform.position.y -= 2.5f;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
			gameObject.transform.position.x -= 2.5f;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
			gameObject.transform.position.x += 2.5f;
		}
		scene.getCamera().smoothFollow(window, gameObject.transform);
	}
}
