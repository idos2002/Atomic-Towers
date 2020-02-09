package com.example.atomictowers.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// TODO: Change implementation for matrix of needed and change to Component class
//  (BTW 9:6 ratio works very well actually, but 8:6 will probably better)
public class GameBackgroundDrawable extends Drawable {

    private int mRows = 1;
    private int mColumns = 1;

    private float mSlotWidth = 5;
    private float mSlotHeight = 5;

    private Paint mSlotPaint = new Paint();

    public GameBackgroundDrawable(int columns, int rows) {
        mColumns = columns;
        mRows = rows;

        mSlotPaint.setColor(0xFF448AFF);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (bounds != null) {
            mSlotWidth = (float) (bounds.right - bounds.left) / mColumns;
            mSlotHeight = (float) (bounds.bottom - bounds.top) / mRows;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        for (float x = 0; x < getBounds().right; x += mSlotWidth) {
            for (float y = 0; y < getBounds().bottom; y += mSlotHeight) {
                canvas.drawRoundRect(
                        new RectF(x, y, x + mSlotWidth, y + mSlotHeight),
                        10, 10,
                        mSlotPaint);
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
