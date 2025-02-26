package com.example.atomictowers.components.towers.weapons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.WeaponType;
import com.example.atomictowers.util.Vector2;

public abstract class Weapon extends Component {
    private static final String TAG = KineticWeapon.class.getSimpleName();

    private Atom mTargetAtom;

    private Vector2 mPosition;

    /**
     * Damage per millisecond.
     */
    private final float mDamage;

    public Weapon(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);

        if (!(data instanceof WeaponType)) {
            throw new IllegalArgumentException("`data` is not of type " + WeaponType.class.getName());
        }
        WeaponType type = (WeaponType) data;

        mPosition = type.getStartingPosition();
        mDamage = type.getDamage();
        setTarget(type.getTargetAtom());
    }

    @NonNull
    @Override
    public Vector2 getPosition() {
        return mPosition;
    }

    public void setPosition(@NonNull Vector2 position) {
        mPosition = position;
    }

    protected float getDamage() {
        return mDamage;
    }

    @Override
    public void update(float timeDiff) {
        damage(mTargetAtom, timeDiff);
    }

    public void setTarget(@NonNull Atom atom) {
        mTargetAtom = atom;
    }

    @NonNull
    public Atom getTarget() {
        return mTargetAtom;
    }

    protected void damage(@NonNull Atom atom, float timeDiff) {
        atom.applyDamage(mDamage * timeDiff);
    }
}
