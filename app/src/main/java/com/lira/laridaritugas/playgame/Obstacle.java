package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

import static com.lira.laridaritugas.playgame.GameView.fps;
import static com.lira.laridaritugas.playgame.GameView.screenRatioY;

public class Obstacle {

    // Buat gameobject obstacle
    private GameObject obstacle;

    // Variabel global untuk menentukan jenis obstacle
    public final int TOP = 0;
    public final int MID = 1;
    public final int BOT = 2;

    // Variabel untuk menyimpan posisi dari jenis obstacle
    private float topObstacleYPosition;
    private float midObstacleYPosition;
    private float bottomObstacleYPosition;

    // Buat object animasi obstacle
    private ArrayList<ImageAnimation> flyingObstacleAnimations;
    private ArrayList<ImageAnimation> groundedObstacleAnimations;
    private ImageAnimation currentObstacleAnimation;

    // Kondisi default dari obstacle
    private float spawnXPosition;
    public boolean isMoving = false;
    public float speed;
    private float obstacleSize = 150;

    Obstacle(Context context, float spawnXPosition, float yPosition, float playerHeight, float playerJumpHeight, float speed) {

        // Inisialisasi nilai default spawn position
        this.spawnXPosition = spawnXPosition;

        // Inisialisasi game speed
        this.speed = speed;

        // Inisialisasi posisi default dari obstacle
        this.bottomObstacleYPosition = yPosition;
        this.midObstacleYPosition = bottomObstacleYPosition - (playerHeight / 2f) + 10f;
        this.topObstacleYPosition = bottomObstacleYPosition - playerJumpHeight - 20f;

        // Inisialisasi array animasi obstacle
        flyingObstacleAnimations = new ArrayList<>();
        groundedObstacleAnimations = new ArrayList<>();

        // add flying obstacle animations
        flyingObstacleAnimations.add(new ImageAnimation(
                context,
                "obsfly1",
                125,
                125,
                1,
                100
        ));

        flyingObstacleAnimations.add(new ImageAnimation(
                context,
                "obsfly2",
                125,
                125,
                1,
                100
        ));

        flyingObstacleAnimations.add(new ImageAnimation(
                context,
                "obsfly3",
                125,
                125,
                1,
                100
        ));

        // add ground obstacle
        groundedObstacleAnimations.add(new ImageAnimation(
                context,
                "obsground1",
                125,
                125,
                2,
                200
        ));

        groundedObstacleAnimations.add(new ImageAnimation(
                context,
                "obsground2",
                125,
                125,
                2,
                200
        ));

        groundedObstacleAnimations.add(new ImageAnimation(
                context,
                "obsground3",
                125,
                125,
                2,
                200
        ));

        // Inisialisasi gameobject obstacle
        obstacle = new GameObject(groundedObstacleAnimations.get(0), spawnXPosition, yPosition);

        // Set besar dari obstacle
        obstacle.maintainResizeByY(obstacleSize * screenRatioY);
    }

    public void update()
    {
        // Move obstacle
        if (isMoving)
            obstacleMove();

        // Update gameobject obstacle
        obstacle.update();
    }

    public void draw(Canvas canvas, Paint paint)
    {
        if (isMoving) {
            obstacle.getObjectAnimation().getCurrentFrame();

            // Draw obstacle
            canvas.drawBitmap(
                    obstacle.getObjectAnimation().getBitmapImage(),
                    obstacle.getObjectAnimation().getFrameToDraw(),
                    obstacle.getWhereToDraw(),
                    paint
            );


        }

        paint.setColor(Color.argb(255, 249, 129, 0));
    }

    private void obstacleMove()
    {
        // Change position based on gamespeed
        obstacle.positionX -= speed / fps;

        // Stop the obstacle movement if already leave the screen
        if (obstacle.positionX <= 0 - obstacle.sizeX)
        {
            obstacle.positionX = spawnXPosition;
            isMoving = false;
        }
    }

    public void setRandomObstacle()
    {
        // create a variable to randomize number
        Random rand = new Random();

        int randObstacle = rand.nextInt(3);

        // set the obstacle type based on random number
        if (randObstacle == TOP ) {
            obstacle.positionY = topObstacleYPosition;
            int randAnimation = rand.nextInt(flyingObstacleAnimations.size());
            obstacle.setObjectAnimation(flyingObstacleAnimations.get(randAnimation));
        } else if (randObstacle == MID) {
            obstacle.positionY = midObstacleYPosition;
            int randAnimation = rand.nextInt(flyingObstacleAnimations.size());
            obstacle.setObjectAnimation(flyingObstacleAnimations.get(randAnimation));
        } else if (randObstacle == BOT) {
            obstacle.positionY = bottomObstacleYPosition;
            int randAnimation = rand.nextInt(groundedObstacleAnimations.size());
            obstacle.setObjectAnimation(groundedObstacleAnimations.get(randAnimation));
        }
    }

    public void startMove()
    {
        // start movement obstacle
        isMoving = true;
    }

    public void updateSpeed(float _speed)
    {
        speed = _speed;
    }

    public void resetObstacleCondition()
    {
        obstacle.positionX = spawnXPosition;
        isMoving = false;
    }

    /* GETTER */
    public float getXPosition() { return obstacle.positionX; }
    public float getYPosition() { return obstacle.positionY; }
    public RectF getObstacleCollider() { return obstacle.getWhereToDraw(); }

}
