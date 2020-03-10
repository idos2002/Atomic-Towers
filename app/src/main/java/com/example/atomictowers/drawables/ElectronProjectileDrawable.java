package com.example.atomictowers.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.towers.ElectronProjectile;
import com.example.atomictowers.util.Vector2;

public class ElectronProjectileDrawable extends Drawable {
    private static final String TAG = AtomDrawable.class.getSimpleName();

    /**
     * The size of the radius relative to the tile dimensions.
     */
    private static final float RELATIVE_RADIUS_SIZE = 0.1f;

    private final ElectronProjectile mElectronProjectile;
    private float mRadius;
    private final Paint mPaint = new Paint();

    public ElectronProjectileDrawable(@NonNull ElectronProjectile electronProjectile,
                                      @NonNull Vector2 tileDimensions) {
        mElectronProjectile = electronProjectile;

        mPaint.setColor(0xFF00E5FF); // Material color: Cyan A 400
        mPaint.setAntiAlias(true);

        mRadius = Math.min(tileDimensions.x, tileDimensions.y) * RELATIVE_RADIUS_SIZE;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(mElectronProjectile.getPosition().x,
            mElectronProjectile.getPosition().y,
            mRadius, mPaint);
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
