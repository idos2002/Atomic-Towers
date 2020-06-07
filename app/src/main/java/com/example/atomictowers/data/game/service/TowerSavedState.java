package com.example.atomictowers.data.game.service;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TowerSavedState implements Serializable {
    public TowerSavedState() {
    }

    public TowerSavedState(@NonNull String towerTypeKey, @NonNull Vector2 position) {
        this.towerTypeKey = towerTypeKey;
        this.position = position;
    }

    @SerializedName("towerTypeKey")
    public String towerTypeKey;

    @SerializedName("position")
    public Vector2 position;
}
