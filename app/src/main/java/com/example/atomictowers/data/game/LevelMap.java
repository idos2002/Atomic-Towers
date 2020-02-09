package com.example.atomictowers.data.game;

import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

public class LevelMap {

    /**
     * A tile on which both atoms and towers cannot exist upon.
     */
    public static final int TILE_INVALID = -1;

    public static final int TILE_EMPTY = 0;

    public static final int TILE_PATH = 1;

    @SerializedName("level")
    public int level = 0;

    @SerializedName("map")
    public int[] map;

    @SerializedName("cols")
    public int cols;

    @SerializedName("rows")
    public int rows;

    private int getAtIndex(int col, int row) {
        return map[row * cols + col];
    }

    public int getAt(Vector2 position, Vector2 canvasSize) {
        int col = (int) (position.x / canvasSize.x);
        int row = (int) (position.y / canvasSize.y);

        return getAtIndex(col, row);
    }
}
