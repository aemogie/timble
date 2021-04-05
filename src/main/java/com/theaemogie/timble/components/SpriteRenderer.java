package com.theaemogie.timble.components;

import com.theaemogie.timble.renderer.Sprite;
import com.theaemogie.timble.renderer.Texture;
import com.theaemogie.timble.timble.Transform;
import com.theaemogie.timble.timble.Window;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class SpriteRenderer extends Component {
	
	private Vector4f color = new Vector4f(1, 1, 1, 1);
	private Sprite sprite = new Sprite();
	
	private transient Transform lastTransform;
	private transient boolean isDirty = true;

//    public SpriteRenderer(Vector4f color) {
//        this.color = color;
//        this.sprite = new Sprite(null);
//    }
//
//    public SpriteRenderer(Sprite sprite) {
//        this.sprite = sprite;
//        this.color = new Vector4f(1,1,1,1);
//    }
	
	@Override
	public void start() {
		this.lastTransform = gameObject.transform.copy();
	}
	
	@Override
	public void update(Window window, double deltaTime) {
		if (!this.lastTransform.equals(gameObject.transform)) {
			gameObject.transform.copy(this.lastTransform);
			this.isDirty = true;
		}
	}
	
	@Override
	public void imGui() {
		if (ImGui.collapsingHeader("Color Picker")) {
			float[] imColor = {color.x, color.y, color.z, color.w};
			if (ImGui.colorPicker4("", imColor)) {
				this.color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
				this.isDirty = true;
			}
		}
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public void setColor(Vector4f color) {
		if (!this.color.equals(color)) {
			this.color.set(color);
			this.isDirty = true;
		}
	}
	
	public Texture getTexture() {
		return sprite.getTexture();
	}
	
	public void setTexture(Texture texture) {
		this.sprite.setTexture(texture);
	}
	
	public Vector2f[] getTextureCoords() {
		return sprite.getTextureCoords();
	}
	
	public boolean isDirty() {
		return this.isDirty;
	}
	
	public void setClean() {
		isDirty = false;
	}
	
	public SpriteRenderer setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.isDirty = true;
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder outputString = new StringBuilder("Class: " + this.getClass().getCanonicalName() + "\n");
		outputString.append("Sprite: \n" + this.sprite.toString().replaceAll("(?m)^", "\t") + "\n");
		return outputString.toString();
	}
}