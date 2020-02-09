package com.example.atomictowers.components.atoms;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.data.game.AtomType;
import com.example.atomictowers.drawables.AtomDrawable;
import com.example.atomictowers.util.Vector2;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class Atom implements Component {

    private static final String TAG = Atom.class.getSimpleName();

    private Game mGame;

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

    private BehaviorSubject<Vector2> mPosition = BehaviorSubject.createDefault(Vector2.ZERO);
    private Vector2 mVelocity = new Vector2(1, 1);

    private AtomDrawable mDrawable;
    private final BehaviorSubject<Integer> mColor = BehaviorSubject.createDefault(Color.BLACK);
    private final BehaviorSubject<Float> mRadius = BehaviorSubject.createDefault(0f);

    public Atom(Game game, AtomType type) {
        mGame = game;
        mAtomicNumber = type.protons;
        mStrength = (type.protons + type.neutrons) * Game.DAMAGE_MULTIPLIER;

        init(type);

        mDrawable = new AtomDrawable(this, mCompositeDisposable);
    }

    private void init(AtomType type) {
        mName.onNext(type.name);
        mSymbol.onNext(type.symbol);
        mColor.onNext(Color.parseColor(type.colorString));
        mRadius.onNext(calculateRadius());
    }

    private float calculateRadius() {
        float maxRadius = Math.min(mGame.getTileDimensions().x, mGame.getTileDimensions().y);
        // Radius of the atom should be at most 2/3 of tile width or height
        return maxRadius - maxRadius / (mAtomicNumber + 2);
    }

    @NonNull
    public Observable<Vector2> getPositionObservable() {
        return mPosition.hide();
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
        mPosition.onNext(mPosition.getValue().add(mVelocity));
        // TODO: Post atom position to global the global Game object.
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mDrawable.draw(canvas);
    }

    public void applyDamage(float damage) {
        mStrength -= damage;

        if (mAtomicNumber <= 0) {
            destroy();
        } else if (mStrength < (mAtomicNumber - 1) * 2 * Game.DAMAGE_MULTIPLIER) {
            changeElement();
        }
    }

    public void destroy() {
        mVelocity = Vector2.ZERO;
        // TODO: Send request to remove instance from atom list to game object
        mCompositeDisposable.dispose();
    }

    private void changeElement() {
        mAtomicNumber--;
        mCompositeDisposable.add(
                mGame.gameRepository.getElements().subscribe(elements -> init(elements.get(mAtomicNumber))));
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format(
                "%s atom { radius: %.2f, position: %s, velocity: %s }",
                mSymbol.getValue(),
                mRadius.getValue(),
                mPosition.getValue(),
                mVelocity);
    }
}
