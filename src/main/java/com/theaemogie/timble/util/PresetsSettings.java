package com.theaemogie.timble.util;

import com.theaemogie.timble.renderer.Color;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class PresetsSettings {
    /**
     * <b>Presets</b>
     */
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0,0,0);
    public static final Color GREY = new Color(42, 42, 42);
    public static final Color HALF_ALPHA_WHITE = new Color(255, 255, 255, 128);
    public static final Color HALF_ALPHA_BLACK = new Color(0, 0, 0, 128);
    
    /**
     * <b>Settings</b>
     */
    public static int GRID_WIDTH = 32;
    public static int GRID_HEIGHT = 32;
    public static final Color GRID_COLOR = HALF_ALPHA_BLACK;
    
    public static final int MAX_LINES = 10000;
    
}