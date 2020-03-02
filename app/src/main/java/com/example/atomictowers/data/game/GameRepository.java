package com.example.atomictowers.data.game;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.atomictowers.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.atomictowers.util.Util.readResourceFile;

public class GameRepository {

    private static final String TAG = GameRepository.class.getSimpleName();

    private volatile static GameRepository INSTANCE;

    static final String LEVELS_KEY = "levels";
    static final String ISOTOPE_LEVELS_KEY = "isotope_levels";

    static final String ELEMENTS_KEY = "elements";
    static final String ISOTOPES_KEY = "isotopes";

    private Context mApplicationContext;

    private Gson mGson = new Gson();

    private GameCache mGameCache;

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
    public Single<List<Level>> getLevels() {
        if (mGameCache.getLevels() != null) {
            return Single.just(mGameCache.getLevels());
        }

        return setAllLevelsInCache()
            .andThen(Single.defer(() -> Single.just(mGameCache.getLevels())));
    }

    @NonNull
    public Single<List<Level>> getIsotopeLevels() {
        if (mGameCache.getIsotopeLevels() != null) {
            return Single.just(mGameCache.getIsotopeLevels());
        }

        return setAllLevelsInCache()
            .andThen(Single.defer(() -> Single.just(mGameCache.getIsotopeLevels())));
    }

    @NonNull
    private Completable setAllLevelsInCache() {
        return Single.fromCallable(() -> readResourceFile(mApplicationContext, R.raw.levels))
            .flatMapCompletable(allLevelsJson -> {
                Type mapType = new TypeToken<Map<String, List<Level>>>() {
                }.getType();
                Map<String, List<Level>> allLevelsMap = mGson.fromJson(allLevelsJson, mapType);

                mGameCache.setAllLevels(allLevelsMap);

                return Completable.complete();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<List<AtomType>> getElements() {
        if (mGameCache.getElements() != null) {
            return Single.just(mGameCache.getElements());
        }

        return setAtomTypesInCache()
            .andThen(Single.defer(() -> Single.just(mGameCache.getElements())));
    }

    @NonNull
    public Single<List<AtomType>> getIsotopes() {
        if (mGameCache.getIsotopes() != null) {
            return Single.just(mGameCache.getIsotopes());
        }

        return setAtomTypesInCache()
            .andThen(Single.defer(() -> Single.just(mGameCache.getIsotopes())));
    }

    @NonNull
    private Completable setAtomTypesInCache() {
        return Completable.fromRunnable(() -> {
            String atomTypesJson = null;
            try {
                atomTypesJson = readResourceFile(mApplicationContext, R.raw.atom_types);
                Log.d(TAG, "reading `atom_types.json` resource file...");
            } catch (IOException e) {
                Log.e(TAG, "error reading `atom_types.json` from resources: ", e);
            }

            Type mapType = new TypeToken<Map<String, List<AtomType>>>() {
            }.getType();

            Map<String, List<AtomType>> atomTypes = mGson.fromJson(atomTypesJson, mapType);

            mGameCache.setAtomTypes(atomTypes);
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
