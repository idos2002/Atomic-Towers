package com.example.atomictowers.data.game;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Used to cache JSON deserialization results for {@link GameRepository} after retrieval.
 */
class GameCache {

    private List<Level> mLevels;
    private List<Element> mElements;
    private Map<String, TowerType> mTowerTypes;

    @Nullable
    Level getLevel(int level) {
        if (mLevels == null) {
            return null;
        }

        return new Level(mLevels.get(level));
    }

    void setLevels(@NonNull List<Level> levels) {
        mLevels = levels;
    }

    @Nullable
    List<Element> getElements() {
        return mElements;
    }

    @Nullable
    Element getElement(int atomicNumber) {
        if (mElements == null) {
            return null;
        }

        return new Element(mElements.get(atomicNumber));
    }

    void setElements(@NonNull List<Element> elements) {
        mElements = elements;
    }

    @Nullable
    TowerType getTowerType(@NonNull String towerTypeKey) {
        if (mTowerTypes == null) {
            return null;
        }
        return new TowerType(Objects.requireNonNull(mTowerTypes.get(towerTypeKey)));
    }

    void setTowerTypes(@NonNull Map<String, TowerType> towerTypes) {
        mTowerTypes = towerTypes;
    }
}
