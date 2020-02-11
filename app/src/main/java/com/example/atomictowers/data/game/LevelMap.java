package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

public class LevelMap {

    /**
     * A tile on which both atoms and towers cannot exist upon.
     */
    public static final int TILE_INVALID = -1;

    public static final int TILE_EMPTY = 0;

    public static final int TILE_PATH = 1;

    @SerializedName("layout")
    private int[] mMap;

    @SerializedName("cols")
    public int cols;

    @SerializedName("rows")
    public int rows;

    public int getAtIndex(int col, int row) {
        return mMap[row * cols + col];
    }

    public int getAtPosition(Vector2 position, Vector2 canvasSize) {
        int col = (int) (position.x / canvasSize.x);
        int row = (int) (position.y / canvasSize.y);

        return getAtIndex(col, row);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                builder.append(getAtIndex(col, row)).append(", ");
            }
            builder.append('\n');
        }

        return builder.toString();
    }
}
