package com.example.atomictowers.components.towers;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.drawables.ElectronProjectileDrawable;

public class ElectronProjectile extends Weapon {
    private static final String TAG = ElectronProjectile.class.getSimpleName();

    private ElectronProjectileDrawable mDrawable;

    public ElectronProjectile(Game game, int id, Object data) {
        super(game, id, data);

        mDrawable = new ElectronProjectileDrawable(this, game.getTileDimensions());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format(
            "electron projectile { id: %d, position: %s, velocity: %s, target: %s }",
            getId(),
            getPosition(),
            getVelocity(),
            getTarget());
    }
}
