package com.theaemogie.timble.timble;

import com.theaemogie.timble.editor.LevelEditorScene;
import com.theaemogie.timble.editor.PickingTexture;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.eventhandlers.WindowResizeListener;
import com.theaemogie.timble.renderer.DebugDraw;
import com.theaemogie.timble.renderer.FrameBuffer;
import com.theaemogie.timble.renderer.Renderer;
import com.theaemogie.timble.renderer.Shader;
import com.theaemogie.timble.scenes.LevelScene;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.util.AssetPool;
import com.theaemogie.timble.util.Time;
import imgui.app.Configuration;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
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
	
	private final String title;
	private final boolean vsync;
	public float r, g, b;
	private Scene currentScene;
	private long glfwWindow;
	
	private GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
	private int targetWidth = 1366;
	private int targetHeight = 768;
	private float targetAspectRatio;
	private int width, height;
	private ImGuiLayer imGuiLayer;
	private FrameBuffer frameBuffer;
	private PickingTexture pickingTexture;
	
	//region Create windows.
	public static Window create(String title) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		return new Window(title);
	}
	
	public static Window create(String title, int width, int height) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		
		return new Window(title, width, height);
	}
	
	public static Window create(String title, int width, int height, Vector3f color) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		
		return new Window(title, width, height, color);
	}
	
	public static Window create(String title, int width, int height, Vector3f color, boolean vsync) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW!");
		}
		
		return new Window(title, width, height, color, vsync);
	}
	//endregion
	
	//region Constructors.
	private Window(String title) {
		this(title, 1366, 768);
	}
	
	private Window(String title, int width, int height) {
		this(title, width, height, new Vector3f(0.2f, 0.2f, 0.2f));
	}
	
	private Window(String title, int width, int height, Vector3f color) {
		this(title, width, height, color, true);
	}
	
	private Window(String title, int width, int height, Vector3f color, boolean vsync) {
		this.width = this.targetWidth = width;
		this.height = this.targetHeight = height;
		this.targetAspectRatio = (float) targetWidth / (float) targetHeight;
		this.title = title;
		this.r = color.x;
		this.g = color.y;
		this.b = color.z;
		this.vsync = vsync;
	}
	//endregion
	
	//region Main stuff: initialize, loop, destroy.
	public void run() {
		windowInit();
		windowLoop();
		windowDestroy();
	}
	
	public void windowInit() {
		//Properties
		glfwDefaultWindowHints(); //Default window hints
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //Invisible till setup is complete
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); //Resizable to true in-case default doesn't work
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL); //The long is the memory address of the window.
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create GLFW Window!");
		}
		
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

//		videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
		this.targetWidth = videoMode.width();
		this.targetHeight = videoMode.height();
		this.targetAspectRatio = (float) this.targetWidth / (float) this.targetHeight;
		
		this.frameBuffer = new FrameBuffer(width, height);
		this.pickingTexture = new PickingTexture(width, height);
		glViewport(0, 0, width, height);
		
		this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
		this.imGuiLayer.initImGui(new Configuration());
		
		Window.this.changeScene(0);
	}
	
	public void windowLoop() {
		
		double startTime = Time.getTime();
		double endTime;
		double deltaTime = -1.0;
		
		Shader defaultShader = AssetPool.getShader("src/main/resources/assets/shaders/Default.glsl");
		Shader pickingShader = AssetPool.getShader("src/main/resources/assets/shaders/Picking.glsl");
		
		while (!glfwWindowShouldClose(glfwWindow)) {
			// Poll events
			glfwPollEvents();
			
			//region Render pass 1. Render to picking texture.
			glDisable(GL_BLEND);
			pickingTexture.enableWriting();
			
			glViewport(0, 0, width, height);
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Renderer.bindShader(pickingShader);
			currentScene.render(this);
			
			pickingTexture.disableWriting();
			glEnable(GL_BLEND);
			//endregion
			
			//Render pass 2. Render to game.
			
			DebugDraw.beginFrame();
			
			this.frameBuffer.bind();
			glClearColor(r, g, b, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			
			if (deltaTime >= 0) {
				DebugDraw.draw(this);
				Renderer.bindShader(defaultShader);
				currentScene.update(this, deltaTime);
				currentScene.render(this);
			}
			this.frameBuffer.unbind();
			
			this.imGuiLayer.update(this, (float) deltaTime, currentScene);
			
			glfwSwapBuffers(glfwWindow);
			
			MouseListener.endFrame();
			
			endTime = Time.getTime();
			deltaTime = endTime - startTime;
			startTime = endTime;
		}
		currentScene.saveExit();
	}
	
	private void windowDestroy() {
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		imGuiLayer.disposeImGui();
		
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}
	//endregion
	
	//region Get and set scenes.
	public void changeScene(int newScene) {
		switch (newScene) {
			case 0:
				this.currentScene = new LevelEditorScene();
				break;
			case 1:
				this.currentScene = new LevelScene();
				break;
			default:
				assert false : "Unknown scene '" + newScene + "'!";
				break;
		}
		
		this.currentScene.load();
		this.currentScene.init();
		this.currentScene.start();
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
	
	//region Get framebuffer.
	public FrameBuffer getFrameBuffer() {
		return this.frameBuffer;
	}
	//endregion
}