package com.example.atomictowers.components.towers;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.util.Vector2;

public class Projectile extends Weapon {

    protected float mSpeed = 0;

    protected Vector2 mVelocity;

    public Projectile() {
        super();
        // TODO: Set velocity vector according to speed scalar.
    }

    @Override
    void damage(@NonNull Atom atom) {

    }

    @Override
    void updateTarget(@Nullable Vector2 target) {
        // TODO: Change velocity of the ability, the direction of it
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }
}
