package io.github.aemogie.timble.components;

import io.github.aemogie.timble.renderer.DebugDraw;
import io.github.aemogie.timble.timble.Camera;
import io.github.aemogie.timble.timble.Window;
import org.joml.Vector2f;

import static io.github.aemogie.timble.util.PresetsSettings.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class GridLines extends Component {
	
	@Override
	public void update(Window window, double deltaTime) {
		Camera camera = window.getCurrentScene().getCamera();
		Vector2f cameraPosition = camera.position;
		Vector2f projectionSize = camera.getProjectionSize();
		
		float firstX = ((int) Math.floor(cameraPosition.x / GRID_WIDTH) - 1) * GRID_HEIGHT;
		float firstY = ((int) Math.floor(cameraPosition.y / GRID_HEIGHT) - 1) * GRID_HEIGHT;
		
		int numVtLines = (int) (projectionSize.x * camera.getZoom() / GRID_WIDTH) + 2;
		int numHzLines = (int) (projectionSize.y * camera.getZoom() / GRID_HEIGHT) + 2;
		
		float width = (int) (projectionSize.x * camera.getZoom()) + GRID_WIDTH * 2;
		float height = (int) (projectionSize.y * camera.getZoom()) + GRID_HEIGHT * 2;
		
		int maxLines = Math.max(numVtLines, numHzLines);
		
		for (int i = 0; i < maxLines; i++) {
			float x = firstX + (GRID_WIDTH * i);
			float y = firstY + (GRID_HEIGHT * i);
			
			if (i < numVtLines) {
				DebugDraw.addLine(new Vector2f(x, firstY), new Vector2f(x, firstY + height), GRID_COLOR);
			}
			
			if (i < numHzLines) {
				DebugDraw.addLine(new Vector2f(firstX, y), new Vector2f(firstX + width, y), GRID_COLOR);
			}
		}
	}
}