package com.example.atomictowers.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.data.game.LevelMap;

public class LevelMapDrawable extends Drawable {

    private final LevelMap mMap;

    private float mTileSize;

    private final Paint mEmptyTilePaint = new Paint();
    private final Paint mPathTilePaint = new Paint();

    public LevelMapDrawable(@NonNull LevelMap map) {
        mMap = map;

        mEmptyTilePaint.setColor(0xff00c853);
        mPathTilePaint.setColor(0xff795548);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (bounds != null) {
            mTileSize = (float) (bounds.right - bounds.left) / mMap.cols;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        for (int col = 0; col < mMap.cols; col++) {
            for (int row = 0; row < mMap.rows; row++) {
                float x = col * mTileSize;
                float y = row * mTileSize;

                switch (mMap.getAtIndex(col, row)) {
                    case LevelMap.TILE_PATH:
                        canvas.drawRect(x, y,
                            x + mTileSize, y + mTileSize,
                            mPathTilePaint);
                        break;
                    case LevelMap.TILE_EMPTY:
                        canvas.drawRect(x, y,
                            x + mTileSize, y + mTileSize,
                            mEmptyTilePaint);
                        break;
                }
            }
        }
    }

    @Override
    public void setAlpha(int i) {
        throw new IllegalArgumentException("GameView must have an opaque background");
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        throw new UnsupportedOperationException("Method setColorFilter() is not implemented");
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
