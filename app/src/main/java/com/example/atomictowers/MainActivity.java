package com.example.atomictowers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Serves as a container for a single-activity app, using the AndroidX Navigation Component.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final float DEFAULT_MUSIC_VOLUME = 0.75f;

    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.memories_of_better_times__gurdonark);
        mediaPlayer.setLooping(true);
        setMusicVolume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void setMusicVolume() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        float musicVolume = sharedPref.getFloat(
            getString(R.string.key_music_volume), DEFAULT_MUSIC_VOLUME);
        mediaPlayer.setVolume(musicVolume, musicVolume);
    }
}
