package com.example.atomictowers.drawables.weapons;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.towers.weapons.PhotonBeam;
import com.example.atomictowers.util.Vector2;

public class PhotonBeamDrawable extends Drawable {
    private static final String TAG = PhotonBeamDrawable.class.getSimpleName();

    /**
     * The size of the radius relative to the tile dimensions.
     */
    private static final float RELATIVE_RADIUS_SIZE = 0.1f;

    private final PhotonBeam mPhotonBeam;
    private final Paint mPaint = new Paint();

    public PhotonBeamDrawable(@NonNull PhotonBeam photonBeam) {
        mPhotonBeam = photonBeam;
        mPaint.setColor(0xFFFF0000);
        mPaint.setStrokeWidth(18);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Vector2 start = mPhotonBeam.getPosition();
        Vector2 end = mPhotonBeam.getTarget().getPosition();
        canvas.drawLine(start.x, start.y, end.x, end.y, mPaint);
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
