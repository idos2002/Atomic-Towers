package com.example.atomictowers.components.towers;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.util.Vector2;

public abstract class Tower implements Component {

    private final int mId;

    /**
     * The position in the game grid (a pair of 2 indices).
     */
    protected final Vector2 mPosition;

    protected Weapon mWeapon;

    protected Tower(int id, Vector2 position) {
        this.mId = id;
        this.mPosition = position;
    }

    public int getId() {
        return mId;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    public void damage(Atom atom) {
        mWeapon.damage(atom);
        mWeapon.updateTarget(null);
    }

    public void destroy() {
        // TODO: Destroy tower; delete it from game board
    }
}
