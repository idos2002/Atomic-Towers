package com.example.atomictowers.components.towers.weapons;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.drawables.weapons.PhotonBeamDrawable;

public class PhotonBeam extends Weapon {
    private PhotonBeamDrawable mDrawable;

    public PhotonBeam(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);
        mDrawable = new PhotonBeamDrawable(this);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }
}
