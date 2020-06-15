package com.example.atomictowers.components;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.atomictowers.R;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.atoms.AtomSequencer;
import com.example.atomictowers.components.towers.ElectronShooter;
import com.example.atomictowers.components.towers.PhotonicLaser;
import com.example.atomictowers.components.towers.Tower;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.Level;
import com.example.atomictowers.data.game.LevelMap;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.data.game.game_state.AtomSavedState;
import com.example.atomictowers.data.game.game_state.SavedGameState;
import com.example.atomictowers.data.game.game_state.TowerSavedState;
import com.example.atomictowers.drawables.LevelMapDrawable;
import com.example.atomictowers.util.Vector2;

import java.lang.reflect.InvocationTargetException;

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

    public static final int MAX_HEALTH = 10 * DAMAGE_MULTIPLIER;

    public final GameRepository gameRepository;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private BehaviorSubject<Boolean> mGamePausedSubject = BehaviorSubject.createDefault(false);

    private float mTileSize;
    private Vector2 mDimensions;

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
    private final SparseArray<Component> mComponents = new SparseArray<>();

    private final PublishSubject<Pair<Atom, Vector2>> mAtomPositions = PublishSubject.create();

    private AtomSequencer mAtomSequencer;

    private String mSelectedTowerTypeKey;


    private final BehaviorSubject<Integer> mHealth = BehaviorSubject.createDefault(MAX_HEALTH);

    public int getHealth() {
        return mHealth.getValue();
    }

    public Observable<Integer> getHealthObservable() {
        return mHealth.hide();
    }


    private final BehaviorSubject<Integer> mEnergy = BehaviorSubject.createDefault(0);

    public int getEnergy() {
        return mEnergy.getValue();
    }

    public Observable<Integer> getEnergyObservable() {
        return mEnergy.hide();
    }


    private final BehaviorSubject<Integer> mGameEnded = BehaviorSubject.create();

    public Observable<Integer> getGameEndedObservable() {
        return mGameEnded.hide();
    }


    /**
     * Creates and initializes a new {@link Game} object.
     * This constructor should <i>only</i> be used for the  initialization of the {@link Game} object.
     *
     * @param gameRepository A {@link GameRepository} instance for the game to retrieve game data from.
     * @param dimensions     The dimensions of the game window
     *                       (the dimensions of {@linkplain com.example.atomictowers.screens.game.GameView GameView}).
     */
    public Game(@NonNull GameRepository gameRepository, @NonNull Vector2 dimensions,
                @NonNull SavedGameState savedGameState) {
        this.gameRepository = gameRepository;
        Log.d(TAG, "New Game created");

        mHealth.onNext(savedGameState.health);
        mEnergy.onNext(savedGameState.energy);

        mCompositeDisposable.add(gameRepository.getLevel(savedGameState.levelNumber).subscribe(level -> {
            updateDimensions(dimensions);
            mLevelMap = level.map;
            mLevelMapDrawable = new LevelMapDrawable(mLevelMap);
            mLevelMapDrawable.setBounds(0, 0, (int) mDimensions.x, (int) mDimensions.y);

            mAtomSequencer = new AtomSequencer(
                this,
                level.elementsAtomicNumbers,
                level.atomSequence,
                savedGameState.numberOfCreatedAtoms);

            initComponents(savedGameState);

            Log.i(TAG, "Game is initialized");
            start();
        }, Throwable::printStackTrace));
    }

    private void initComponents(@NonNull SavedGameState savedGameState) {
        if (savedGameState.atomSavedStates != null) {
            for (AtomSavedState savedState : savedGameState.atomSavedStates) {
                addComponent(savedState);
            }
        }
        if (savedGameState.towerSavedStates != null) {
            for (TowerSavedState towerState : savedGameState.towerSavedStates) {
                addComponent(towerState);
            }
        }
    }

    /**
     * Called <i>Only</i> when the {@link Game} object is fully initialized.
     * This is the <i>only</i> starting point of the game.
     */
    private void start() {
        mAtomSequencer.start();
    }

    public void pause() {
        mGamePausedSubject.onNext(true);
        mAtomSequencer.pause();
    }

    public void resume() {
        mGamePausedSubject.onNext(false);
        start();
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
        return Level.LEVEL_ONE;
    }

    @NonNull
    public LevelMap getMap() {
        return mLevelMap;
    }

    public void selectTowerType(@NonNull String selectedTowerTypeKey) {
        mSelectedTowerTypeKey = selectedTowerTypeKey;
    }

    public void putTowerOnMap(@NonNull Vector2 tileIndex) {
        if (mSelectedTowerTypeKey != null && mLevelMap.getAtIndex(tileIndex) == LevelMap.TILE_EMPTY) {
            mLevelMap.setAtIndex(tileIndex, LevelMap.TILE_TOWER);
            mCompositeDisposable.add(
                gameRepository.getTowerType(mSelectedTowerTypeKey)
                    .subscribe(towerType -> {
                        towerType.setTileIndex(tileIndex);
                        addComponent(convertTowerTypeKeyToClass(mSelectedTowerTypeKey), towerType);
                    }, Throwable::printStackTrace));
        }
    }

    private Class<? extends Tower> convertTowerTypeKeyToClass(@NonNull String towerTypeKey) {
        switch (towerTypeKey) {
            case TowerType.ELECTRON_SHOOTER_TYPE_KEY:
                return ElectronShooter.class;
            case TowerType.PHOTONIC_LASER_TYPE_KEY:
                return PhotonicLaser.class;
            default:
                throw new IllegalArgumentException("towerTypeKey `" + towerTypeKey + "` is not a valid");
        }
    }

    @NonNull
    public SparseArray<Component> getComponentsMap() {
        return mComponents.clone();
    }

    public int addComponent(@NonNull Class<? extends Component> type, @Nullable Object data) {
        int id = generateComponentId();
        try {
            Component component = type.getConstructor(Game.class, int.class, Object.class)
                .newInstance(this, id, data);
            mComponents.append(id, component);
            Log.d(TAG, "Created new component with id: " + id);
        } catch (IllegalAccessException
            | InstantiationException
            | InvocationTargetException
            | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not create a new component", e);
        }
        return id;
    }

    public int addComponent(@NonNull AtomSavedState savedState) {
        int id = generateComponentId();
        mCompositeDisposable.add(gameRepository.getElement(savedState.atomicNumber).subscribe(element -> {
            Component component = new Atom(this, id, element, savedState);
            mComponents.append(id, component);
            Log.d(TAG, "Created new component with id: " + id);
        }, e -> {
            throw new IllegalArgumentException("Could not create a new component", e);
        }));
        return id;
    }

    public int addComponent(@NonNull TowerSavedState savedState) {
        int id = generateComponentId();
        mCompositeDisposable.add(gameRepository.getTowerType(savedState.towerTypeKey).subscribe(towerType -> {
            Class<? extends Tower> type = convertTowerTypeKeyToClass(savedState.towerTypeKey);
            Component component =
                type.getConstructor(Game.class, int.class, TowerType.class, TowerSavedState.class)
                    .newInstance(this, id, towerType, savedState);
            mComponents.append(id, component);

            mLevelMap.setAtPosition(savedState.position, mTileSize, LevelMap.TILE_TOWER);
            Log.d(TAG, "Created new component with id: " + id);
        }, e -> {
            throw new IllegalArgumentException("Could not create a new component", e);
        }));
        return id;
    }

    /**
     * Generates a unique new {@link Component} ID.
     *
     * @return a unique {@link Component} ID
     */
    private synchronized int generateComponentId() {
        return ++mIdCounter;
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

    public void increaseEnergy() {
        mEnergy.onNext(mEnergy.getValue() + 1);
    }

    public void decreaseHealth(int atomStrength) {
        mHealth.onNext(mHealth.getValue() - atomStrength);

        if (mHealth.getValue() <= 0) {
            finish(R.string.game_lost_message);
        }
    }

    public Observable<Pair<Atom, Vector2>> getAtomPositionObservable() {
        return mAtomPositions.hide();
    }

    public int getNumberOfCreatedAtoms() {
        return mAtomSequencer.getNumberOfCreatedAtoms();
    }

    public void finish(@StringRes int gameEndedMessageStringId) {
        mGameEnded.onNext(gameEndedMessageStringId);
        mCompositeDisposable.dispose();
        mAtomSequencer.destroy();
    }

    public boolean hasFinished() {
        if (mGameEnded.getValue() == null) {
            return false;
        }
        return mGameEnded.getValue() == R.string.game_won_message
            || mGameEnded.getValue() == R.string.game_lost_message;
    }
}
