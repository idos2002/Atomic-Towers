package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Used to cache JSON deserialization results for {@link GameRepository} after retrieval.
 */
public class GameCache {

    private List<AtomType> mElements;
    private List<AtomType> mIsotopes;

    @Nullable
    public List<AtomType> getElements() {
        return mElements;
    }

    @Nullable
    public List<AtomType> getIsotopes() {
        return mIsotopes;
    }

    public void setAtomTypes(@NonNull Map<String, List<AtomType>> typeMap) {
        mElements = typeMap.get(GameRepository.ELEMENTS_KEY);
        mIsotopes = typeMap.get(GameRepository.ISOTOPES_KEY);
    }
}
