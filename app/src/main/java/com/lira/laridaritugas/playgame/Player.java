package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

import static com.lira.laridaritugas.playgame.GameView.fps;
import static com.lira.laridaritugas.playgame.GameView.screenRatioY;

public class Player {

    long time;

    // create gameobject player
    private GameObject playerObject;

    // name player file
    private String playerRun = "run";
    private String playerSlide = "slide";

    // player state animation
    private ImageAnimation playerJumpAnimation;
    private ImageAnimation playerSlideAnimation;
    private ImageAnimation playerRunAnimation;

    // player height
    private float playerHeight = 400 * screenRatioY;
    private float playerSlideHeight = 190 * screenRatioY;

    // default y-axis position
    private float defaultYPosition;

    // player jump stats
    private float jumpPosition;
    private float jumpHeight = playerHeight + 10f;
    private float jumpSpeed = 1500;
    private long jumpTime = 1000;
    private long startJumpTime = 0;

    // player slide stats
    private long startSlideTime = 0;
    private long slideTime = 1000;


    // player state
    public final int RUNNING = 0;
    public final int JUMPING = 1;
    public final int FALLING = 2;
    public final int SLIDING = 3;

    // current player state
    private int currentPlayerState = RUNNING;

    // player collider
    private RectF playerCollider;
    private float leftCollider;
    private float topCollider;
    private float rightCollider;
    private float bottomCollider;
    private float slideColliderX = 322 * screenRatioY;
    private float runColliderX = 60 * screenRatioY;

    // sound
    SoundPool soundPool;
    int loseID = -1;
    int jumpID = -1;
    int slideID = -1;

    // constructor
    Player(Context context, float xPosition, float yPosition) {
        // add player animation from every state
        playerJumpAnimation = new ImageAnimation(
                context,
                playerRun,
                1600,
                200,
                8,
                50);

        playerRunAnimation = new ImageAnimation(
                context,
                playerRun,
                1600,
                200,
                8,
                50);

        playerSlideAnimation = new ImageAnimation(
                context,
                playerSlide,
                774,
                120,
                3,
                70);

        // inisialisasi player gameobject
        playerObject = new GameObject(playerRunAnimation, xPosition, yPosition);

        // set player height
        playerObject.maintainResizeByY(playerHeight * screenRatioY);

        // player run collider
        changeCollider(
                playerObject.sizeX / 2f - runColliderX,
                playerObject.sizeY,
                playerObject.sizeX / 2f + runColliderX,
                0);

        // inisialisasi default and jump y-position
        this.defaultYPosition = playerObject.positionY;
        this.jumpPosition = defaultYPosition - jumpHeight;


        // initialize sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            // Buat objek dari dua kelas yang dibutuhkan
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("Lose.ogg");
            loseID = soundPool.load(descriptor, 0);

            // Muat efek suara kita di memory yang siap digunakan
            descriptor = assetManager.openFd("Jump.ogg");
            jumpID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("Slide.ogg");
            slideID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Tampilkan pesan error ke konsol
            Log.e("error:", "failed to load sound file");
        }
    }

    public void update() {
        // count time
        time = System.currentTimeMillis();

        // move player
        playerMovement();


        // update player gameobject
        playerObject.update();

        // update player collider
        UpdatePlayerCollider();
    }

    public void draw(Canvas canvas, Paint paint)
    {
        // debug check collider
        // paint.setColor(Color.argb(255,249,129,0));
        // canvas.drawRect(playerCollider, paint);

        // draw player
        canvas.drawBitmap(
                playerObject.getObjectAnimation().getBitmapImage(),
                playerObject.getObjectAnimation().getFrameToDraw(),
                playerObject.getWhereToDraw(),
                paint
        );
    }

    public void startAnimation()
    {
        // start player animation
        playerObject.getObjectAnimation().getCurrentFrame();
    }

    private void playerMovement()
    {
        // set current player state
        if (currentPlayerState == RUNNING)
        {
            playerRun();
        }
        else if (currentPlayerState == JUMPING)
        {
            playerJump();
        }
        else if (currentPlayerState == FALLING)
        {
            playerFall();
        }
        else if (currentPlayerState == SLIDING)
        {
            playerSlide();
        }
    }

    private void playerJump()
    {
        // change player animation to jump state
        if (playerObject.getObjectAnimation() != playerJumpAnimation)
        {
            playerObject.setObjectAnimation(playerJumpAnimation);
            playerObject.maintainResizeByY(playerHeight);
            changeCollider(
                    playerObject.sizeX / 2f - runColliderX,
                    playerObject.sizeY,
                    playerObject.sizeX / 2f + runColliderX,
                    0);
            soundPool.play(jumpID, 1, 1, 0, 0, 1);
        }

        // change player y-position to jump position
        if (playerObject.positionY > jumpPosition)
        {
            playerObject.positionY -= jumpSpeed / fps;
            startJumpTime = time;
        }
        else
        {
            // if jump time is run out, player start falling
            if (time >= startJumpTime + jumpTime)
            {
                setPlayerMoving(FALLING);
            }
        }
    }

    private void playerFall()
    {
        // change player position to default position and change state to run
        if (playerObject.positionY < defaultYPosition)
        {
            playerObject.positionY += jumpSpeed / fps;
            if (playerObject.positionY >= defaultYPosition)
                playerObject.positionY = defaultYPosition;
        }
        else
        {
            setPlayerMoving(RUNNING);
        }
    }

    private void playerRun()
    {
        // change player animation to running
        if (playerObject.getObjectAnimation() != playerRunAnimation)
        {
            playerObject.setObjectAnimation(playerRunAnimation);
            playerObject.maintainResizeByY(playerHeight);
            changeCollider(
                    playerObject.sizeX / 2f - runColliderX,
                    playerObject.sizeY,
                    playerObject.sizeX / 2f + runColliderX,
                    0);

        }
    }

    private void playerSlide()
    {
        // change player animation to sliding
        if (playerObject.getObjectAnimation() != playerSlideAnimation)
        {
            startSlideTime = time;
            playerObject.setObjectAnimation(playerSlideAnimation);
            playerObject.positionY = defaultYPosition;
            playerObject.maintainResizeByY(playerSlideHeight);
            changeCollider(
                    20f/100f * playerObject.sizeX,
                    playerObject.sizeY,
                    20f/100f * playerObject.sizeX + slideColliderX,
                    0);
            soundPool.play(slideID, 1, 1, 0, 0, 1);
        }

        // sliding timer
        // if sliding time is run out, player state change to running
        if (time >= startSlideTime + slideTime)
        {
            setPlayerMoving(RUNNING);
        }
    }

    public void playerLose()
    {
        soundPool.play(loseID, 1, 1, 0,0,1);
    }

    private void UpdatePlayerCollider()
    {
            playerCollider = new RectF(
                    playerObject.positionX + leftCollider,
                    playerObject.positionY - topCollider,
                    playerObject.positionX + rightCollider,
                    playerObject.positionY + bottomCollider
            );
    }

    private void changeCollider(float left, float top, float right, float bottom)
    {
        leftCollider = left;
        topCollider = top;
        rightCollider = right;
        bottomCollider = bottom;
    }

    public void resetPlayerState()
    {
        playerObject.positionY = defaultYPosition;
        setPlayerMoving(RUNNING);
    }

    /* SETTER */
    public void setPlayerMoving(int movement) {
        currentPlayerState = movement;
    }

    /* GETTER */
    public int getPlayerMovement() { return currentPlayerState; }
    public float getPlayerHeight() { return playerHeight; }
    public float getJumpHeight() { return jumpHeight; }
    public RectF getPlayerCollider() { return playerCollider; }

}
