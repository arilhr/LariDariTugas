package com.lira.laridaritugas.playgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Button;

import static com.lira.laridaritugas.playgame.GameView.getApproxXToCenterText;
import static com.lira.laridaritugas.playgame.GameView.screenRatioX;
import static com.lira.laridaritugas.playgame.GameView.screenRatioY;

public class GameEnd {

    private int screenX, screenY;

    private Rect blackScreen;

    public int finalScore = 0;

    private int fontSize = (int)(120 * screenRatioX);

    GameEnd(Context context, int sX, int sY)
    {
        screenX = sX;
        screenY = sY;

        blackScreen = new Rect(0, 0, screenX, screenY);
    }

    public void draw(Canvas canvas, Paint paint)
    {
        paint.setColor(Color.argb(60,0,0,0));

        canvas.drawRect(blackScreen, paint);

        paint.setColor(Color.argb(255,255,255,255));
        String finalScoreText = "SCORE : " + finalScore;
        float xOffset = getApproxXToCenterText(finalScoreText, fontSize, screenX, paint);
        canvas.drawText(finalScoreText, xOffset, screenY / 2f, paint);
    }


}
