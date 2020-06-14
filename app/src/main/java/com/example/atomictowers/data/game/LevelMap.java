package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LevelMap {
    private static final String TAG = LevelMap.class.getSimpleName();

    public static final int TILE_EMPTY = 0;
    public static final int TILE_PATH = 1;
    public static final int TILE_TOWER = 2;

    @SerializedName("path")
    private List<Vector2> mPath;

    private transient int[] mMap;

    @SerializedName("cols")
    public int cols;

    @SerializedName("rows")
    public int rows;

    public LevelMap(@NonNull LevelMap map) {
        cols = map.cols;
        rows = map.rows;
        mPath = new ArrayList<>(map.mPath);
    }

    @NonNull
    public List<Vector2> getPath() {
        return mPath;
    }

    @NonNull
    public int[] getMap() {
        if (mMap != null) {
            return mMap;
        }

        mMap = new int[cols * rows];
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                mMap[row * cols + col] = TILE_EMPTY;
            }
        }
        for (Vector2 index : mPath) {
            int col = (int) index.x;
            int row = (int) index.y;
            mMap[row * cols + col] = TILE_PATH;
        }
        return mMap;
    }

    public int getAtIndex(@NonNull Vector2 index) {
        int col = (int) index.x;
        int row = (int) index.y;
        return getAtIndex(col, row);
    }

    public int getAtIndex(int col, int row) {
        return getMap()[row * cols + col];
    }

    public void setAtPosition(@NonNull Vector2 position, float tileSize, int value) {
        int col = (int) (position.x / tileSize);
        int row = (int) (position.y / tileSize);

        setAtIndex(col, row, value);
    }

    public void setAtIndex(@NonNull Vector2 index, int value) {
        int col = (int) index.x;
        int row = (int) index.y;
        setAtIndex(col, row, value);
    }

    public void setAtIndex(int col, int row, int value) {
        getMap()[row * cols + col] = value;
    }

    @NonNull
    public Vector2 getStartingPosition(@NonNull Game game) {
        float startX = game.getTileSize() * (mPath.get(0).x - 0.5f);
        float startY = game.getTileSize() * (mPath.get(0).y + 0.5f);

        return new Vector2(startX, startY);
    }

    @NonNull
    public Vector2 getPositionFromPath(@NonNull Game game, int pathIndex) {
        if (pathIndex >= mPath.size()) {
            pathIndex--;
        }

        float startX = game.getTileSize() * (mPath.get(pathIndex).x + 0.5f);
        float startY = game.getTileSize() * (mPath.get(pathIndex).y + 0.5f);

        return new Vector2(startX, startY);
    }

    @NonNull
    public Vector2 getEndingPosition(@NonNull Game game) {
        float startX = game.getTileSize() * (mPath.get(mPath.size() - 1).x + 1.5f);
        float startY = game.getTileSize() * (mPath.get(mPath.size() - 1).y + 0.5f);

        return new Vector2(startX, startY);
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
