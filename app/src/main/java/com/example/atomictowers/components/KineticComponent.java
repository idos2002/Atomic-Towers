package com.example.atomictowers.components;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;

public abstract class KineticComponent extends Component {

    private static final int BASE_SPEED = 2;

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

    public boolean isAtTarget() {
        // TODO: convert to check if it is near the target and not exactly at the target
        return getTarget().equals(getPosition());
    }

    @NonNull
    protected Vector2 getVelocity() {
        return mVelocity;
    }

    // TODO: Change calculateVelocity() to return only the direction,
    //  so the calculateVelocity() will be abstract, and will be overridden to
    //  scale the velocity accordingly by the extending component class.
    protected void calculateVelocity() {
        Vector2 direction = mTarget.subtract(getPosition()).toUnit();
        mVelocity = direction.scale(BASE_SPEED);
    }

    @Override
    public void destroy() {
        mVelocity = Vector2.ZERO;
        super.destroy();
    }
}
