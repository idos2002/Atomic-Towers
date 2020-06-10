package com.example.atomictowers.data.game;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Level {
    public static final int LEVEL_ONE = 0;

    @SerializedName("level")
    public int number;

    // TODO: Add material implementation
//    @SerializedName("material")
//    public Material material;

    @SerializedName("map")
    public LevelMap map;

    public Level(@NonNull Level level) {
        number = level.number;
        map = new LevelMap(level.map);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("\n{\nlevel: %d, map:\n%s}", number, map);
    }
}
