package com.theaemogie.timble.timble;

import com.theaemogie.timble.editor.GameViewWindow;
import com.theaemogie.timble.editor.PropertiesWindow;
import com.theaemogie.timble.eventhandlers.KeyListener;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.renderer.PickingTexture;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;

import static com.theaemogie.timble.util.StringUtils.resourcePath;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class ImGuiLayer {
	
	//region Callbacks
	public static boolean[] isButtonPressed = new boolean[9];
	//endregion
	// LWJGL3 renderer (SHOULD be initialized)
	private static ImGuiIO io;
	private static boolean[] keyPressed = new boolean[350];
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
	// Mouse cursors provided by GLFW
	private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];
	private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private long glfwWindow;
	private GameViewWindow gameViewWindow;
	private PropertiesWindow propertiesWindow;
	
	public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
		this.glfwWindow = glfwWindow;
		this.gameViewWindow = new GameViewWindow();
		this.propertiesWindow = new PropertiesWindow(pickingTexture);
	}
	
	public void mouseButtonCallback(long window, int button, int action, int mods, GameViewWindow gameViewWindow) {
		if (button < isButtonPressed.length) {
			if (action == GLFW_PRESS) {
				isButtonPressed[button] = true;
			} else if (action == GLFW_RELEASE) {
				isButtonPressed[button] = false;
				MouseListener.mouseButtonCallback(window, button, action, mods);
			}
		}
		io.setMouseDown(isButtonPressed);
		
		if (!io.getWantCaptureMouse() && isButtonPressed[1]) {
			ImGui.setWindowFocus(null);
		}
		
		if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
			MouseListener.mouseButtonCallback(window, button, action, mods);
		}
	}
	
	private void keyCallback(long window, int key, int scancode, int action, int mods) {
		if (!(key < 0 || key >= keyPressed.length)) {
			if (action == GLFW_PRESS) {
				keyPressed[key] = true;
			} else if (action == GLFW_RELEASE) {
				keyPressed[key] = false;
			}
		}
		io.setKeysDown(keyPressed);
		io.setKeyCtrl(keyPressed[GLFW_KEY_LEFT_CONTROL] || keyPressed[GLFW_KEY_RIGHT_CONTROL]);
		io.setKeyShift(keyPressed[GLFW_KEY_LEFT_SHIFT] || keyPressed[GLFW_KEY_RIGHT_SHIFT]);
		io.setKeyAlt(keyPressed[GLFW_KEY_LEFT_ALT] || keyPressed[GLFW_KEY_RIGHT_ALT]);
		io.setKeySuper(keyPressed[GLFW_KEY_LEFT_SUPER] || keyPressed[GLFW_KEY_RIGHT_SUPER]);
		
		if (!io.getWantCaptureKeyboard()) {
			KeyListener.keyCallback(window, key, scancode, action, mods);
		}
	}
	
	private void scrollCallback(long window, double xOffset, double yOffset) {
		io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
		io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
		MouseListener.mouseScrollCallback(window, xOffset, yOffset);
	}
	
	public void initImGui() {
		
		ImGui.createContext();
		
		io = ImGui.getIO();
		
		io.setIniFilename(".run/ImGui.ini"); // We don't want to save .ini file
		io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
		io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
//        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
		io.setConfigViewportsNoTaskBarIcon(true);
		io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
		io.setBackendPlatformName("imgui_java_impl_glfw");
		
		imGuiGlfw.init(glfwWindow, true);
		
		glfwSetMouseButtonCallback(glfwWindow, (window, button, action, mods) -> this.mouseButtonCallback(window, button, action, mods, gameViewWindow));
		glfwSetKeyCallback(glfwWindow, this::keyCallback);
		glfwSetScrollCallback(glfwWindow, this::scrollCallback);
		
		final ImFontAtlas fontAtlas = io.getFonts();
		final ImFontConfig fontConfig = new ImFontConfig();
		
		fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
		
		fontConfig.setPixelSnapH(true);
		fontAtlas.addFontFromFileTTF(resourcePath("fonts/8bit-8px.ttf").toAbsolutePath().toString(), 10, fontConfig);
		
		fontConfig.destroy();
		
		imGuiGl3.init("#version 330 core");
	}
	
	public void update(Window window, float deltaTime, Scene currentScene) {
		startFrame();
		
		// Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
		ImGui.newFrame();
		setupDockSpace(window); //Window begun. Have to end after process()
		process(window, currentScene);
		ImGui.end(); //Window ends.
		ImGui.render();
		
		endFrame();
	}
	
	private void startFrame() {
		imGuiGlfw.newFrame();
	}
	
	public void process(Window window, Scene currentScene) {
		ImGui.showDemoWindow();
		propertiesWindow.update(window, currentScene);
		propertiesWindow.imGui();
		gameViewWindow.imGui(window);
	}
	
	protected void endFrame() {
		// After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
		// At that moment ImGui will be rendered to the current OpenGL context.
		imGuiGl3.renderDrawData(ImGui.getDrawData());
		
		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(backupWindowPtr);
		}
	}
	
	private void setupDockSpace(Window window) {
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
		ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
		ImGui.setNextWindowSize(window.getWidth(), window.getHeight());
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
		
		windowFlags |= ImGuiWindowFlags.NoTitleBar;
		windowFlags |= ImGuiWindowFlags.NoCollapse;
		windowFlags |= ImGuiWindowFlags.NoResize;
		windowFlags |= ImGuiWindowFlags.NoMove;
		windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;
		windowFlags |= ImGuiWindowFlags.NoNavFocus;
		
		ImGui.begin("DockSpace", new ImBoolean(true), windowFlags);
		ImGui.popStyleVar(2);
		
		//DockSpace
		ImGui.dockSpace(ImGui.getID("DockSpace"));
	}
	
	public void disposeImGui() {
		imGuiGlfw.dispose();
		imGuiGl3.dispose();
		ImGui.destroyContext();
	}
}