package com.example.mbkz;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import java.util.Random;

public class MyApp extends Application {
    public MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.backgound_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.4f,0.4f);
        mediaPlayer.setOnPreparedListener(mp -> {
            startMusic();
        });
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying() && isMusicAllowed()) {
            mediaPlayer.start();
        }
    }

    public void startMusic() {
        if(!isMusicAllowed())
            return;

        int duration = mediaPlayer.getDuration();
        Random random = new Random();
        int randomPosition = random.nextInt(duration);
        mediaPlayer.seekTo(randomPosition);
        mediaPlayer.start();
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }


    private boolean isMusicAllowed() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("setting", MODE_PRIVATE);
        return  sharedPreferences.getBoolean("music", true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mediaPlayer.release();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            mediaPlayer.pause();
        }
    }
}