package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Used to cache JSON deserialization results for {@link GameRepository} after retrieval.
 */
class GameCache {

    private List<Level> mLevels;
    private List<Level> mIsotopeLevels;

    private List<AtomType> mElements;
    private List<AtomType> mIsotopes;

    private Map<String, WeaponType> mWeaponTypes;

    @Nullable
    List<Level> getLevels() {
        return mLevels;
    }

    @Nullable
    List<Level> getIsotopeLevels() {
        return mIsotopeLevels;
    }

    void setAllLevels(@NonNull Map<String, List<Level>> allLevelsMap) {
        mLevels = allLevelsMap.get(GameRepository.LEVELS_KEY);
        mIsotopeLevels = allLevelsMap.get(GameRepository.ISOTOPE_LEVELS_KEY);
    }

    @Nullable
    List<AtomType> getElements() {
        return mElements;
    }

    @Nullable
    List<AtomType> getIsotopes() {
        return mIsotopes;
    }

    void setAtomTypes(@NonNull Map<String, List<AtomType>> typeMap) {
        mElements = typeMap.get(GameRepository.ELEMENTS_KEY);
        mIsotopes = typeMap.get(GameRepository.ISOTOPES_KEY);
    }

    @Nullable
    WeaponType getWeaponType(@NonNull String weaponTypeKey) {
        if (mWeaponTypes == null) {
            return null;
        }
        return mWeaponTypes.get(weaponTypeKey);
    }

    void setWeaponTypes(@NonNull Map<String, WeaponType> weaponTypes) {
        mWeaponTypes = weaponTypes;
    }
}
