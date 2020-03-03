package com.example.atomictowers.components;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.LevelMap;
import com.example.atomictowers.drawables.LevelMapDrawable;
import com.example.atomictowers.util.Vector2;

import java.lang.reflect.InvocationTargetException;

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

    private LevelMap mLevelMap;
    private Drawable mLevelMapDrawable;

    /**
     * Used as a counter for {@link Component} ID's.
     * Should only be used by {@link #generateComponentId()}!
     */
    private volatile int mIdCounter = 0;

    /**
     * Used to index all {@link Component}s on screen.
     * {@link SparseArray} is used instead of {@link java.util.HashMap} because of IntelliJ warning.
     *
     * @see <a href="https://stackoverflow.com/questions/25560629/sparsearray-vs-hashmap">
     * SparseArray vs HashMap (Stack Overflow)</a>
     */
    private SparseArray<Component> mComponents = new SparseArray<>();

    private PublishSubject<Pair<Atom, Vector2>> mAtomPositions = PublishSubject.create();

    /**
     * Creates and initializes a new {@link Game} object.
     * This constructor should <i>only</i> be used for the  initialization of the {@link Game} object.
     *
     * @param gameRepository A {@link GameRepository} instance for the game to retrieve game data from.
     * @param dimensions     The dimensions of the game window
     *                       (the dimensions of {@link com.example.atomictowers.screens.game.GameView}).
     */
    public Game(@NonNull GameRepository gameRepository, @NonNull Vector2 dimensions) {
        this.gameRepository = gameRepository;
        mDimensions = dimensions;
        mTileDimensions = new Vector2(dimensions.x / 8, dimensions.y / 6);

        mCompositeDisposable.add(gameRepository.getLevels().subscribe(
            levels -> {
                mLevelMap = levels.get(0).map;
                mLevelMapDrawable = new LevelMapDrawable(mLevelMap);
                mLevelMapDrawable.setBounds(0, 0, (int) dimensions.x, (int) dimensions.y);

                Log.i(TAG, "Game is initialized");
                start();
            },
            Throwable::printStackTrace));

        Log.d(TAG, "new Game created");
    }

    /**
     * Called Only when the {@link Game} object is fully initialized.
     * This is the <i>only</i> starting point of the game.
     */
    private void start() {
        mCompositeDisposable.add(gameRepository.getElements().subscribe(atomTypes -> {
            // Used as a bug check - should not display this atom.
            addComponent(Atom.class, atomTypes.get(0));

            addComponent(Atom.class, atomTypes.get(1));
            addComponent(Atom.class, atomTypes.get(2));
        }));
    }

    public void update() {
        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            mComponents.get(key).update();
        }
    }

    public void draw(@NonNull Canvas canvas) {
        if (mLevelMapDrawable != null) {
            mLevelMapDrawable.draw(canvas);
        }

        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            mComponents.get(key).draw(canvas);
        }
    }

    public void updateDimensions(int width, int height) {
        mDimensions = new Vector2(width, height);

        // Update the tile dimensions
        mTileDimensions = new Vector2((float) width / mLevelMap.cols,
            (float) height / mLevelMap.rows);
    }

    @NonNull
    public Vector2 getDimensions() {
        return mDimensions;
    }

    @NonNull
    public Vector2 getTileDimensions() {
        return mTileDimensions;
    }

    // TODO: Fix the case when map is not initialized yet - will produce NullPointerException.
    @NonNull
    public LevelMap getMap() {
        return mLevelMap;
    }

    public void addComponent(@NonNull Class<? extends Component> type) {
        int id = generateComponentId();
        try {
            Component component = type.getConstructor(Game.class, int.class)
                .newInstance(this, id);
            mComponents.append(id, component);
        } catch (IllegalAccessException
            | InstantiationException
            | InvocationTargetException
            | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not create a new component", e);
        }

    }

    public void addComponent(@NonNull Class<? extends Component> type, @NonNull Object data) {
        int id = generateComponentId();
        try {
            Component component = type.getConstructor(Game.class, int.class, Object.class)
                .newInstance(this, id, data);
            mComponents.append(id, component);
        } catch (IllegalAccessException
            | InstantiationException
            | InvocationTargetException
            | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not create a new component", e);
        }
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
