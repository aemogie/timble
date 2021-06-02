package com.theaemogie.timble.components;

import com.theaemogie.timble.timble.GameObject;
import com.theaemogie.timble.timble.Window;
import com.theaemogie.timble.eventhandlers.MouseListener;
import com.theaemogie.timble.util.PresetsSettings;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class MouseControls extends Component {
    GameObject holdingObject = null;

    public void pickupObject(Window window, GameObject gameObject) {
        this.holdingObject = gameObject;
        window.getCurrentScene().addGameObjectToScene(holdingObject);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(Window window, double deltaTime) {
        if (holdingObject != null) {
            holdingObject.transform.position.x = (int) (MouseListener.getOrthoX(window) / PresetsSettings.GRID_WIDTH) * PresetsSettings.GRID_WIDTH;
            holdingObject.transform.position.y = (int) (MouseListener.getOrthoY(window) / PresetsSettings.GRID_HEIGHT) * PresetsSettings.GRID_HEIGHT;

            if (MouseListener.isButtonPressed(0)) {
                place();
            }

        }
    }
}
