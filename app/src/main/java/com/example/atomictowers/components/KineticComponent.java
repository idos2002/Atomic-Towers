package com.example.atomictowers.components;

import androidx.annotation.NonNull;

import com.example.atomictowers.util.Vector2;

public abstract class KineticComponent extends Component {
    private static final String TAG = KineticComponent.class.getSimpleName();

    private Vector2 mTarget = Vector2.ZERO;
    private Vector2 mVelocity = Vector2.ZERO;

    public KineticComponent(Game game, int id, Object data) {
        super(game, id, data);
    }

    @NonNull
    public Vector2 getTarget() {
        return mTarget;
    }

    public void setTarget(@NonNull Vector2 target) {
        mTarget = target;
    }

    public boolean isNearTarget(float radius) {
        return mTarget.distance(getPosition()) <= radius;
    }

    @NonNull
    protected Vector2 getVelocity() {
        return mVelocity;
    }

    protected void setVelocity(float speed) {
        Vector2 direction = mTarget.subtract(getPosition()).toUnit();
        mVelocity = direction.scale(speed);
    }

    @Override
    public void destroy() {
        mVelocity = Vector2.ZERO;
        super.destroy();
    }
}
