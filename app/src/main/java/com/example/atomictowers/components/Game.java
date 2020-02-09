package com.example.atomictowers.components;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.util.Vector2;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class Game {

    private static final String TAG = Game.class.getSimpleName();

    /**
     * A multiplier used to scale the damage of the towers, and the strength of the atoms accordingly.
     */
    // TODO: Set DAMAGE_MULTIPLIER in the towers JSON data.
    public static final int DAMAGE_MULTIPLIER = 100;

    public final GameRepository gameRepository;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private Vector2 mDimensions;

    private Vector2 mTileDimensions;

    private List<Atom> mAtoms = new ArrayList<>();

    public Game(@NonNull GameRepository gameRepository, @NonNull Vector2 dimensions) {
        this.gameRepository = gameRepository;
        mDimensions = dimensions;
        Log.d(TAG, "dimens: " + dimensions);
        mTileDimensions = new Vector2(dimensions.x / 8, dimensions.y / 6);

        mCompositeDisposable.add(gameRepository.getElements().subscribe(atomTypes -> {
            mAtoms.add(new Atom(this, atomTypes.get(0)));
            mAtoms.add(new Atom(this, atomTypes.get(1)));
            mAtoms.add(new Atom(this, atomTypes.get(2)));
        }));
        //mAtom = new Atom(this, gameRepository.getElements().blockingGet().get(0));
        // TODO: NOT WORKING - make the GameView async, so it will work.

        Log.d(TAG, "new Game created");
    }

    public void update() {
        for (Atom atom : mAtoms) {
            atom.update();
        }
    }

    public void draw(@NonNull Canvas canvas) {
        for (Atom atom : mAtoms) {
            atom.draw(canvas);
        }
    }

    public void updateDimensions(int width, int height) {
        mDimensions = new Vector2(width, height);
        mTileDimensions = new Vector2(width / 8, height / 6);
    }

    public Vector2 getDimensions() {
        return mDimensions;
    }

    public Vector2 getTileDimensions() {
        return mTileDimensions;
    }

    public void finish() {
        mCompositeDisposable.dispose();
    }
}
