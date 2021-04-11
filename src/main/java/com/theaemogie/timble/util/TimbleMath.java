package com.theaemogie.timble.util;

import com.theaemogie.timble.timble.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TimbleMath {

    public static void rotate(Vector2f vector, Vector2f origin, float angleDegrees) {
        float x = vector.x - origin.x;
        float y = vector.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDegrees));
        float sin = (float) Math.sin(Math.toRadians(angleDegrees));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vector.x = xPrime;
        vector.y = yPrime;
    }

    public static boolean compare(float a, float b, float epsilon) {
        return Math.abs(a - b) <= epsilon * Math.max(1.0f, Math.max(Math.abs(a), Math.abs(b)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float a, float b) {
        return compare(a, b, Float.MAX_VALUE);
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1, vec2, Float.MAX_VALUE);
    }
    
    public static Vector2i generateXnYFromRatio(Window window, int screenWidth, int screenHeight) {
        int aspectWidth = screenWidth;
        int aspectHeight = (int) ((float) aspectWidth / window.getTargetAspectRatio());
    
        if (aspectHeight > screenHeight) {
            aspectHeight = screenHeight;
            aspectWidth = (int) ((float) aspectHeight * window.getTargetAspectRatio());
        }
        return new Vector2i(aspectWidth, aspectHeight);
    }
    
    public static float map(float value, float originStart, float originEnd, float targetStart, float targetEnd) {
        float mappedVal;
        mappedVal = (value - originStart); //Get value starting from 0
        mappedVal /= (originEnd - originStart); //Divide that ny the total range to get a value between 1 and 0.
        mappedVal *= (targetEnd - targetStart); //Multiply by required range to get a value between those 2 numbers.
        mappedVal += targetStart; //Add the start of required range so that it is back in the range it was supposed to be.
        return mappedVal;
    }
    
    public static float dist(Vector2f pos1, Vector2f pos2) {
        return (float) Math.hypot(pos1.x - pos2.x, pos1.y - pos2.y);
    }
}
