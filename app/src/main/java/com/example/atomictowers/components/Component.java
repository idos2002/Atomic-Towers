package com.example.atomictowers.components;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.util.Vector2;

public abstract class Component {

    private final Game mGame;
    private final int mId;

    public Component(@NonNull Game game, int id, @Nullable Object data) {
        mGame = game;
        mId = id;
    }

    @NonNull
    public Game getGame() {
        return mGame;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public abstract Vector2 getPosition();

    public abstract void update();

    public abstract void draw(@NonNull Canvas canvas);

    public void destroy() {
        mGame.removeComponent(mId);
    }
}