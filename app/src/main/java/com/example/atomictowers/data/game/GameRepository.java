package com.example.atomictowers.data.game;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.atomictowers.R;
import com.example.atomictowers.data.game.game_state.SavedGameState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.atomictowers.util.Util.internalStorageFileExists;
import static com.example.atomictowers.util.Util.readInternalStorageFile;
import static com.example.atomictowers.util.Util.readResourceFile;
import static com.example.atomictowers.util.Util.writeInternalStorageFile;

public class GameRepository {
    private static final String TAG = GameRepository.class.getSimpleName();
    public static final String SAVED_GAME_STATE_FILENAME = "saved_game_state.json";

    private volatile static GameRepository INSTANCE;
    private final Context mApplicationContext;
    private final Gson mGson = new Gson();
    private final GameCache mGameCache;

    private GameRepository(@NonNull Context applicationContext, @NonNull GameCache gameCache) {
        mApplicationContext = applicationContext;
        mGameCache = gameCache;
    }

    public static GameRepository getInstance(@NonNull Context applicationContext) {
        if (INSTANCE == null) {
            synchronized (GameRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GameRepository(applicationContext, new GameCache());
                }
            }
        }

        return INSTANCE;
    }

    @NonNull
    public Single<Level> getLevel(int levelNumber) {
        Level level = mGameCache.getLevel(levelNumber);
        if (level != null) {
            return Single.just(level);
        }

        return setLevelsInCache()
            .andThen(Single.defer(() -> {
                Level newLevel = mGameCache.getLevel(levelNumber);
                if (newLevel == null) {
                    throw new RuntimeException(
                        "error retrieving Level number " + levelNumber);
                }
                return Single.just(newLevel);
            }));
    }

    @NonNull
    private Completable setLevelsInCache() {
        return Single.fromCallable(() -> readResourceFile(mApplicationContext, R.raw.levels))
            .flatMapCompletable(levelsJson -> {
                Type type = new TypeToken<List<Level>>() {
                }.getType();
                List<Level> levels = mGson.fromJson(levelsJson, type);

                if (levels == null) {
                    throw new RuntimeException("error parsing `levels.json`");
                }
                mGameCache.setLevels(levels);

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<List<Element>> getElements() {
        if (mGameCache.getElements() != null) {
            return Single.just(mGameCache.getElements());
        }

        return setElementsInCache()
            .andThen(Single.defer(() -> Single.just(mGameCache.getElements())));
    }

    @NonNull
    public Single<Element> getElement(int atomicNumber) {
        Element element = mGameCache.getElement(atomicNumber);
        if (element != null) {
            return Single.just(element);
        }

        return setElementsInCache()
            .andThen(Single.defer(() -> {
                Element newElement = mGameCache.getElement(atomicNumber);
                if (newElement == null) {
                    throw new RuntimeException(
                        "error retrieving Element with atomic number " + atomicNumber);
                }
                return Single.just(newElement);
            }));
    }

    @NonNull
    private Completable setElementsInCache() {
        return Single.fromCallable(() -> readResourceFile(mApplicationContext, R.raw.elements))
            .flatMapCompletable(elementsJson -> {
                Type type = new TypeToken<List<Element>>() {
                }.getType();
                List<Element> elements = mGson.fromJson(elementsJson, type);

                if (elements == null) {
                    throw new RuntimeException("error parsing `elements.json`");
                }
                mGameCache.setElements(elements);

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<TowerType> getTowerType(@NonNull String towerTypeKey) {
        TowerType towerType = mGameCache.getTowerType(towerTypeKey);
        if (towerType != null) {
            return Single.just(towerType);
        }

        return setTowerTypesInCache()
            .andThen(Single.defer(() -> {
                TowerType newTowerType = mGameCache.getTowerType(towerTypeKey);
                if (newTowerType == null) {
                    throw new RuntimeException(
                        "error retrieving WeaponType with key `" + towerTypeKey + "`");
                }
                return Single.just(newTowerType);
            }));
    }

    @NonNull
    private Completable setTowerTypesInCache() {
        return Single.fromCallable(() -> readResourceFile(mApplicationContext, R.raw.towers))
            .flatMapCompletable(towerTypesJson -> {
                Type mapType = new TypeToken<Map<String, TowerType>>() {
                }.getType();
                Map<String, TowerType> towerTypes = mGson.fromJson(towerTypesJson, mapType);

                if (towerTypes == null) {
                    throw new RuntimeException("error parsing `towers.json`");
                }
                mGameCache.setTowerTypes(towerTypes);

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Drawable getDrawableFromResources(@DrawableRes int resourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mApplicationContext.getResources().getDrawable(resourceId, null);
        } else {
            //noinspection deprecation
            return mApplicationContext.getResources().getDrawable(resourceId);
        }
    }

    @NonNull
    public Completable setSaveGameState(@NonNull SavedGameState gameState) {
        return Completable.fromCallable(() -> {
            String gameStateJson = mGson.toJson(gameState);
            writeInternalStorageFile(mApplicationContext, SAVED_GAME_STATE_FILENAME, gameStateJson);
            return Completable.complete();
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Nullable
    public Single<SavedGameState> getSavedGameState() {
        if (!internalStorageFileExists(mApplicationContext, SAVED_GAME_STATE_FILENAME)) {
            return null;
        }

        return Single.fromCallable(() -> {
            String savedStateJson = readInternalStorageFile(mApplicationContext, SAVED_GAME_STATE_FILENAME);
            return mGson.fromJson(savedStateJson, SavedGameState.class);
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
