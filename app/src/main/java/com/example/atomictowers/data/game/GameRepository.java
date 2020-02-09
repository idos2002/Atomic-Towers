package com.example.atomictowers.data.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.atomictowers.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameRepository {

    private volatile static GameRepository INSTANCE;

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

    @SuppressLint("CheckResult")
    @NonNull
    public Single<List<AtomType>> getElements() {
        if (mGameCache.getElements() != null) {
            return Single.just(mGameCache.getElements());
        }

        return setAtomTypesInCache().andThen(Single.defer(() -> Single.just(mGameCache.getElements())));
    }

    @SuppressLint("CheckResult")
    @NonNull
    public Single<List<AtomType>> getIsotopes() {
        if (mGameCache.getIsotopes() != null) {
            return Single.just(mGameCache.getIsotopes());
        }

        return setAtomTypesInCache().andThen(Single.defer(() -> Single.just(mGameCache.getIsotopes())));
    }

    private Completable setAtomTypesInCache() {
        return Completable.fromRunnable(() -> {
            String atomTypesJson = null;
            try {
                atomTypesJson = readResourceFile(R.raw.atom_types);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("GameRepository", atomTypesJson);

            Type mapType = new TypeToken<Map<String, List<AtomType>>>() {
            }.getType();

            Map<String, List<AtomType>> atomTypes = mGson.fromJson(atomTypesJson, mapType);
            Log.i("GameRepository2", atomTypes.toString());

            mGameCache.setAtomTypes(atomTypes);
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private String readResourceFile(int resourceId) throws IOException {
        InputStream inputStream = mApplicationContext.getResources().openRawResource(resourceId);

        StringBuffer buffer = new StringBuffer();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        return buffer.toString();
    }
}
