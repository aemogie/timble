package com.theaemogie.timble.eventhandlers;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class KeyListener {
    private static KeyListener instance;
    private boolean[] keyPressed = new boolean[350];
    private int charInput;

    private KeyListener() {
    }

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (!(key < 0 || key >= get().keyPressed.length)) {
            if (action == GLFW_PRESS) {
                get().keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                get().keyPressed[key] = false;
            }
        }
    }

    public static boolean isKeyPressed(int key) {
        return get().keyPressed[key];
    }

    public static void charCallback(long window, int character) {
        get().charInput = character;
    }
}
