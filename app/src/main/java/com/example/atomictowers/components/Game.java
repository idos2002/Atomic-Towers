package com.example.atomictowers.components;

import android.graphics.Canvas;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.util.Vector2;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

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

    /**
     * Used as a counter for {@link Component} ID's.
     * Should only be used by {@link #generateComponentId()}!
     */
    private volatile int mIdCounter = 0;

    /**
     * Used to index all {@link Component}s on screen. Used because of IntelliJ warning.
     *
     * @see <a href="https://stackoverflow.com/questions/25560629/sparsearray-vs-hashmap">
     * SparseArray vs HashMap (Stack Overflow)</a>
     */
    private SparseArray<Component> mComponents = new SparseArray<>();

    private PublishSubject<Pair<Atom, Vector2>> mAtomPositions = PublishSubject.create();

    public Game(@NonNull GameRepository gameRepository, @NonNull Vector2 dimensions) {
        this.gameRepository = gameRepository;
        mDimensions = dimensions;
        mTileDimensions = new Vector2(dimensions.x / 8, dimensions.y / 6);

        mCompositeDisposable.add(gameRepository.getElements().subscribe(atomTypes -> {
            addComponent(new Atom(this, 1, atomTypes.get(0)));
            addComponent(new Atom(this, 2, atomTypes.get(1)));
            addComponent(new Atom(this, 3, atomTypes.get(2)));
        }));

        Log.d(TAG, "new Game created");
    }

    public void update() {
        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            mComponents.get(key).update();
        }
    }

    public void draw(@NonNull Canvas canvas) {
        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            mComponents.get(key).draw(canvas);
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

    public void addComponent(@NonNull Component component) {
        mComponents.append(generateComponentId(), component);
    }

    public void removeComponent(int componentId) {
        mComponents.remove(componentId);
    }

    public void postPosition(Atom atom, Vector2 position) {
        mAtomPositions.onNext(new Pair<>(atom, position));
    }

    public void finish() {
        mCompositeDisposable.dispose();
    }

    /**
     * Generates a unique new {@link Component} ID.
     *
     * @return a unique {@link Component} ID
     */
    private synchronized int generateComponentId() {
        return ++mIdCounter;
    }
}
