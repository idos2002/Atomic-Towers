package com.example.atomictowers.components;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

public interface Component {

    int getId();

    void update();

    void draw(@NonNull Canvas canvas);
}