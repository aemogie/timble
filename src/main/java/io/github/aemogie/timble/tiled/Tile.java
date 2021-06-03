package io.github.aemogie.timble.tiled;

import io.github.aemogie.timble.components.Component;
import io.github.aemogie.timble.components.SpriteRenderer;
import io.github.aemogie.timble.physics.components.PhysicsComponent;
import io.github.aemogie.timble.renderer.Color;
import io.github.aemogie.timble.renderer.Sprite;
import io.github.aemogie.timble.timble.GameObject;
import io.github.aemogie.timble.timble.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Tile extends GameObject {
	
	SpriteRenderer spriteRenderer = new SpriteRenderer();
	
	public Tile(int x, int y, Vector2f tileSize, Vector2f offset, Sprite sprite, Color color) {
		super("Tile " + x + " : " + y, new Transform(
				x * tileSize.x + offset.x,
				y * tileSize.y + offset.y,
				tileSize.x,
				tileSize.y
		));
		spriteRenderer.setSprite(sprite);
		spriteRenderer.setColor(color);
		super.addComponent(spriteRenderer);
	}
	
	public Tile(int x, int y, int z, Vector2f tileSize, Vector2f offset, Sprite sprite, Color color) {
		super("Tile " + x + " : " + y, new Transform(
				new Vector3f(
						x * tileSize.x + offset.x,
						y * tileSize.y + offset.y,
						0
				),
				new Vector2f(
						tileSize.x,
						tileSize.y
				)
		));
		spriteRenderer.setSprite(sprite);
		spriteRenderer.setColor(color);
		super.addComponent(spriteRenderer);
	}
	
	@Override
	public void start() {
		super.start();
	}
	
	@Deprecated @Override
	public <T extends Component> T getComponent(Class<T> componentClass) {
		return super.getComponent(componentClass);
	}
	
	@Deprecated @Override
	public <T extends Component> void removeComponent(Class<T> componentClass) {
		super.removeComponent(componentClass);
	}
	
	/**
	 * @deprecated for the Tile class.
	 * @see #addPhysicsComponent
	 */
	@Deprecated @Override
	public Tile addComponent(Component c) {
		return (Tile) super.addComponent(c);
	}
	
	public <T extends PhysicsComponent> Tile addPhysicsComponent(T c) {
		return (Tile) super.addComponent(c);
	}
}