package com.example.atomictowers.components.atoms;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.KineticComponent;
import com.example.atomictowers.data.game.Element;
import com.example.atomictowers.data.game.LevelMap;
import com.example.atomictowers.drawables.AtomDrawable;
import com.example.atomictowers.util.Vector2;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class Atom extends KineticComponent {

    private static final String TAG = Atom.class.getSimpleName();

    private static final float MAX_SPEED = 2f;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final BehaviorSubject<String> mName = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> mSymbol = BehaviorSubject.createDefault("");

    /**
     * Defined as the atom's number of protons + number of neutrons,
     * multiplied by <code>Game.DAMAGE_MULTIPLIER</code>.
     */
    private int mStrength;

    /**
     * The number of protons the atom has, which describe which element it is.
     */
    private int mAtomicNumber;

    private LevelMap mMap;
    private int mPathIndex = 0;

    private final BehaviorSubject<Vector2> mPosition = BehaviorSubject.createDefault(Vector2.ZERO);
    private float mSpeed = 0;

    private AtomDrawable mDrawable;
    private final BehaviorSubject<Integer> mColor = BehaviorSubject.createDefault(Color.BLACK);
    private final BehaviorSubject<Float> mRadius = BehaviorSubject.createDefault(0f);

    public Atom(@NonNull Game game, int id, @Nullable Object data) {
        super(game, id, data);
        if (!(data instanceof Element)) {
            throw new IllegalArgumentException("`data` is not of type " + Element.class.getName());
        }

        Element element = (Element) data;

        mAtomicNumber = element.protons;
        mStrength = (element.protons + element.neutrons) * Game.DAMAGE_MULTIPLIER;

        mMap = getGame().getMap();
        if (mMap.getPath().isEmpty()) {
            destroy();
            getGame().finish();
        }

        initAtomTypeFields(element);

        mDrawable = new AtomDrawable(this, mCompositeDisposable);

        mPosition.onNext(getGame().getMap().getStartingPosition(getGame()));

        setTarget(mMap.getPositionFromPath(getGame(), mPathIndex));
        setVelocity(mSpeed);
    }

    private void initAtomTypeFields(@NonNull Element element) {
        mName.onNext(element.name);
        mSymbol.onNext(element.symbol);
        mColor.onNext(Color.parseColor(element.colorString));
        mRadius.onNext(calculateRadius());

        mSpeed = (MAX_SPEED / mAtomicNumber) * getGame().getTileSize();
    }

    private float calculateRadius() {
        float maxRadius = 0.45f * getGame().getTileSize();
        // Radius of the atom should be at most 2/3 of tile width or height
        return maxRadius - maxRadius / (mAtomicNumber + 2);
    }

    @NonNull
    public Observable<Vector2> getPositionObservable() {
        return mPosition.hide();
    }

    @NonNull
    @Override
    public Vector2 getPosition() {
        return mPosition.getValue();
    }

    @NonNull
    public Observable<Integer> getColorObservable() {
        return mColor.hide();
    }

    @NonNull
    public Observable<Float> getRadiusObservable() {
        return mRadius.hide();
    }

    public float getRadius() {
        return mRadius.getValue();
    }

    @NonNull
    public Observable<String> getSymbolObservable() {
        return mSymbol.hide();
    }

    @Override
    public void update(float timeDiff) {
        if (mAtomicNumber <= 0 || mPosition.getValue().x > getGame().getDimensions().x) {
            destroy();
        } else {
            if (isNearTarget(getVelocity().magnitude() * (0.1f / MAX_SPEED))) {
                mPathIndex++;

                if (mPathIndex < mMap.getPath().size()) {
                    setTarget(mMap.getPositionFromPath(getGame(), mPathIndex));
                    getGame().postAtomPosition(this, mPosition.getValue());
                } else if (mPathIndex == mMap.getPath().size()) {
                    setTarget(mMap.getEndingPosition(getGame()));
                    getGame().postAtomPosition(this, mPosition.getValue());
                } else {
                    destroy();
                }
            }

            setVelocity(mSpeed);
            mPosition.onNext(mPosition.getValue().add(getVelocity().scale(timeDiff)));
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }

    public void applyDamage(float damage) {
        mStrength -= damage;

        if (mAtomicNumber <= 0) {
            destroy();
        } else if (
            // If the strength is lower than or equal to the minimum strength
            // for this atomic number, lower the atomic number
            mStrength <= (mAtomicNumber - 1) * 2 * Game.DAMAGE_MULTIPLIER
        ) {
            changeElement();
        }

        Log.d(TAG, "damaged: " + toString());
    }

    @Override
    public void destroy() {
        mSpeed = 0;
        mCompositeDisposable.dispose();
        super.destroy();
    }

    public boolean isDestroyed() {
        return mAtomicNumber <= 0;
    }

    private void changeElement() {
        mAtomicNumber--;
        mCompositeDisposable.add(getGame().gameRepository.getElement(mAtomicNumber)
            .subscribe(this::initAtomTypeFields, Throwable::printStackTrace));
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format(
            "%s atom { id: %d, strength: %d, atomicNumber: %d, position: %s, velocity: %s }",
            mSymbol.getValue(),
            getId(),
            mStrength,
            mAtomicNumber,
            getPosition(),
            getVelocity());
    }
}
