package com.example.atomictowers.data.game.game_state;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AtomSavedState implements Serializable {
    public AtomSavedState() {
    }

    public AtomSavedState(int atomicNumber, int strength, int pathIndex, @NonNull Vector2 position,
                          boolean isLastAtom) {
        this.atomicNumber = atomicNumber;
        this.strength = strength;
        this.pathIndex = pathIndex;
        this.position = position;
        this.isLastAtom = isLastAtom;
    }

    @SerializedName("atomicNumber")
    public int atomicNumber;

    @SerializedName("strength")
    public int strength;

    @SerializedName("pathIndex")
    public int pathIndex;

    @SerializedName("position")
    public Vector2 position;

    @SerializedName("isLastAtom")
    public boolean isLastAtom = false;
}
