package com.example.atomictowers.components.towers;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.data.game.WeaponType;
import com.example.atomictowers.data.game.game_state.TowerSavedState;
import com.example.atomictowers.util.Vector2;

public abstract class Tower extends Component {

    /**
     * Top-left corner of the tile
     */
    protected Vector2 mPosition;
    protected float mRange;
    protected long mShootInterval;
    protected Atom mTarget;
    protected WeaponType mWeaponType;
    protected Drawable mDrawable;

    public Tower(@NonNull Game game, int id, @Nullable Object data) {
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
        initDrawable();
    }

    private void initPosition(@NonNull Vector2 tileIndex) {
        float x = tileIndex.x * getGame().getTileSize();
        float y = tileIndex.y * getGame().getTileSize();
        mPosition = new Vector2(x, y);
    }

    public Tower(@NonNull Game game, int id, @NonNull TowerType towerType, @NonNull TowerSavedState savedState) {
        super(game, id, towerType);

        float tileSize = game.getTileSize();

        mRange = towerType.getRange(tileSize);
        mShootInterval = towerType.shootInterval;
        mWeaponType = towerType.weaponType;

        mPosition = savedState.position;

        mWeaponType.setStartingPosition(
            mPosition.add(new Vector2(tileSize * 0.5f, tileSize * 0.5f)));
        initDrawable();
    }

    protected abstract void initDrawable();

    @NonNull
    public abstract String getTowerTypeKey();

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }

    @NonNull
    @Override
    public Vector2 getPosition() {
        return mPosition;
    }
}
