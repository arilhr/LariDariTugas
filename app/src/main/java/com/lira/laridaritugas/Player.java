package com.lira.laridaritugas;

import android.content.Context;
import android.graphics.RectF;

public class Player {

    private ImageAnimation playerJumpAnimation;
    private ImageAnimation playerSlideAnimation;
    private ImageAnimation playerRunAnimation;
    public ImageAnimation playerAnimation;

    private float xPosition = 100;
    private float yPosition = 200;

    private RectF whereToDraw;

    private float jumpPosition;
    private float runPosition;
    private float slidePosition;

    private long timePerMovement = 200;
    private boolean isMovementChanged = false;
    long timeLastMovementChanged = 0;

    public final int RUNNING = 0;
    public final int JUMPING = 1;
    public final int SLIDING = 2;

    private int playerMovement = RUNNING;

    Player(Context context, float xPosition, float yPosition) {
        playerJumpAnimation = new ImageAnimation(context,
                "bob",
                60,
                104,
                5,
                100);

        playerRunAnimation = new ImageAnimation(context,
                "bob",
                60,
                104,
                5,
                100);

        playerSlideAnimation = new ImageAnimation(context,
                "bob",
                60,
                104,
                5,
                100);

        playerAnimation = playerRunAnimation;

        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.jumpPosition = yPosition - 200f;
        this.runPosition = yPosition;
        this.slidePosition = yPosition - 100f;

        whereToDraw = new RectF();
        setWhereToDraw();
    }

    public void update() {
        long time = System.currentTimeMillis();

        playerMovement(time);
    }

    private void playerMovement(long time) {
        if (isMovementChanged) {
            timeLastMovementChanged = time;
            if (playerMovement == JUMPING) {
                // atur posisi player
                yPosition = jumpPosition;

                // ubah animasi ke jump
                playerAnimation = playerJumpAnimation;

            } else if (playerMovement == SLIDING) {
                // atur posisi player
                yPosition = slidePosition;

                // ubah animasi ke slide
                playerAnimation = playerSlideAnimation;

            } else if (playerMovement == RUNNING) {
                // atur posisi player
                yPosition = runPosition;

                // ubah animasi ke run
                playerAnimation = playerRunAnimation;
            }
            setWhereToDraw();
            isMovementChanged = false;
        }

        if (time > timeLastMovementChanged + timePerMovement && playerMovement != RUNNING) {
            setPlayerMoving(RUNNING);
        }
    }

    private void setWhereToDraw() {
        whereToDraw.set(xPosition,
                yPosition,
                xPosition + playerAnimation.getLength(),
                yPosition + playerAnimation.getHeight());
    }

    public RectF getWhereToDraw() { return whereToDraw; }

    public int getPlayerMovement() { return playerMovement; }

    public void setPlayerMoving(int movement) {
        playerMovement = movement;
        isMovementChanged = true;
    }
}
