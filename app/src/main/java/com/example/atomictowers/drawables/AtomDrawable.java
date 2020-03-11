package com.example.atomictowers.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.util.Vector2;

import io.reactivex.disposables.CompositeDisposable;

public class AtomDrawable extends Drawable {

    private static final String TAG = AtomDrawable.class.getSimpleName();

    private Vector2 mPosition;

    private float mRadius;
    private String mSymbol;

    private final Paint mBackgroundPaint = new Paint();
    private final Paint mTextPaint = new Paint();

    public AtomDrawable(@NonNull Atom atom, @NonNull CompositeDisposable compositeDisposable) {

        compositeDisposable.add(
            atom.getPositionObservable().subscribe(position -> mPosition = position,
                Throwable::printStackTrace));

        compositeDisposable.add(
            atom.getColorObservable().subscribe(color -> {
                mBackgroundPaint.setColor(color);
                mTextPaint.setColor(calculateTextColor(color));
            }, Throwable::printStackTrace));

        compositeDisposable.add(
            atom.getRadiusObservable().subscribe(radius -> {
                mRadius = radius;
                mTextPaint.setTextSize(radius);
            }, Throwable::printStackTrace));

        compositeDisposable.add(
            atom.getSymbolObservable().subscribe(symbol -> mSymbol = symbol,
                Throwable::printStackTrace));

        mBackgroundPaint.setAntiAlias(true);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(mPosition.x, mPosition.y, mRadius, mBackgroundPaint);

        // Draw text at the center of the circle.
        // Based on: https://stackoverflow.com/questions/11120392/android-center-text-on-canvas
        canvas.drawText(mSymbol,
            mPosition.x,
            mPosition.y - ((mTextPaint.descent() + mTextPaint.ascent()) / 2),
            mTextPaint);
    }

    /**
     * Calculates the text color according to the background color.
     * Source: <a href="https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color">Determine font color based on background color</a>.
     *
     * @param backgroundColor The background color.
     * @return The calculated color of the text.
     */
    @ColorInt
    private int calculateTextColor(@ColorInt int backgroundColor) {
        if (backgroundColor == Color.TRANSPARENT) {
            return Color.TRANSPARENT;
        }

        // Calculate the perceptive luminance
        double luminance = (0.299 * Color.red(backgroundColor) +
            0.587 * Color.green(backgroundColor) +
            0.114 * Color.blue(backgroundColor)) / 255;

        // Return black for bright colors, white for dark colors
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
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
