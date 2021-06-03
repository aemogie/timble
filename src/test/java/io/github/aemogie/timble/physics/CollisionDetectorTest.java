package io.github.aemogie.timble.physics;

import io.github.aemogie.timble.physics.primitives.Line;
import io.github.aemogie.timble.physics.rigidbody.IntersectionDetector;
import junit.framework.TestCase;
import org.joml.Vector2f;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class CollisionDetectorTest {
	private final float EPSILON = 0.000001f;
	
	@Test
	public void pointAndLineShouldReturnTrueTest() {
		Vector2f point = new Vector2f(0, 0);
		Line line = new Line(new Vector2f(0, 0), new Vector2f(12, 4));
		TestCase.assertTrue(IntersectionDetector.pointAndLine(point, line));
	}
	@Test
	public void pointAndLVerticalLineShouldReturnTrueTest() {
		Vector2f point = new Vector2f(0, 5);
		Line line = new Line(new Vector2f(0, 0), new Vector2f(0, 10));
		assertTrue(IntersectionDetector.pointAndLine(point, line));
	}
}
