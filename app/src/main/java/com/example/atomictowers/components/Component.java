package com.example.atomictowers.components;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

public interface Component {

    void update();

    void draw(@NonNull Canvas canvas);
}