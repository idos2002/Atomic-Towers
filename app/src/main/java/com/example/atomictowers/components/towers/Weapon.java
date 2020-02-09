package com.example.atomictowers.components.towers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.WeaponAbility;
import com.example.atomictowers.util.Vector2;

// TODO: Will be a json data file object
public abstract class Weapon implements Component {

    protected WeaponAbility ability;

    abstract void damage(@NonNull Atom atom);

    abstract void updateTarget(@Nullable Vector2 target);
}
