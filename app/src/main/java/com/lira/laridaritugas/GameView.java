package com.lira.laridaritugas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable, GestureDetector.OnGestureListener {

    Thread gameThread = null;
    SurfaceHolder ourHolder;

    // Kondisi game
    volatile boolean isPlaying;
    boolean isPaused = true;

    // Object in the game
    ArrayList<ParallaxImage> parallaxImages;
    Player player;
    Canvas canvas;
    Paint paint;
    Context context;

    // Hitung fps
    long fps;
    private long timeThisFrame;

    // Resolusi layar
    private int screenX;
    private int screenY;

    // Penghitung skor
    int score;

    // Kondisi input
    private static int MIN_DISTANCE = 150;
    GestureDetector gestureDetector;
    private float pressY, upY;

    GameView(Context context) {
        super(context);

        this.context = context;
        gestureDetector = new GestureDetector(context, this);

        // Objek Display untuk mendapatkan detail layar
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        // Simpan resolusi di objek Point
        Point size = new Point();
        display.getSize(size);

        // resolusi layar
        screenX = size.x;
        screenY = size.y;

        // canvas
        ourHolder = getHolder();
        paint = new Paint();

        // buat objek player
        player = new Player(this.context,200, 700);

        // buat objek parallax
        parallaxImages = new ArrayList<>();

        // UBAH GAMBAR BACKGROUND
        parallaxImages.add(new ParallaxImage(
                this.context,
                screenX,
                screenY,
                "bg", 0, 100, 500
        ));

        // UBAH GAMBAR GROUND
        parallaxImages.add(new ParallaxImage(
                this.context,
                screenX,
                screenY,
                "ground", 70, 100, 500
        ));

        // score ke 0
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
        // Player update
        player.update();

        // Parallax image
        for (ParallaxImage pi : parallaxImages)
            pi.update(fps);

        scoreCount();
    }

    public void newGame() {
        score = 0;
    }

    public void scoreCount() {
        // Score update
        score += 10;
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255,  26, 128, 182));

            paint.setColor(Color.argb(255,249,129,0));
            paint.setTextSize(45);

            // draw bg
            drawParallaxImage(0);


            // draw player
            player.playerAnimation.getCurrentFrame();
            canvas.drawBitmap(player.playerAnimation.getBitmapImage(),
                    player.playerAnimation.getFrameToDraw(),
                    player.getWhereToDraw(),
                    paint);

            drawParallaxImage(1);

            canvas.drawText("Score : " + score, 20,40,paint);
            canvas.drawText("Player State : " + player.getPlayerMovement(), 20,80,paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawParallaxImage(int position) {

        ParallaxImage img = parallaxImages.get(position);

        Rect fromRect1 = new Rect(0,0,img.width - img.xClip, img.height);
        Rect toRect1 = new Rect(img.xClip, img.startY, img.width, img.endY);

        Rect fromRect2 = new Rect(img.width - img.xClip,0, img.width, img.height);
        Rect toRect2 = new Rect(0, img.startY, img.xClip, img.endY);

        if (!img.reversedFirst) {
            canvas.drawBitmap(img.image, fromRect1, toRect1, paint);
            canvas.drawBitmap(img.imageReversed, fromRect2, toRect2, paint);
        } else {
            canvas.drawBitmap(img.image, fromRect2, toRect2, paint);
            canvas.drawBitmap(img.imageReversed, fromRect1, toRect1, paint);
        }
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

            // Pemain menyentuh layar
            case MotionEvent.ACTION_DOWN:
                if (isPaused) {
                    isPaused = false;
                } else {
                    pressY = motionEvent.getY();
                }
                break;

            // Pemain mengangkat sentuhan jari dari layar
            case MotionEvent.ACTION_UP:
                if (!isPaused)
                    upY = motionEvent.getY();

                if (pressY > upY && player.getPlayerMovement() != player.JUMPING)
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
