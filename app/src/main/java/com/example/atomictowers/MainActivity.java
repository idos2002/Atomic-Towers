package com.example.atomictowers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Serves as a container for a single-activity app, using the AndroidX Navigation Component.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // TODO: Note to self: I'll implement menus on my own, so they will match the game style.
}
