package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class ImageAnimation {
    Bitmap imageAnimation;

    private int frameHeight;
    private int frameWidth;
    private int frameCount;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int animationLength;

    private Rect frameToDraw;

    ImageAnimation(Context context, String imageName, int frameWidth, int frameHeight, int frameCount, int animationLength) {
        int resID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        imageAnimation = BitmapFactory.decodeResource(context.getResources(), resID);
        imageAnimation = Bitmap.createScaledBitmap(imageAnimation, frameWidth * frameCount, frameHeight, false);

        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
        this.animationLength = animationLength;

        frameToDraw = new Rect(0,0, frameWidth, frameHeight);
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + animationLength) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= frameCount) {
                currentFrame = 0;
            }
        }

        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    public int getLength() { return frameWidth; }

    public int getHeight() { return frameHeight; }

    public Rect getFrameToDraw() {
        return frameToDraw;
    }

    public Bitmap getBitmapImage() { return imageAnimation; }
}
