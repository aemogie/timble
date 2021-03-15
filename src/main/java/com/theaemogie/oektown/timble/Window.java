package com.theaemogie.oektown.timble;

import com.theaemogie.oektown.timble.listener.KeyListener;
import com.theaemogie.oektown.timble.listener.MouseListener;
import com.theaemogie.oektown.timble.scenes.LevelEditorScene;
import com.theaemogie.oektown.timble.scenes.LevelScene;
import com.theaemogie.oektown.timble.scenes.Scene;
import com.theaemogie.oektown.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Window {

    private static Window window = null;
    private static Scene currentScene;
    private final int WIDTH;
    private final int HEIGHT;
    private final String TITLE;
    public float r;
    public float g;
    public float b;
    private long glfwWindow;

    private Window() {
        this.WIDTH = 1280 / 2;
        this.HEIGHT = 720 / 2;
        this.TITLE = "Oek Town";
        this.r = 0.2f;
        this.g = 0.2f;
        this.b = 0.2f;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Invalid scene " + newScene + "!";
                break;
        }
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        loop();
        destroy();
    }

    private void init() {
        //Log errors to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialization check
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        //Properties
        glfwDefaultWindowHints(); //Default window hints
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //Invisible till setup is complete
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); //Resizable to true in-case default doesn't work
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CENTER_CURSOR, GLFW_TRUE);

        //Create window
        glfwWindow = glfwCreateWindow(this.WIDTH, this.HEIGHT, this.TITLE, NULL, NULL); //The long is the memory address of the window.
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW Window!");
        }

        //region Callback setup

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        //endregion

        //Have no idea yet. Didn't understand what he said in the tutorial.
        glfwMakeContextCurrent(glfwWindow);
        //Refresh as much as possible - V-Sync
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        /*
        This line is critical for LWJGL's interoperation with GLFW's
        OpenGL context, or any context that is managed externally.
        LWJGL detects the context that is current in the current thread,
        creates the GLCapabilities instance and makes the OpenGL
        bindings available for use.
        */
        GL.createCapabilities();

        Window.changeScene(0);
    }

    private void loop() {

        double startTime = Time.getTime();
        double endTime;
        double deltaTime = -1.0;

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glClearColor(r, g, b, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0) {
                currentScene.update(deltaTime);
            }


            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            deltaTime = endTime - startTime;
            startTime = endTime;
        }
    }

    private void destroy() {
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
