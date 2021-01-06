package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import static com.lira.laridaritugas.playgame.GameView.fps;

public class ParallaxImage {

    // bitmap for image and the reversed
    Bitmap image, imageReversed;

    // image size
    int width;
    int height;

    // parallax condition
    boolean reversedFirst;

    // gamespeed
    float speed;

    // image x-position
    float xClip;

    // image y-position
    float startY;
    float endY;

    ParallaxImage(Context context, float screenWidth, float screenHeight, String bitmapName, float sY, float eY, float s, boolean isReversed) {

        // find file based on name file
        int resID = context.getResources().getIdentifier(bitmapName, "drawable", context.getPackageName());

        // decode bitmap image file
        image = BitmapFactory.decodeResource(context.getResources(), resID);

        // initialize
        reversedFirst = false;
        xClip = 0;

        startY = (sY * (screenHeight / 100));
        endY = (eY * (screenHeight / 100));
        speed = s;

        // initialize image bitmap
        image =  Bitmap.createScaledBitmap(image, (int)screenWidth, (int)(endY - startY), true);

        if (isReversed)
        {
            width = image.getWidth();
            height = image.getHeight();

            // reverse image bitmap
            Matrix matrix = new Matrix();
            matrix.setScale(-1, 1);

            // initialize reversed image
            imageReversed = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        }
        else
        {
            width = image.getWidth();
            height = image.getHeight();

            // reverse image bitmap
            Matrix matrix = new Matrix();
            matrix.setScale(1, 1);

            // initialize reversed image
            imageReversed = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        }

    }

    public void update() {
        // move the image
        xClip -= speed / fps;

        // swap image with reversed if xclip leave the screen
        if (xClip >= width) {
            xClip = 0;
            reversedFirst = !reversedFirst;
        } else if (xClip <= 0) {
            xClip = width;
            reversedFirst = !reversedFirst;
        }
    }

    public void draw(Canvas canvas, Paint paint) {

        Rect fromRect1 = new Rect(0,0,(int)(width - xClip), height);
        Rect toRect1 = new Rect((int)xClip, (int)startY, width, (int)endY);

        Rect fromRect2 = new Rect((int)(width - xClip),0, width, height);
        Rect toRect2 = new Rect(0, (int)startY, (int)xClip, (int)endY);

        // draw parallax image
        if (!reversedFirst) {
            canvas.drawBitmap(image, fromRect1, toRect1, paint);
            canvas.drawBitmap(imageReversed, fromRect2, toRect2, paint);
        } else {
            canvas.drawBitmap(image, fromRect2, toRect2, paint);
            canvas.drawBitmap(imageReversed, fromRect1, toRect1, paint);
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
