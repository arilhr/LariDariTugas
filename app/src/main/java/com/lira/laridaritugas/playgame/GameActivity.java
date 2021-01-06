package com.lira.laridaritugas.playgame;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        gameView.resume();
    }
}