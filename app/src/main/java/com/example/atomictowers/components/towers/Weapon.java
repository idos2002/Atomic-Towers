package com.example.atomictowers.components.towers;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.KineticComponent;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.WeaponType;
import com.example.atomictowers.util.Vector2;

import io.reactivex.disposables.CompositeDisposable;

public abstract class Weapon extends KineticComponent {
    private static final String TAG = Weapon.class.getSimpleName();

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private Atom mTargetAtom;

    private Vector2 mPosition = Vector2.ZERO;
    private float mSpeed;

    private final float mDamage;

    public Weapon(Game game, int id, Object data) {
        super(game, id, data);
        if (!(data instanceof WeaponType)) {
            throw new IllegalArgumentException("`data` is not of type " + WeaponType.class.getName());
        }

        WeaponType type = (WeaponType) data;

        mSpeed = type.speed;
        mDamage = type.getDamage();

        setTarget(type.getTargetAtom());
    }

    @NonNull
    @Override
    public Vector2 getPosition() {
        return mPosition;
    }

    @Override
    public void update() {
        if (isNearTarget(mTargetAtom.getRadius())) {
            damage(mTargetAtom);
        }

        setVelocity(mSpeed);
        mPosition = mPosition.add(getVelocity());
    }

    public void setTarget(@NonNull Atom atom) {
        mTargetAtom = atom;

        // Listen to the target atom's position, and update the target position
        // every time it changes.
        mCompositeDisposable.add(
            atom.getPositionObservable().subscribe(this::setTarget, Throwable::printStackTrace));
    }

    protected void damage(@NonNull Atom atom) {
        atom.applyDamage(mDamage);
    }

    @Override
    public void destroy() {
        mSpeed = 0;
        mCompositeDisposable.dispose();
        super.destroy();
    }
}
