package com.example.atomictowers.components.towers;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.WeaponAbility;
import com.example.atomictowers.util.Vector2;

public class Beam extends Weapon {

    private Vector2 mTarget;

    public Beam(int id, WeaponAbility ability) {
        super(id, ability);
    }

    @Override
    void damage(@NonNull Atom atom) {

    }

    @Override
    void updateTarget(@Nullable Vector2 target) {
        mTarget = target;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }
}
