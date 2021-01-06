package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class ObstacleSpawner {

    // create array of obstacle
    public ArrayList<Obstacle> obstacleObject;

    // type of obstacle
    public final int TOP = 0;
    public final int MID = 1;
    public final int BOT = 2;

    // storing the gamespeed
    private float speed;

    // spawning condition
    private boolean startSpawning = false;

    // which obstacle is moving next
    private int currentObstacleObject = 0;

    // many obstacle is being created
    private int manyObstaclePooling = 3;

    ObstacleSpawner(Context context, float screenX, float yPosition, float playerHeight, float playerJumpHeight, float gameSpeed)
    {
        // initialize obstacle array
        obstacleObject = new ArrayList<>();
        for(int i = 0; i < manyObstaclePooling; i++)
            obstacleObject.add(new Obstacle(
               context,
               screenX,
               yPosition,
                    playerHeight,
                    playerJumpHeight,
                    gameSpeed
            ));

        // inisialisasi gamespeed
        this.speed = gameSpeed;
    }

    public void update()
    {
        // update object obstacle
        for (Obstacle ob : obstacleObject)
        {
            ob.update();
            ob.updateSpeed(speed);
        }
        // set current obstacle
        if (obstacleObject.get(currentObstacleObject).getXPosition() < 200)
            setCurrentObstacleObject();

        // start moving current obstacle
        if (!obstacleObject.get(currentObstacleObject).isMoving)
            obstacleObject.get(currentObstacleObject).startMove();
    }

    public void draw(Canvas canvas, Paint paint)
    {
        // draw current obstacle
        for (Obstacle ob : obstacleObject)
            ob.draw(canvas, paint);

    }

    private void setCurrentObstacleObject()
    {
        // spawn another obstacle if current obstacle in the specific position
        // change current obstacle
        currentObstacleObject++;
        if (currentObstacleObject >= manyObstaclePooling)
            currentObstacleObject = 0;

        // random type of obstacle
        obstacleObject.get(currentObstacleObject).setRandomObstacle();
    }

    public void updateSpeed(float _speed)
    {
        speed = _speed;
    }

    public void resetObstacleSpawner(float _speed)
    {
        speed = _speed;
        currentObstacleObject = 0;
        for (Obstacle ob : obstacleObject)
            ob.resetObstacleCondition();
    }

}
