package com.example.atomictowers.components;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.ElectronShooter;
import com.example.atomictowers.data.game.Element;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.LevelMap;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.drawables.LevelMapDrawable;
import com.example.atomictowers.util.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class Game {
    private static final String TAG = Game.class.getSimpleName();

    private static final int HORIZONTAL_TILE_COUNT = 8;
    private static final int VERTICAL_TILE_COUNT = 6;

    /**
     * A multiplier used to scale the damage of the towers, and the strength of the atoms accordingly.
     */
    public static final int DAMAGE_MULTIPLIER = 100;

    public final GameRepository gameRepository;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private BehaviorSubject<Boolean> mGamePausedSubject = BehaviorSubject.createDefault(false);

    private float mTileSize;
    private Vector2 mDimensions;

    private LevelMap mLevelMap;
    private Drawable mLevelMapDrawable;

    // TODO: Change value by player's choosing and add scoring system
    private String mPickedTowerTypeKey = TowerType.ELECTRON_SHOOTER_TYPE_KEY;

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
    private final SparseArray<Component> mComponents = new SparseArray<>();

    private final PublishSubject<Pair<Atom, Vector2>> mAtomPositions = PublishSubject.create();

    /**
     * Creates and initializes a new {@link Game} object.
     * This constructor should <i>only</i> be used for the  initialization of the {@link Game} object.
     *
     * @param gameRepository A {@link GameRepository} instance for the game to retrieve game data from.
     * @param dimensions     The dimensions of the game window
     *                       (the dimensions of {@linkplain com.example.atomictowers.screens.game.GameView GameView}).
     */
    public Game(@NonNull GameRepository gameRepository, @NonNull Vector2 dimensions) {
        this.gameRepository = gameRepository;
        Log.d(TAG, "new Game created");

        mCompositeDisposable.add(gameRepository.getLevel(0).subscribe(level -> {
            updateDimensions(dimensions);
            mLevelMap = level.map;
            mLevelMapDrawable = new LevelMapDrawable(mLevelMap);
            mLevelMapDrawable.setBounds(0, 0, (int) mDimensions.x, (int) mDimensions.y);

            Log.i(TAG, "Game is initialized");
            start();
        }, Throwable::printStackTrace));
    }

    /**
     * Called Only when the {@link Game} object is fully initialized.
     * This is the <i>only</i> starting point of the game.
     */
    @SuppressLint("RxDefaultScheduler")
    private void start() {
        mCompositeDisposable.add(gameRepository.getElements().subscribe(elements -> {
            // Used as a bug check - should not display this atom.
            addComponent(Atom.class, elements.get(0));

            mCompositeDisposable.add(
                Observable.interval(0, 6000, TimeUnit.MILLISECONDS)
                    .subscribe(l -> addComponent(Atom.class, elements.get(Element.OXYGEN)),
                        Throwable::printStackTrace));
        }, Throwable::printStackTrace));
    }

    public void pause() {
        mGamePausedSubject.onNext(true);
    }

    public void resume() {
        mGamePausedSubject.onNext(false);
    }

    public boolean isGamePaused() {
        return mGamePausedSubject.getValue();
    }

    public BehaviorSubject<Boolean> getGamePausedSubject() {
        return mGamePausedSubject;
    }

    /**
     * Updates all game components with a given change in time.
     *
     * @param timeDiff The change in time in seconds.
     */
    public void update(float timeDiff) {
        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            Component component = mComponents.get(key);
            if (component != null) {
                component.update(timeDiff);
            }
        }
    }

    public void draw(@NonNull Canvas canvas) {
        if (mLevelMapDrawable != null) {
            mLevelMapDrawable.draw(canvas);
        }

        for (int i = 0; i < mComponents.size(); i++) {
            int key = mComponents.keyAt(i);
            Component component = mComponents.get(key);
            if (component != null) {
                component.draw(canvas);
            }
        }
    }

    public float getTileSize() {
        return mTileSize;
    }

    @NonNull
    public Vector2 getDimensions() {
        return mDimensions;
    }

    @NonNull
    public static Vector2 getDimensionsByScreenHeight(int height) {
        float tileSize = (float) height / VERTICAL_TILE_COUNT;
        return new Vector2(tileSize * HORIZONTAL_TILE_COUNT, tileSize * VERTICAL_TILE_COUNT);
    }

    public void updateDimensions(@NonNull Vector2 dimensions) {
        // The game must have square tiles
        mTileSize = dimensions.y / VERTICAL_TILE_COUNT;
        mDimensions = new Vector2(mTileSize * HORIZONTAL_TILE_COUNT, mTileSize * VERTICAL_TILE_COUNT);
    }

    public int getLevelNumber() {
        // TODO: Update this method
        return 0;
    }

    // TODO: Fix the case when map is not initialized yet - will produce NullPointerException.
    //  A solution could be adding a loading screen, until the game is fully initialized
    //  (like other games).
    @NonNull
    public LevelMap getMap() {
        return mLevelMap;
    }

    // TODO: Add check whether there is already a tower on a tile
    public void putTowerOnMap(@NonNull Vector2 tileIndex) {
        mCompositeDisposable.add(
            gameRepository.getTowerType(mPickedTowerTypeKey)
                .subscribe(towerType -> {
                    towerType.setTileIndex(tileIndex);
                    addComponent(convertTowerTyeKeyToClass(mPickedTowerTypeKey), towerType);
                }, Throwable::printStackTrace));
    }

    private Class<? extends Component> convertTowerTyeKeyToClass(@NonNull String towerTypeKey) {
        switch (towerTypeKey) {
            case TowerType.ELECTRON_SHOOTER_TYPE_KEY:
                return ElectronShooter.class;
            default:
                throw new IllegalArgumentException("towerTypeKey is not a valid");
        }
    }

    @NonNull
    public SparseArray<Component> getComponentsMap() {
        return mComponents.clone();
    }

    public int addComponent(@NonNull Class<? extends Component> type) {
        return addComponent(type, null);
    }

    public int addComponent(@NonNull Class<? extends Component> type, @Nullable Object data) {
        int id = generateComponentId();

        try {
            Component component = type.getConstructor(Game.class, int.class, Object.class)
                .newInstance(this, id, data);
            mComponents.append(id, component);
            Log.d(TAG, "created new component with id: " + id);
        } catch (IllegalAccessException
            | InstantiationException
            | InvocationTargetException
            | NoSuchMethodException e) {
            throw new IllegalArgumentException("could not create a new component", e);
        }

        return id;
    }

    @Nullable
    public Component getComponent(int componentId) {
        return mComponents.get(componentId);
    }

    public void removeComponent(int componentId) {
        mComponents.remove(componentId);
        Log.d(TAG, "removed component with id: " + componentId);
    }

    public void postAtomPosition(Atom atom, Vector2 position) {
        mAtomPositions.onNext(new Pair<>(atom, position));
    }

    public Observable<Pair<Atom, Vector2>> getAtomPositionObservable() {
        return mAtomPositions.hide();
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
