package com.lira.laridaritugas.playgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable, GestureDetector.OnGestureListener {

    Thread gameThread = null;
    SurfaceHolder ourHolder;

    // game condition
    volatile boolean isPlaying;
    boolean isPaused = true;

    // array of parallax image
    ArrayList<ParallaxImage> parallaxImages;

    // player and obstacle object
    Player player;
    ObstacleSpawner obstacleSpawner;

    // canvas
    Canvas canvas;
    Paint paint;
    Context context;

    // count the fps
    public static long fps;
    private long timeThisFrame;

    // screen resolution
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;

    // score
    int score;

    // game speed
    private float defaultGameSpeed = 500;
    private float updatedGameSpeed;

    // game end
    private GameEnd gameEnd;

    // game state
    private int gameState = 0;
    private final int PRESTART = 0;
    private final int GAMESTART = 1;
    private final int GAMEEND = 2;

    // input gesture
    private static int MIN_DISTANCE = 150;
    GestureDetector gestureDetector;
    private float pressY, upY;

    GameView(Context context) {
        super(context);

        this.context = context;

        // initialize input gesture detector
        gestureDetector = new GestureDetector(context, this);

        // get screen resolution
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenY = size.y;

        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        // initialize canvas
        ourHolder = getHolder();
        paint = new Paint();

        // initialize game speed
        updatedGameSpeed = defaultGameSpeed;

        // initialize player object
        player = new Player(
                this.context,
                200,
                95f / 100f * screenY
        );

        // initialize array parallax image
        parallaxImages = new ArrayList<>();

        // add background image to array parallax image
        parallaxImages.add(new ParallaxImage(
                this.context,
                screenX,
                screenY,
                "bgplay", 0, 100, updatedGameSpeed,
                false
        ));

        // initialize obstacle object
        obstacleSpawner = new ObstacleSpawner(
                this.context,
                screenX,
                95f / 100f * screenY,
                player.getPlayerHeight(),
                player.getJumpHeight(),
                updatedGameSpeed
        );

        gameEnd = new GameEnd(this.context, screenX, screenY);

        // start new game
        newGame();
    }

    @Override
    public void run() {
        while (isPlaying) {
            long startFrameTime = System.currentTimeMillis();

            if (!isPaused) {
                update();
            }

            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000/timeThisFrame;
            }
        }
    }

    public void update() {

        // update player
        player.update();

        // update obstacle
        obstacleSpawner.update();

        // counting score
        scoreCount();

        // update parallax image
        for (ParallaxImage pi : parallaxImages)
            pi.update();

        for (Obstacle ob : obstacleSpawner.obstacleObject)
            if (RectF.intersects(player.getPlayerCollider(), ob.getObstacleCollider())) {
                lose();
            }

    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,  26, 128, 182));
            paint.setColor(Color.argb(255,249,129,0));

            // draw background
            parallaxImages.get(0).draw(canvas, paint);

            // draw player
            if (!isPaused) {
                player.startAnimation();
            }

            player.draw(canvas, paint);


            // draw obstacle
            obstacleSpawner.draw(canvas, paint);

            // lose display
            if (gameState == GAMEEND)
            {
                gameEnd.draw(canvas, paint);
            }


            paint.setColor(Color.argb(255,249,129,0));
            paint.setTextSize(45);
            canvas.drawText("Score : " + score, 20,40,paint);
            canvas.drawText("FPS : " + fps, 20,80,paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void lose()
    {

        gameState = GAMEEND;
        isPaused = true;

    }

    public void newGame()
    {
        // set score to 0
        score = 0;
        obstacleSpawner.resetObstacleSpawner(defaultGameSpeed);
        player.resetPlayerState();
    }

    public void scoreCount() {
        // update score
        score += 1;
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // error
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        gestureDetector.onTouchEvent(motionEvent);

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // player touch the screen
            case MotionEvent.ACTION_DOWN:
                if (isPaused) {
                    isPaused = false;
                } else {
                    pressY = motionEvent.getY();
                }
                break;

            // player stop touching the screen
            case MotionEvent.ACTION_UP:
                if (!isPaused)
                    upY = motionEvent.getY();

                if (pressY > upY && player.getPlayerMovement() != player.JUMPING && player.getPlayerMovement() != player.FALLING)
                    player.setPlayerMoving(player.JUMPING);
                else if ((pressY < upY && player.getPlayerMovement() != player.SLIDING))
                    player.setPlayerMoving(player.SLIDING);
                break;

        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}
