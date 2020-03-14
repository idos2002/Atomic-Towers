package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;
import com.google.gson.annotations.SerializedName;

public class TowerType {
    public static final String ELECTRON_SHOOTER_TYPE_KEY = "electronShooter";

    @SerializedName("weapon")
    public WeaponType weaponType;

    /**
     * The relative range of targets of the tower, given by the minimum tile dimension.
     * For example, if the game tiles are {@code 300x200} pixels, the tower's range will be
     * {@code relativeRange * 200}, which is the maximum radius the tower will be
     * able to detect and shoot atoms.
     */
    @SerializedName("relativeRange")
    public float relativeRange;

    /**
     * The interval in milliseconds the weapon shoots a new projectile.
     * {@code 0} is infinite shoot interval, used for lasers, beams, etc.
     */
    @SerializedName("shootInterval")
    public long shootInterval;

    private Vector2 mTileIndex;

    public float getRange(Vector2 tileDimensions) {
        return relativeRange * Math.min(tileDimensions.x, tileDimensions.y);
    }

    @NonNull
    public Vector2 getTileIndex() {
        if (mTileIndex == null) {
            throw new IllegalStateException("tower's tile index was not set - to set target atom use `setTileIndex()`");
        }

        return mTileIndex;
    }

    public void setTileIndex(@NonNull Vector2 tileIndex) {
        mTileIndex = tileIndex;
    }

    public void setTargetAtom(@NonNull Vector2 position) {
        mTileIndex = position;
    }
}
