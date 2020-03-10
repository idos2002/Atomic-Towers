package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.google.gson.annotations.SerializedName;

/**
 * A data class that represents properties of a {@linkplain com.example.atomictowers.components.towers}
 */
public class WeaponType {

    public static final String ELECTRON_PROJECTILE_TYPE_KEY = "electronProjectile";

    private Atom mTargetAtom;

    // TODO: Implement tower leveling
    public int level = 1;

    /**
     * The magnitude of the {@linkplain com.example.atomictowers.components.towers.Weapon Weapon}'s
     * velocity. If the weapon is a beam for example, the speed will be 0.
     */
    @SerializedName("speed")
    public float speed = 0;

    /**
     * A floating point number indicating the relative effect of the damage in the atom's strength.
     * For example, a relative damage of 0.1 will decrease the strength of an atom
     * by 0.1 * {@linkplain com.example.atomictowers.components.Game#DAMAGE_MULTIPLIER Game.DAMAGE_MULTIPLIER}.
     */
    @SerializedName("relativeDamage")
    public float relativeDamage = 0;

//    @NonNull
//    public Class<? extends Weapon> getWeaponClass(@NonNull String weaponTypeKey) {
//        switch (weaponTypeKey) {
//            case ELECTRON_PROJECTILE_TYPE_KEY:
//                return ElectronProjectile.class;
//            default:
//                throw new IllegalStateException("`weaponTypeKey`: " + weaponTypeKey + " is not a real weapon type key");
//        }
//    }

    public Atom getTargetAtom() {
        if (mTargetAtom == null) {
            throw new IllegalStateException("target Atom was not set - to set target atom use `setTargetAtom(targetAtom)`");
        }

        return mTargetAtom;
    }

    public void setTargetAtom(@NonNull Atom targetAtom) {
        mTargetAtom = targetAtom;
    }

    public float getDamage() {
        return relativeDamage * Game.DAMAGE_MULTIPLIER;
    }
}
