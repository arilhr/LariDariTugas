package com.lira.laridaritugas.mainmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lira.laridaritugas.playgame.GameActivity;
import com.lira.laridaritugas.R;

public class MenuActivity extends Activity {

    MediaPlayer mediaPlayer;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // high score text
        settings = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);
        String highScoreText = "HIGH SCORE : " + highScore;
        TextView highScoreTextView = findViewById(R.id.highScore);
        highScoreTextView.setText(highScoreText);

        // game sound
        mediaPlayer = MediaPlayer.create(this, R.raw.mainmenu);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1,1);
        mediaPlayer.start();

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, GameActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}