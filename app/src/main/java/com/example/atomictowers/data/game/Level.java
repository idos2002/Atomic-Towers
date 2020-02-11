package com.example.atomictowers.data.game;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Level {

    @SerializedName("level")
    public int number = 0;

    // TODO: Add material implementation
//    @SerializedName("material")
//    public Material material;

    @SerializedName("map")
    public LevelMap map;

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("\n{\nlevel: %d, map:\n%s}", number, map);
    }
}
