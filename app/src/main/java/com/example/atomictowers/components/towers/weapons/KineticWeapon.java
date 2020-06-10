package com.example.atomictowers.components.towers.weapons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.KineticComponent;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.WeaponType;
import com.example.atomictowers.util.Vector2;

import io.reactivex.disposables.Disposable;

public abstract class KineticWeapon extends KineticComponent {
    private static final String TAG = KineticWeapon.class.getSimpleName();

    private Disposable mTargetAtomSubscription;

    private Atom mTargetAtom;

    private Vector2 mPosition;
    private float mSpeed;

    /**
     * Damage per hit.
     */
    private final float mDamage;

    public KineticWeapon(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);

        if (!(data instanceof WeaponType)) {
            throw new IllegalArgumentException("`data` is not of type " + WeaponType.class.getName());
        }
        WeaponType type = (WeaponType) data;

        mPosition = type.getStartingPosition();
        mSpeed = type.speed * getGame().getTileSize();
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
        if (isNearTarget(mTargetAtom.getRadius())) {
            damage(mTargetAtom);
        }

        Vector2 gameDimensions = getGame().getDimensions();
        if (getPosition().x < 0 || getPosition().x > gameDimensions.x ||
            getPosition().y < 0 || getPosition().y > gameDimensions.y) {
            destroy();
        }

        setVelocity(mSpeed);
        mPosition = mPosition.add(getVelocity().scale(timeDiff));
    }

    public void setTarget(@NonNull Atom atom) {
        mTargetAtom = atom;

        // Listen to the target atom's position, and update the target position
        // every time it changes.
        mTargetAtomSubscription = atom.getPositionObservable()
            .subscribe(this::setTarget, Throwable::printStackTrace);
    }

    protected void damage(@NonNull Atom atom) {
        atom.applyDamage(mDamage);
    }

    @Override
    public void destroy() {
        mSpeed = 0;
        mTargetAtomSubscription.dispose();
        super.destroy();
    }
}
