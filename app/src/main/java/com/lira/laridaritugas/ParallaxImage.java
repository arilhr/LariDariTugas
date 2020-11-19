package com.lira.laridaritugas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ParallaxImage {

    Bitmap image;
    Bitmap imageReversed;

    int width;
    int height;
    boolean reversedFirst;
    float speed;

    int xClip;
    int startY;
    int endY;

    ParallaxImage(Context context, float screenWidth, float screenHeight, String bitmapName, int sY, int eY, float s) {

        // cari id file gambar menggunakan nama file
        int resID = context.getResources().getIdentifier(bitmapName, "drawable", context.getPackageName());

        // masukkan gambar sesuai id yang sudah dicari
        image = BitmapFactory.decodeResource(context.getResources(), resID);

        reversedFirst = false;
        xClip = 0;

        startY = (int)(sY * (screenHeight / 100));
        endY = (int)(eY * (screenHeight / 100));
        speed = s;

        image =  Bitmap.createScaledBitmap(image, (int)screenWidth, (endY - startY), true);

        width = image.getWidth();
        height = image.getHeight();

        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        imageReversed = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
    }

    public void update(long fps) {
        xClip -= speed / fps;
        if (xClip >= width) {
            xClip = 0;
            reversedFirst = !reversedFirst;
        } else if (xClip <= 0) {
            xClip = width;
            reversedFirst = !reversedFirst;
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
