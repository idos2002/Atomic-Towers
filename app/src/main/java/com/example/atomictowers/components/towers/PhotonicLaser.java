package com.example.atomictowers.components.towers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.R;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.weapons.PhotonBeam;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.data.game.game_state.TowerSavedState;
import com.example.atomictowers.util.Vector2;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PhotonicLaser extends Tower {
    private int mWeaponComponentId;
    private Disposable mAtomPositionObservableSubscription;

    public PhotonicLaser(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);
        setAtomRadar();
    }

    public PhotonicLaser(@NonNull Game game, int id, @NonNull TowerType towerType, @NonNull TowerSavedState savedState) {
        super(game, id, towerType, savedState);
        setAtomRadar();
    }

    @Override
    protected void initDrawable() {
        int tileSize = (int) getGame().getTileSize();
        int topLeftCornerX = (int) mPosition.x;
        int topLeftCornerY = (int) mPosition.y;

        mDrawable = getGame().gameRepository.getDrawableFromResources(R.drawable.photonic_laser);
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
                    setBeam();
                    if (!mAtomPositionObservableSubscription.isDisposed()) {
                        mAtomPositionObservableSubscription.dispose();
                    }
                }
            }, Throwable::printStackTrace);
    }

    private void setBeam() {
        mWeaponType.setTargetAtom(mTarget);
        mWeaponComponentId = getGame().addComponent(PhotonBeam.class, mWeaponType);
    }

    @Override
    public void update(float timeDiff) {
        if (mTarget != null) {
            if (mTarget.isDestroyed() || mPosition.distance(mTarget.getPosition()) > mRange) {
                mTarget = null;
                getGame().removeComponent(mWeaponComponentId);
                mWeaponComponentId = 0;
                setAtomRadar();
            }
        }
    }

    @NonNull
    @Override
    public String getTowerTypeKey() {
        return TowerType.PHOTONIC_LASER_TYPE_KEY;
    }

    @Override
    public void destroy() {
        if (!mAtomPositionObservableSubscription.isDisposed()) {
            mAtomPositionObservableSubscription.dispose();
        }
        super.destroy();
    }
}
