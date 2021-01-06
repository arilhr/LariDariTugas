package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Button;

import static com.lira.laridaritugas.playgame.GameView.screenRatioX;
import static com.lira.laridaritugas.playgame.GameView.screenRatioY;

public class GameEnd {

    private int screenX, screenY;

    private Button restartButton;

    GameEnd(Context context, int sX, int sY)
    {
        screenX = sX;
        screenY = sY;

        restartButton = new Button(context);
    }

    public void draw(Canvas canvas, Paint paint)
    {
        paint.setColor(Color.BLUE);


    }
}
