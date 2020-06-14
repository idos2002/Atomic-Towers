package com.example.atomictowers.data.game;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.atoms.AtomSequencer.AtomSequenceItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Level {
    public static final int LEVEL_ONE = 0;

    @SerializedName("level")
    public int number;

    @SerializedName("name")
    public String name;

    @SerializedName("elements")
    public List<Integer> elementsAtomicNumbers;

    @SerializedName("atomSequence")
    public List<AtomSequenceItem> atomSequence;

    @SerializedName("map")
    public LevelMap map;

    public Level(@NonNull Level level) {
        number = level.number;
        name = level.name;
        elementsAtomicNumbers = new ArrayList<>(level.elementsAtomicNumbers);
        atomSequence = new ArrayList<>(level.atomSequence);
        map = new LevelMap(level.map);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("\n{\nlevel: %d, map:\n%s}", number, map);
    }
}
