package com.theaemogie.oektown.timble.listener;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class MouseListener {

    private static MouseListener instance;

    private double scrollX;
    private double scrollY;

    private double xPos;
    private double yPos;
    private double lastX;
    private double lastY;

    public boolean[] isButtonPressed = new boolean[3];

    private boolean isDragging;

    public MouseListener() {

        this.scrollX = 0.0;
        this.scrollY = 0.0;

        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;

        this.isDragging = isButtonPressed[0] || isButtonPressed[1] || isButtonPressed[2];
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    public static void mousePositionCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button > get().isButtonPressed.length) {
                get().isButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button > get().isButtonPressed.length) {
                get().isButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDX() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDY() {
        return (float) (get().lastY - get().yPos);
    }

    public static boolean isButtonPressed(int button) {
        if (button > get().isButtonPressed.length) {
            return get().isButtonPressed[button];
        } else {
            return false;
        }
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static void frameEnded() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

}
