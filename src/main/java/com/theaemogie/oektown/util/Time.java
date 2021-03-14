package com.theaemogie.oektown.util;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Time {
    public static double timeStarted = glfwGetTime();

    public static double getTime(){
        double timeElapsed =  (glfwGetTime() - timeStarted);
        return timeElapsed;
    }
}
