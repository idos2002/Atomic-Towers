package com.example.atomictowers.components.towers;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.R;
import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.weapons.ElectronProjectile;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.data.game.WeaponType;
import com.example.atomictowers.util.Vector2;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ElectronShooter extends Component {
    private static final String TAG = ElectronShooter.class.getSimpleName();

    // Top left corner of the tile
    private Vector2 mPosition;

    private float mRange;

    private long mShootInterval;

    private Drawable mDrawable;

    private Atom mTarget;

    private WeaponType mWeaponType;

    private Disposable mAtomPositionObservableSubscription;
    private Disposable mShooterSubscription;

    public ElectronShooter(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);

        if (!(data instanceof TowerType)) {
            throw new IllegalArgumentException("`data` is not of type " + Vector2.class.getName());
        }
        TowerType type = (TowerType) data;

        float tileSize = game.getTileSize();

        mRange = type.getRange(tileSize);
        mShootInterval = type.shootInterval;
        mWeaponType = type.weaponType;

        initPosition(type.getTileIndex());
        mWeaponType.setStartingPosition(
            mPosition.add(new Vector2(tileSize * 0.5f, tileSize * 0.5f)));

        initDrawable(game.gameRepository.getDrawableFromResources(R.drawable.electron_shooter));

        setAtomRadar();
    }

    private void initPosition(@NonNull Vector2 tileIndex) {
        float x = tileIndex.x * getGame().getTileSize();
        float y = tileIndex.y * getGame().getTileSize();
        mPosition = new Vector2(x, y);
    }

    private void initDrawable(@NonNull Drawable drawable) {
        int tileSize = (int) getGame().getTileSize();
        int topLeftCornerX = (int) mPosition.x;
        int topLeftCornerY = (int) mPosition.y;

        mDrawable = getGame().gameRepository.getDrawableFromResources(R.drawable.electron_shooter);
        mDrawable.setBounds(topLeftCornerX, topLeftCornerY,
            topLeftCornerX + tileSize, topLeftCornerY + tileSize);
    }

    private void setAtomRadar() {
        mAtomPositionObservableSubscription = getGame().getAtomPositionObservable()
            .filter(pair -> {
                Vector2 atomPosition = pair.second;
                return mPosition.distance(atomPosition) <= mRange;
            })
            .subscribeOn(Schedulers.computation())
            .subscribe(pair -> {
                Atom targetAtom = pair.first;
                if (mTarget == null) {
                    mTarget = targetAtom;
                    setShooter();
                    if (!mAtomPositionObservableSubscription.isDisposed()) {
                        mAtomPositionObservableSubscription.dispose();
                    }
                }
            }, Throwable::printStackTrace);
    }

    private void setShooter() {
        mShooterSubscription = Observable.interval(
            mShootInterval, TimeUnit.MILLISECONDS, Schedulers.computation()
        ).subscribe(x -> {
            mWeaponType.setTargetAtom(mTarget);
            getGame().addComponent(ElectronProjectile.class, mWeaponType);
        }, Throwable::printStackTrace);
    }

    @NonNull
    @Override
    public Vector2 getPosition() {
        return mPosition;
    }

    @Override
    public void update() {
        if (mTarget != null) {
            if (mTarget.isDestroyed() || mPosition.distance(mTarget.getPosition()) > mRange) {
                mTarget = null;
                if (!mShooterSubscription.isDisposed()) {
                    mShooterSubscription.dispose();
                }
                setAtomRadar();
            }
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }
}
