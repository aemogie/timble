package com.theaemogie.timble.physics.rigidbody;

import com.theaemogie.timble.physics.primitives.AABB;
import com.theaemogie.timble.physics.primitives.OBB;
import com.theaemogie.timble.physics.primitives.Circle;
import com.theaemogie.timble.physics.primitives.Line;
import com.theaemogie.timble.util.TimbleMath;
import org.joml.Vector2f;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class IntersectionDetector {

    //======================================================
    //"Point on" Tests.
    //======================================================
    public static boolean pointOnLine(Vector2f point, Line line) {
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;
        float gradient = dy/dx;

        //y = mx + c
        //y = c + mx
        //y - mx = c;
        float intercept = line.getEnd().y - (gradient * line.getEnd().x);

        //Check the equation of vector.
        return point.y == gradient * point.x + intercept;
    }

    public static boolean pointInCircle(Vector2f point, Circle circle) {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInAABB(Vector2f point, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return
                min.x <= point.x &&
                point.x <= max.x &&
                min.y <= point.y &&
                point.y <= max.y;
    }

    public static boolean pointInBox2D(Vector2f point, OBB box) {
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        TimbleMath.rotate(pointLocalBoxSpace, box.getRigidBody().getPosition(), box.getRigidBody().getRotation());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return
                min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.x <= max.x &&
                min.y <= pointLocalBoxSpace.y &&
                pointLocalBoxSpace.y <= max.y;
    }

    //======================================================
    //"Line on" Tests.
    //======================================================
}
