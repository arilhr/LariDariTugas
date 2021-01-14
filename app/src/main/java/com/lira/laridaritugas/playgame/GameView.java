package com.lira.laridaritugas.playgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import com.lira.laridaritugas.R;
import com.lira.laridaritugas.mainmenu.MenuActivity;

import java.io.IOException;
import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable, GestureDetector.OnGestureListener {

    Thread gameThread = null;
    SurfaceHolder ourHolder;
    public Typeface defaultTypeface;

    // game condition
    volatile boolean isPlaying;
    public static boolean isPaused = true;

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
    boolean newHighscore = false;
    int highScore;
    SharedPreferences settings;

    // game speed
    private float defaultGameSpeed = 450;
    private float updatedGameSpeed;

    // game prestart countdown
    private CountDownTimer prestartCountdown;
    private long startTime = 4000;
    private long remainingTime;
    String timerText = "" + startTime / 1000;

    // game state
    public int gameState;
    public final int PRESTART = 0;
    public final int GAMESTART = 1;
    public final int GAMEEND = 2;
    public final int GAMEPAUSE = 3;
    private int stateBefore;

    // gameend button
    ImageAnimation restartButton;
    ImageAnimation backMenuButton;
    RectF restartButtonPos;
    RectF backMenuButtonPos;

    // backsound game
    MediaPlayer mediaplayer;
    int soundingameID = -1;

    // input gesture
    private int MIN_DISTANCE = 150;
    GestureDetector gestureDetector;
    private float pressY, upY;

    GameView(Context context) {
        super(context);

        this.context = context;

        // initialize typeface
        defaultTypeface = ResourcesCompat.getFont(context, R.font.forwa);

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

        // high score
        settings = context.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);

        // gamestate
        gameState = PRESTART;

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

        // game button
        restartButton = new ImageAnimation(context,
                "restartbutton",
                259,
                186,
                1,
                0);

        backMenuButton = new ImageAnimation(context,
                "menubutton",
                259,
                186,
                1,
                0);

        // initialize sound
        mediaplayer = MediaPlayer.create(this.context, R.raw.ingame);
        mediaplayer.setLooping(true);
        mediaplayer.setVolume(1f, 1f);

        // start new game
        newGame();
        startCountdown(startTime);
        mediaplayer.start();
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

        if (gameState == GAMESTART)
        {
            // update obstacle
            obstacleSpawner.update();

            // update speed
            speedUp();

            // counting score
            scoreCount();

            for (Obstacle ob : obstacleSpawner.obstacleObject)
                if (RectF.intersects(player.getPlayerCollider(), ob.getObstacleCollider())) {
                    lose();
                }
        }

        // update parallax image
        for (ParallaxImage pi : parallaxImages)
            pi.update();


    }



    /* GAME STATE */
    private void lose()
    {
        if (score > highScore)
        {
            setHighScore(score);
            newHighscore = true;
        } else {
            newHighscore = false;
        }

        player.playerLose();
        gameState = GAMEEND;
        isPaused = true;
    }

    public void startGame()
    {
        highScore = settings.getInt("HIGH_SCORE", 0);
        gameState = GAMESTART;
    }


    public void newGame()
    {
        // reset state
        score = 0;
        updatedGameSpeed = defaultGameSpeed;
        obstacleSpawner.resetObstacleSpawner(defaultGameSpeed);
        player.resetPlayerState();

        gameState = PRESTART;
        isPaused = false;
    }

    public void restartGame()
    {
        newGame();
        startCountdown(startTime);
    }

    private void gamePausing()
    {
        isPaused = true;
        stateBefore = gameState;
        gameState = GAMEPAUSE;
    }

    /* END GAME STATE SETTING */

    /* GAME COUNTDOWN */
    private void startCountdown(long longTime)
    {
        prestartCountdown = new CountDownTimer(longTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText = "" + millisUntilFinished/1000;
                remainingTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                startGame();
            }
        };

        prestartCountdown.start();
    }
    /* END GAME COUNTDOWN */

    /* DRAW VIEW GAME */
    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,  26, 128, 182));
            paint.setColor(Color.argb(255,249,129,0));
            paint.setTypeface(defaultTypeface);

            // draw background
            parallaxImages.get(0).draw(canvas, paint);

            // draw player
            if (!isPaused) {
                player.startAnimation();
            }

            player.draw(canvas, paint);

            // draw obstacle
            obstacleSpawner.draw(canvas, paint);

            if (gameState == GAMESTART)
                drawGameStartHUD(canvas, paint);

            // game pre start
            if (gameState == PRESTART)
                drawGamePreStart(canvas, paint);

            if (gameState == GAMEPAUSE)
                drawGamePause(canvas, paint);

            // lose display
            if (gameState == GAMEEND)
                drawGameEnd(canvas, paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameStartHUD(Canvas canvas, Paint paint)
    {
        int fontSize = 30;

        paint.setColor(Color.argb(255,0,0,0));
        paint.setTextSize(fontSize);
        if (highScore > score)
            canvas.drawText("High Score : " + highScore, 30,80 * screenRatioY, paint);
        else
            canvas.drawText("High Score : " + score, 30,80 * screenRatioY, paint);

        canvas.drawText("Score : " + score, 30,165 * screenRatioY, paint);

        // show fps
        canvas.drawText("FPS : " + fps, screenX / 2f,80 * screenRatioY, paint);
    }


    private void drawGamePause(Canvas canvas, Paint paint)
    {
        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        int fontSize = 60;
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(fontSize);
        String pauseText = "touch to resume";
        float xOffset = getApproxXToCenterText(pauseText, fontSize, screenX, paint);
        canvas.drawText(pauseText, xOffset, screenY / 2f, paint);
    }

    private void drawGameEnd(Canvas canvas, Paint paint)
    {
        /* BACKGROUND */
        paint.setColor(Color.argb(150,255,255,255));
        canvas.drawRect(0, 0, screenX, screenY, paint);
        /* END BACKGROUND */

        /* CONTENT */
        int fontSize;
        float xOffset;

        // NEW HIGHSCORE
        if (newHighscore)
        {
            // rect
            float rectWidth = 804 * screenRatioX;
            float rectHeight = 135 * screenRatioY;
            float rectPositionY = 447 * screenRatioY;
            paint.setColor(Color.argb(255,0,0,0));
            canvas.drawRect(
                    (screenX / 2f) - (rectWidth / 2f),
                    (rectPositionY - rectHeight),
                    (screenX / 2f) + (rectWidth / 2f),
                    (rectPositionY),
                    paint
            );

            // text
            float newHighScoreTextPosition = 440 * screenRatioY;
            fontSize = (int)(70 * screenRatioY);
            paint.setColor(Color.argb(255,249,180,67));
            String highScoreText = "NEW HIGHSCORE";
            xOffset = getApproxXToCenterText(highScoreText, fontSize, screenX, paint);
            canvas.drawText(highScoreText, xOffset, newHighScoreTextPosition, paint);
        }

        // SCORE
        float scorePosition = 618 * screenRatioY;
        fontSize = (int)(100 * screenRatioY);
        paint.setColor(Color.argb(255,0,0,0));
        String finalScoreText = "SCORE : " + score;
        xOffset = getApproxXToCenterText(finalScoreText, fontSize, screenX, paint);
        canvas.drawText(finalScoreText, xOffset, scorePosition, paint);
        /* END CONTENT */

        /* BUTTON */
        float positionY = screenY - (100 * screenRatioY);
        restartButtonPos = new RectF((screenX/2f) - (restartButton.getLength() + 100f * screenRatioX),
                positionY - restartButton.getHeight(),
                (screenX/2f) - (100f * screenRatioX),
                positionY
                );
        backMenuButtonPos = new RectF((screenX/2f) + (100f * screenRatioX),
                positionY - restartButton.getHeight(),
                (screenX/2f) + (100f * screenRatioX) + backMenuButton.getLength(),
                positionY
        );

        // RESTART BUTTON
        canvas.drawBitmap(restartButton.getBitmapImage(), restartButton.getFrameToDraw(), restartButtonPos, paint);

        // BACK MENU BUTTON
        canvas.drawBitmap(backMenuButton.getBitmapImage(), backMenuButton.getFrameToDraw(), backMenuButtonPos, paint);
        /* END BUTTON */
    }

    private void drawGamePreStart(Canvas canvas, Paint paint)
    {
        int fontSize;
        float xOffset;

        /* COUNTDOWN TEXT */
        fontSize = (int)(120 * screenRatioY);
        paint.setColor(Color.argb(255, 0, 0, 0));
        xOffset = getApproxXToCenterText(timerText, fontSize, screenX, paint);
        canvas.drawText(timerText, xOffset, screenY / 2f, paint);
        /* END COUNTDOWN TEXT */

        /* HELP BG */
        float rectWidth = 520 * screenRatioX;
        float rectHeight = 185 * screenRatioY;
        float rectPositionY = screenY;
        paint.setColor(Color.argb(150, 255, 255, 255));
        canvas.drawRect(
                (screenX / 2f) - (rectWidth / 2f),
                rectPositionY - rectHeight,
                (screenX / 2f) + (rectWidth / 2f),
                rectPositionY,
                paint
        );
        /* END HELP BG */

        /* HELP CONTENT */
        // text
        String swipeUp = "SWIPE UP TO JUMP";
        String swipeDown = "SWIPE DOWN TO SLIDE";
        float swipeUpTextPos = 960 * screenRatioY;
        float swipeDownTextPos = 1035 * screenRatioY;
        fontSize = (int)(35 * screenRatioY);
        paint.setColor(Color.argb(255, 0, 0, 0));
        xOffset = getApproxXToCenterText(swipeUp, fontSize, screenX, paint);
        canvas.drawText(swipeUp, xOffset, swipeUpTextPos, paint);
        xOffset = getApproxXToCenterText(swipeDown, fontSize, screenX, paint);
        canvas.drawText(swipeDown, xOffset, swipeDownTextPos, paint);

        // line
        float lineWidth = 470 * screenRatioX;
        float lineHeight = 8 * screenRatioY;
        float linePositionY = 980 * screenRatioY;
        canvas.drawRect(
                (screenX / 2f) - (lineWidth / 2f),
                (linePositionY) - (lineHeight),
                (screenX / 2f) + (lineWidth / 2f),
                linePositionY,
                paint
        );
        /* END HELP CONTENT */
    }

    /* END DRAW VIEW GAME STATE */

    /* GAME PROGRESSION */
    public void scoreCount() {
        // update score
        score += 1;
    }

    public void setHighScore(int _score)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("HIGH_SCORE", _score);
        editor.commit();

        highScore = settings.getInt("HIGH_SCORE",0);
    }

    private void speedUp()
    {
        if (score < 2000)
        {
            updatedGameSpeed += 1 / 5f;
        }
        else if (score < 5000)
        {
            updatedGameSpeed += 1 / 10f;
        }
        else
        {
            updatedGameSpeed += 1 / 20f;
        }


        for (ParallaxImage p : parallaxImages)
            p.setSpeed(updatedGameSpeed);

        obstacleSpawner.updateSpeed(updatedGameSpeed);
    }

    /* END GAME PROGRESSION */

    public static float getApproxXToCenterText(String text, int fontSize, int widthToFitStringInto, Paint p) {
        Rect bounds = new Rect();
        p.setTextSize(fontSize);
        p.getTextBounds(text, 0, text.length(), bounds);
        float xOffset = widthToFitStringInto / 2f - bounds.width() / 2f;

        return xOffset;
    }

    public void pause() {
        isPlaying = false;
        if (gameState == PRESTART)
        {
            prestartCountdown.cancel();
        }
        if (gameState != GAMEEND)
        {
            gamePausing();
        }
        mediaplayer.pause();

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // error
        }
    }

    public void resume() {
        isPlaying = true;
        mediaplayer.start();

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void destroy()
    {
        mediaplayer.stop();
        mediaplayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        gestureDetector.onTouchEvent(motionEvent);
        int x = (int)motionEvent.getX(0);
        int y = (int)motionEvent.getY(0);

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // player touch the screen
            case MotionEvent.ACTION_DOWN:
                if (gameState == GAMEPAUSE) {
                    isPaused = false;
                    gameState = stateBefore;
                    if (gameState == PRESTART)
                    {
                        startCountdown(remainingTime);
                    }
                } else {
                    pressY = motionEvent.getY();
                }

                if (gameState == GAMEEND)
                {
                    if (backMenuButtonPos.contains(x,y)) {
                        context.startActivity(new Intent(context, MenuActivity.class));
                    }

                    if (restartButtonPos.contains(x,y)) {
                        restartGame();
                    }
                }

                break;

            // player stop touching the screen
            case MotionEvent.ACTION_UP:
                if (!isPaused)
                    upY = motionEvent.getY();

                if (pressY > upY && player.getPlayerMovement() != player.JUMPING && player.getPlayerMovement() != player.FALLING) {
                    if (pressY - upY > MIN_DISTANCE)
                        player.setPlayerMoving(player.JUMPING);
                }
                else if (pressY < upY && player.getPlayerMovement() != player.SLIDING)
                {
                    if (upY - pressY > MIN_DISTANCE)
                        player.setPlayerMoving(player.SLIDING);
                }


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
