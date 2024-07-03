package com.example.warehouse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private int waktu_loading = 4000; // 4000 ms = 4 seconds
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Initialize the MediaPlayer and start playing the sound
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_sound);
        mediaPlayer.start();

        // Delay for the specified loading time, then start the MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(home);
                finish();

                // Stop and release the MediaPlayer when the splash screen is finished
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        }, waktu_loading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the MediaPlayer is released if the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
