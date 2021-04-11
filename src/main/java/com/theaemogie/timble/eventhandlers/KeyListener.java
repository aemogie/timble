package com.theaemogie.timble.eventhandlers;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class KeyListener {
    private static KeyListener instance;
    private static boolean[] keyPressed = new boolean[350];
    private static int charInput;
    
    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (!(key < 0 || key >= keyPressed.length)) {
            if (action == GLFW_PRESS) {
                keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                keyPressed[key] = false;
            }
        }
    }

    public static boolean isKeyPressed(int key) {
        return keyPressed[key];
    }

    public static void charCallback(long window, int character) {
        charInput = character;
    }
}
