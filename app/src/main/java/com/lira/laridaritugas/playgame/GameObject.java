package com.lira.laridaritugas.playgame;

import android.graphics.RectF;

public class GameObject {

    private ImageAnimation objectAnimation;

    public float sizeX;
    public float sizeY;
    public float positionX;
    public float positionY;

    private RectF whereToDraw;

    GameObject(ImageAnimation animation, float positionX, float positionY)
    {
        // DEFAULT DATA
        objectAnimation = animation;
        this.sizeX = objectAnimation.getLength();
        this.sizeY = objectAnimation.getHeight();
        this.positionX = positionX;
        this.positionY = positionY;

        setWhereToDraw();
    }

    public void update()
    {
        setWhereToDraw();
    }

    public void maintainResizeByY(float sizeY)
    {
        this.sizeX = (sizeY / objectAnimation.getHeight()) * objectAnimation.getLength();
        this.sizeY = sizeY;
        setWhereToDraw();
    }

    private void setWhereToDraw()
    {
        whereToDraw = new RectF(positionX, positionY - sizeY, positionX + sizeX, positionY);
    }

    /* SETTER */
    public void setObjectAnimation(ImageAnimation animation)
    {
        objectAnimation = animation;
        maintainResizeByY(sizeY);
    }

    /* GETTER */
    public RectF getWhereToDraw() {
        return  whereToDraw;
    }

    public ImageAnimation getObjectAnimation() {
        return objectAnimation;
    }
}
