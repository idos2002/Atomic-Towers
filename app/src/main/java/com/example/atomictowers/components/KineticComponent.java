package com.example.atomictowers.components;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;

public abstract class KineticComponent extends Component {

    private Vector2 mTarget = Vector2.ZERO;
    private Vector2 mVelocity = Vector2.ZERO;

    public KineticComponent(Game game, int id) {
        super(game, id);
    }

    @NonNull
    public Vector2 getTarget() {
        return mTarget;
    }

    public void setTarget(@NonNull Vector2 target) {
        mTarget = target;
    }

    public boolean isNearTarget(float radius) {
        // TODO: convert to check if it is near the target and not exactly at the target
        return getTarget().distance(getPosition()) <= radius;
    }

    @NonNull
    protected Vector2 getVelocity() {
        return mVelocity;
    }

    protected void setVelocity(@NonNull Vector2 velocity) {
        mVelocity = velocity;
    }

    protected abstract void calculateVelocity();

    @NonNull
    protected Vector2 calculateDirection() {
        return mTarget.subtract(getPosition()).toUnit();
    }

    @Override
    public void destroy() {
        mVelocity = Vector2.ZERO;
        super.destroy();
    }
}
