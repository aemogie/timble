package com.theaemogie.timble.editor;

import com.theaemogie.timble.components.EditorCamera;
import com.theaemogie.timble.components.GridLines;
import com.theaemogie.timble.components.MouseControls;
import com.theaemogie.timble.scenes.Scene;
import com.theaemogie.timble.tiled.TiledMap;
import com.theaemogie.timble.timble.Camera;
import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.timble.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.theaemogie.timble.util.StringUtils.resourcePath;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {
	
	private GameObject levelEditorComponents = null;
	
	public LevelEditorScene(Window window) {
		super(
				window,
				new Vector2i(32, 32),
				0f
		);
	}
	
	@Override
	public void init(Window window) {
		super.init(window);
		if (levelEditorComponents == null) {
			levelEditorComponents = new GameObject("Level Editor Components", new Transform())
					.addComponent(new MouseControls())
					.addComponent(new GridLines())
					.addComponent(new EditorCamera());
		}
		
		levelEditorComponents.getComponent(EditorCamera.class).init(this.camera);
		
		//region Map.
		new TiledMap(resourcePath("tilemap/map.json"), 32, 32, this);
		//endregion
	}
	
	@Override
	protected void loadResources() {
		super.loadResources();
	}
	
	@Override
	public void preFrame(Window window) {
		window.getFrameBuffer().bind();
	}
	
	@Override
	public void update(double deltaTime) {
		levelEditorComponents.update(WINDOW, deltaTime);
		super.update(deltaTime);
	}
	
	@Override
	public void postFrame(Window window, double deltaTime) {
		window.getFrameBuffer().unbind();
		window.imGuiLayer.update(window, (float) deltaTime, this);
	}
	
	@Override
	public void end(Window window) {
		window.imGuiLayer.disposeImGui();
		super.end(window);
	}
	
	@Override
	public void saveExit() {
		try {
			FileWriter editorComponentsFile = new FileWriter(".run/editorComponents.dat");
			editorComponentsFile.write(gson.toJson(levelEditorComponents));
			editorComponentsFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileWriter cameraFile = new FileWriter(".run/camera.dat");
			cameraFile.write(gson.toJson(this.camera));
			cameraFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void load() {
		String cameraFile = "";
		try {
			cameraFile = new String(Files.readAllBytes(Paths.get(".run/camera.dat")));
		} catch (IOException ignored) {
		}
		if (!(cameraFile.equals("") || cameraFile.equals("{}"))) {
			this.camera = gson.fromJson(cameraFile, Camera.class);
			camera.init(camera.position);
			cameraLoaded = true;
		}
		String editorComponentsFile = "";
		try {
			editorComponentsFile = new String(Files.readAllBytes(Paths.get(".run/editorComponents.dat")));
		} catch (IOException ignored) {
		}
		if (!(editorComponentsFile.equals("") || editorComponentsFile.equals("{}"))) {
			this.levelEditorComponents = gson.fromJson(editorComponentsFile, GameObject.class);
		}
	}
}