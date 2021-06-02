package com.theaemogie.timble.timble;

import com.theaemogie.timble.editor.LevelEditorScene;
import com.theaemogie.timble.renderer.PickingTexture;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.eventhandlers.WindowResizeListener;
import com.theaemogie.timble.renderer.*;
import com.theaemogie.timble.scenes.LevelScene;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.util.AssetPool;
import com.theaemogie.timble.util.Time;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static com.theaemogie.timble.util.Logger.debugLog;
import static com.theaemogie.timble.util.PresetsSettings.GREY;
import static com.theaemogie.timble.util.StringUtils.resourcePath;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Window {
	
	private final String title;
	private final Color color;
	private final boolean fullscreen;
	private final boolean vsync;
	private Scene currentScene;
	private long glfwWindow;
	
	private int width, height;
	public ImGuiLayer imGuiLayer;
	private FrameBuffer frameBuffer;
	private PickingTexture pickingTexture;
	
	//region Constructor.
	private Window(String title, Color color, boolean fullscreen, boolean vsync) {
		this.title = title;
		GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
		this.width = fullscreen ? videoMode.width() :  (int) (videoMode.width() / 1.5);
		this.height = fullscreen ? videoMode.height() :  (int) (videoMode.height() / 1.5);
		this.color = color;
		this.vsync = vsync;
		this.fullscreen = fullscreen;
	}
	//endregion
	
	//region Create windows.
	public static Window create() {
		return create("Timble Game Engine (By Aemogie.)");
	}
	
	public static Window create(String title) {
		return create(title, GREY);
	}
	
	public static Window create(String title, Color color) {
		return create(title, color, false);
	}
	
	public static Window create(String title, Color color, boolean fullScreen) {
		return create(title, color, fullScreen, true);
	}
	
	public static Window create(String title, Color color, boolean fullScreen, boolean vsync) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");
		return new Window(title, color, fullScreen, vsync);
	}
	//endregion
	
	//region Main stuff: initialize, loop, destroy.
	public void run(int scene) {
		debugLog("Initializing Window...");
		windowInit(scene);
		debugLog("Window Initialized!");
		debugLog("Trying to run window loop...");
		windowLoop();
		debugLog("Successfully exited the window loop!");
		debugLog("Destroying the window context...");
		windowDestroy();
		debugLog("Successfully destroyed the current context!");
	}
	
	public void windowInit(int scene) {
		//Properties
		glfwDefaultWindowHints(); //Default window hints
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //Invisible till setup is complete
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); //Resizable to true in-case default doesn't work
//		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL); //The long is the memory address of the window.
		
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create GLFW Window!");
		}
		
		// TODO: 4/11/2021 Port to Dev Kit.
		//region Callback setup
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwSetCharCallback(glfwWindow, KeyListener::charCallback);
		//endregion
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(glfwWindow);
		//Enable vsync
		glfwSwapInterval(vsync ? 1 : 0);
		
		// Make the window visible
		glfwShowWindow(glfwWindow);
		/*
		This line is critical for LWJGL's interoperation with GLFW's
		OpenGL context, or any context that is managed externally.
		LWJGL detects the context that is current in the current thread,
		creates the GLCapabilities instance and makes the OpenGL
		bindings available for use.
		*/
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// Set resize callback after we make the current context.
		glfwSetWindowSizeCallback(glfwWindow, (window, width, height) -> WindowResizeListener.resizeCallback(this, width, height));
		
		
		this.frameBuffer = new FrameBuffer(width, height);
		this.pickingTexture = new PickingTexture(width, height);
		
		Window.this.changeScene(scene);
		
		if (currentScene instanceof LevelEditorScene) {
			imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
			imGuiLayer.initImGui();
		}
		
		glViewport(0, 0, width, height);
	}
	
	public void windowLoop() {
		double startTime = Time.getTime();
		double endTime;
		double deltaTime = -1.0;
		
		Shader defaultShader = AssetPool.getShader(resourcePath("shaders/Default.glsl"));
		Shader pickingShader = AssetPool.getShader(resourcePath("shaders/Picking.glsl"));
		
		while (!glfwWindowShouldClose(glfwWindow)) {
			
			glfwSetWindowTitle(glfwWindow,title + " - " +  currentScene.getClass().getSimpleName());
			
			// Poll events
			glfwPollEvents();
			
			//region Render pass 1. Render to picking texture.
			glDisable(GL_BLEND);
			pickingTexture.enableWriting();
			
//			glViewport(0, 0, width, height);
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Renderer.bindShader(pickingShader);
			currentScene.render();
			
			pickingTexture.disableWriting();
			glEnable(GL_BLEND);
			//endregion
			
			//Render pass 2. Render to game.
			DebugDraw.beginFrame();
			
			currentScene.preFrame(this);
			Vector3f colorInVec3 = color.toNormVec3();
			glClearColor(colorInVec3.x, colorInVec3.y, colorInVec3.z, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			
			if (deltaTime >= 0) {
				DebugDraw.draw(this);
				Renderer.bindShader(defaultShader);
				currentScene.update(deltaTime);
				currentScene.render();
			}
			
			currentScene.postFrame(this, deltaTime);
			
			glfwSwapBuffers(glfwWindow);
			
			MouseListener.endFrame();
			
			endTime = Time.getTime();
			deltaTime = endTime - startTime;
			startTime = endTime;
		}
	}
	
	private void windowDestroy() {
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		currentScene.end(this);
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}
	//endregion
	
	//region Get and set scenes.
	public void changeScene(int newScene) {
		if (currentScene != null) currentScene.end(this);
		switch (newScene) {
			case 0:
				currentScene = new LevelEditorScene(this);
				break;
			case 1:
				currentScene = new LevelScene(this);
				break;
			default:
				assert false : "Unknown scene '" + newScene + "'!";
				break;
		}
		currentScene.load();
		currentScene.init(this);
		currentScene.start();
		debugLog("Successfully changed the scene to: " + currentScene);
	}
	
	public Scene getCurrentScene() {
		return this.currentScene;
	}
	//endregion
	
	//region Get and set width, height and aspect ration. 
	public int getWidth() {
		return this.width;
	}
	
	public void setWidth(int newWidth) {
		this.width = newWidth;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int newHeight) {
		this.height = newHeight;
	}
	
	public float getTargetAspectRatio() {
		return 16.0f / 9.0f;
	}
	//endregion
	
	//region Get framebuffer and picking texture.
	public FrameBuffer getFrameBuffer() {
		return this.frameBuffer;
	}
	
	public PickingTexture getPickingTexture() {
		return pickingTexture;
	}
	//endregion
	
	//region Get and set the background color.
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color.setColor(color);
		debugLog("Successfully set the background color to: " + color);
	}
	//endregion
}