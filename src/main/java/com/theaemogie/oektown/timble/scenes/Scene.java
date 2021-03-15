package com.theaemogie.oektown.timble.scenes;

import com.theaemogie.oektown.timble.Camera;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Scene {

    protected Camera camera;

    public Scene() {
    }

    public void init(){

    }

    public abstract void update(double deltaTime);
}