package com.theaemogie.learninggame.components;

import com.theaemogie.learninggame.gameengine.GameObject;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public abstract class Component {
    public GameObject gameObject = null;

    public void start(){

    }

    public abstract void update(double deltaTime);
}
