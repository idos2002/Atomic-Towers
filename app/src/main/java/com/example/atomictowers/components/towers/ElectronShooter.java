package com.example.atomictowers.components.towers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.R;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.weapons.ElectronProjectile;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.util.Vector2;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ElectronShooter extends Tower {
    private static final String TAG = ElectronShooter.class.getSimpleName();

    private Disposable mAtomPositionObservableSubscription;
    private Disposable mShooterSubscription;
    private Disposable mGamePauseSubscription;

    public ElectronShooter(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);
        setGamePauseListener();
        setAtomRadar();
    }

    @Override
    protected void initDrawable() {
        int tileSize = (int) getGame().getTileSize();
        int topLeftCornerX = (int) mPosition.x;
        int topLeftCornerY = (int) mPosition.y;

        mDrawable = getGame().gameRepository.getDrawableFromResources(R.drawable.electron_shooter);
        mDrawable.setBounds(topLeftCornerX, topLeftCornerY,
            topLeftCornerX + tileSize, topLeftCornerY + tileSize);
    }

    @NonNull
    @Override
    public String getTowerTypeKey() {
        return TowerType.ELECTRON_SHOOTER_TYPE_KEY;
    }

    private void setGamePauseListener() {
        mGamePauseSubscription = getGame().getGamePausedSubject()
            .filter(isPaused -> isPaused)
            .subscribe(isPaused -> {
                if (mShooterSubscription != null && !mShooterSubscription.isDisposed()) {
                    mShooterSubscription.dispose();
                }
            }, Throwable::printStackTrace);
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
            mShootInterval, TimeUnit.MILLISECONDS, Schedulers.computation())
            .subscribe(l -> {
                mWeaponType.setTargetAtom(mTarget);
                getGame().addComponent(ElectronProjectile.class, mWeaponType);
            }, Throwable::printStackTrace);
    }

    @Override
    public void update(float timeDiff) {
        if (mTarget != null) {
            if (mTarget.isDestroyed() || mPosition.distance(mTarget.getPosition()) > mRange) {
                mTarget = null;
                if (!mShooterSubscription.isDisposed()) {
                    mShooterSubscription.dispose();
                }
                setAtomRadar();
            } else if (mShooterSubscription.isDisposed() && !getGame().getGamePausedSubject().getValue()) {
                setShooter();
            }
        }
    }

    @Override
    public void destroy() {
        if (!mGamePauseSubscription.isDisposed()) {
            mGamePauseSubscription.dispose();
        }
        if (!mShooterSubscription.isDisposed()) {
            mShooterSubscription.dispose();
        }
        if (!mAtomPositionObservableSubscription.isDisposed()) {
            mAtomPositionObservableSubscription.dispose();
        }
        super.destroy();
    }
}
