package io.github.aemogie.timble.gl.utils;

import io.github.aemogie.timble.graphics.window.Window;
import io.github.aemogie.timble.utils.events.Event.Listener;

import static org.lwjgl.opengl.GL11.*;

public class Debug {
	private Debug() {}
	public static final Listener<Window.FrameLoopEvent> TRIANGLE = new Listener<>(false) {
		@Override
		protected boolean onFire(Window.FrameLoopEvent event) {
			glBegin(GL_TRIANGLES);
			glVertex2f(-0.5f, -0.5f);
			glVertex2f(0.0f, 0.5f);
			glVertex2f(0.5f, -0.5f);
			glEnd();
			return true;
		}
	};
}