package com.theaemogie.timble.components;

import com.theaemogie.timble.renderer.Color;
import com.theaemogie.timble.timble.Camera;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.renderer.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.theaemogie.timble.util.Settings.GRID_HEIGHT;
import static com.theaemogie.timble.util.Settings.GRID_WIDTH;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class GridLines extends Component {
	
	Color color = new Color(0, 0, 0, 63);
	
	@Override
	public void update(Window window, double deltaTime) {
		Camera camera = window.getCurrentScene().getCamera();
		Vector2f cameraPosition = camera.position;
		Vector2f projectionSize = camera.getProjectionSize();
		
		int firstX = ((int) (cameraPosition.x / GRID_WIDTH) - 1) * GRID_HEIGHT;
		int firstY = ((int) (cameraPosition.y / GRID_HEIGHT) - 1) * GRID_HEIGHT;
		
		int numVtLines = (int) (projectionSize.x * camera.getZoom() / GRID_WIDTH) + 2;
		int numHzLines = (int) (projectionSize.y * camera.getZoom() / GRID_HEIGHT) + 2;
		
		int width = (int) (projectionSize.x * camera.getZoom()) + GRID_WIDTH * 2;
		int height = (int) (projectionSize.y * camera.getZoom()) + GRID_HEIGHT * 2;
		
		int maxLines = Math.max(numVtLines, numHzLines);
		
		for (int i = 0; i < maxLines; i++) {
			int x = firstX + (GRID_WIDTH * i);
			int y = firstY + (GRID_HEIGHT * i);
			
			if (i < numVtLines) {
				DebugDraw.addLine(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
			}
			
			if (i < numHzLines) {
				DebugDraw.addLine(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
			}
		}
	}
}