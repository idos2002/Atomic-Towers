package com.example.atomictowers.components.atoms;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.KineticComponent;
import com.example.atomictowers.data.game.AtomType;
import com.example.atomictowers.data.game.LevelMap;
import com.example.atomictowers.drawables.AtomDrawable;
import com.example.atomictowers.util.Vector2;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class Atom extends KineticComponent {

    private static final String TAG = Atom.class.getSimpleName();

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final BehaviorSubject<String> mName = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> mSymbol = BehaviorSubject.createDefault("");

    /**
     * Defined as the atom's number of protons + number of neutrons,
     * multiplied by <code>Game.DAMAGE_MULTIPLIER</code>.
     */
    private int mStrength;

    /**
     * The number of protons the atom has, which describe which type of atom it is.
     */
    private int mAtomicNumber;

    private LevelMap mMap;
    private int mPathIndex = 0;

    private BehaviorSubject<Vector2> mPosition = BehaviorSubject.createDefault(Vector2.ZERO);

    private AtomDrawable mDrawable;
    private final BehaviorSubject<Integer> mColor = BehaviorSubject.createDefault(Color.BLACK);
    private final BehaviorSubject<Float> mRadius = BehaviorSubject.createDefault(0f);

    public Atom(@NonNull Game game, int id, @NonNull AtomType type) {
        super(game, id);

        mAtomicNumber = type.protons;
        mStrength = (type.protons + type.neutrons) * Game.DAMAGE_MULTIPLIER;

        mMap = getGame().getMap();
        if (mMap.getPath().isEmpty()) {
            destroy();
        }

        init(type);

        mDrawable = new AtomDrawable(this, mCompositeDisposable);

        mCompositeDisposable.add(
            mPosition.subscribe(position -> getGame().postPosition(this, position)));
        mPosition.onNext(getGame().getMap().getStartingPosition(getGame()));
    }

    private void init(@NonNull AtomType type) {
        mName.onNext(type.name);
        mSymbol.onNext(type.symbol);
        mColor.onNext(Color.parseColor(type.colorString));
        mRadius.onNext(calculateRadius());

        setTarget(mMap.getPositionFromPath(getGame(), mPathIndex));
        calculateVelocity();
    }

    private float calculateRadius() {
        Vector2 tileDimensions = getGame().getTileDimensions();
        float maxRadius = 0.5f * Math.min(tileDimensions.x, tileDimensions.y);
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

    @NonNull
    public Observable<String> getSymbolObservable() {
        return mSymbol.hide();
    }

    @Override
    public void update() {
//        Log.d(TAG, "position: " + getPosition().toString());

        if (isAtTarget()) {
            mPathIndex++;

            if (mMap.getPath().size() - 1 < mPathIndex) {
                if (mMap.getPath().size() == mPathIndex) {
                    setTarget(mMap.getEndingPosition(getGame()));
                } else {
                    destroy();
                }
            } else {
                setTarget(mMap.getPositionFromPath(getGame(), mPathIndex));
                calculateVelocity();
            }
        } else {
            calculateVelocity();
        }

        mPosition.onNext(mPosition.getValue().add(getVelocity()));
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
            mStrength <= (mAtomicNumber - 1) * 2 * Game.DAMAGE_MULTIPLIER) {
            changeElement();
        }
    }

    @Override
    public void destroy() {
        Log.i(TAG, this.toString() + " got destroyed");
        mCompositeDisposable.dispose();
        super.destroy();
    }

    private void changeElement() {
        mAtomicNumber--;
        mCompositeDisposable.add(getGame().gameRepository.getElements()
            .subscribe(elements -> init(elements.get(mAtomicNumber))));
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format(
            "%s atom { id: %d, radius: %.2f, position: %s, velocity: %s }",
            mSymbol.getValue(),
            getId(),
            mRadius.getValue(),
            getPosition(),
            getVelocity());
    }
}
