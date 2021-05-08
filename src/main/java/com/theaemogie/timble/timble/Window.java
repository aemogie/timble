package com.theaemogie.timble.timble;

import com.theaemogie.timble.editor.LevelEditorScene;
import com.theaemogie.timble.tiles.PickingTexture;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.eventhandlers.WindowResizeListener;
import com.theaemogie.timble.renderer.*;
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
	private final boolean vsync;
	public float r, g, b;
	private Scene currentScene;
	private long glfwWindow;
	
	private GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
	private int width, height;
	public ImGuiLayer imGuiLayer;
	private FrameBuffer frameBuffer;
	private PickingTexture pickingTexture;
	
	//region Constructor.
	private Window(String title, Vector3f color, boolean vsync) {
		this.title = title;
		this.width = (int) (videoMode.width() / 1.625);
		this.height = (int) (videoMode.height() / 1.625);
		this.r = color.x;
		this.g = color.y;
		this.b = color.z;
		this.vsync = vsync;
	}
	//endregion
	
	//region Create windows.
	
	public static Window create(String title) {
		return create(title, new Vector3f(0.2f,0.2f,0.2f));
	}
	
	public static Window create(String title, Vector3f color) {
		return create(title, color, true);
	}
	
	public static Window create(String title, Vector3f color, boolean vsync) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");
		return new Window(title, color, vsync);
	}
	//endregion
	
	//region Main stuff: initialize, loop, destroy.
	public void run(int scene) {
		windowInit(scene);
		windowLoop();
		windowDestroy();
	}
	
	public void windowInit(int scene) {
		//Properties
		glfwDefaultWindowHints(); //Default window hints
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //Invisible till setup is complete
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); //Resizable to true in-case default doesn't work
//		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL); //The long is the memory address of the window.
		
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
			imGuiLayer.initImGui(new Configuration());
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
			currentScene.render(this);
			
			pickingTexture.disableWriting();
			glEnable(GL_BLEND);
			//endregion
			
			//Render pass 2. Render to game.
			DebugDraw.beginFrame();
			
			currentScene.preFrame(this);
			
			glClearColor(r, g, b, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			
			if (deltaTime >= 0) {
				DebugDraw.draw(this);
				Renderer.bindShader(defaultShader);
				currentScene.update(this, deltaTime);
				currentScene.render(this);
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
				currentScene = new LevelEditorScene();
				break;
			case 1:
				currentScene = new LevelScene();
				break;
			default:
				assert false : "Unknown scene '" + newScene + "'!";
				break;
		}
		currentScene.load();
		currentScene.init(this);
		currentScene.start();
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
}