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
    public static boolean pointAndLine(Vector2f point, Line line) {
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;
        if (dx == 0f) {
            return TimbleMath.compare(point.x, line.getStart().x);
        }
        float gradient = dy/dx;

        //y = mx + c
        //y = c + mx
        //y - mx = c;
        float intercept = line.getEnd().y - (gradient * line.getEnd().x);

        //Check the equation of vector.
        return point.y == gradient * point.x + intercept;
    }

    public static boolean pointAndCircle(Vector2f point, Circle circle) {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean pointAndAABB(Vector2f point, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return
                min.x <= point.x &&
                point.x <= max.x &&
                min.y <= point.y &&
                point.y <= max.y;
    }

    public static boolean pointAndOBB(Vector2f point, OBB obb) {
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        TimbleMath.rotate(pointLocalBoxSpace, obb.getRigidBody().getPosition(), obb.getRigidBody().getRotation());

        Vector2f min = obb.getMin();
        Vector2f max = obb.getMax();

        return
                min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.x <= max.x &&
                min.y <= pointLocalBoxSpace.y &&
                pointLocalBoxSpace.y <= max.y;
    }
    
    //======================================================
    //"Line on" Tests.
    //======================================================
    public static boolean lineAndCircle(Line line, Circle circle) {
        if (pointAndCircle(line.getStart(), circle) || pointAndCircle(line.getEnd(), circle)) {
            return true;
        }
        
        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());
        
        //"Project" circle's position to line segment. -> t
        Vector2f circlePos = circle.getCenter();
        Vector2f circlePosToLineStart = new Vector2f(circlePos).sub(line.getStart());
        float t = circlePosToLineStart.dot(ab) / ab.dot(ab);
    
        //Check if t is on line segment.
        if (t < 0.0f || t > 1.0f) {
            return false;
        }
        
        //Find closest point to line segment.
        Vector2f closestPoint = new Vector2f(line.getStart()).add(ab.mul(t));
    
        return pointAndCircle(closestPoint, circle);
    }
}
