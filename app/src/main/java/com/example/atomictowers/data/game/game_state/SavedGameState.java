package com.example.atomictowers.data.game.game_state;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.Tower;
import com.example.atomictowers.components.towers.weapons.Weapon;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedGameState implements Serializable {
    public SavedGameState(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public SavedGameState(int levelNumber, @NonNull SparseArray<Component> components) {
        this.levelNumber = levelNumber;
        initSavedStates(components);
    }

    private void initSavedStates(@NonNull SparseArray<Component> components) {
        for (int i = 0; i < components.size(); i++) {
            int key = components.keyAt(i);
            Component component = components.get(key);

            if (component == null || component instanceof Weapon) {
                break;
            } else if (component instanceof Atom) {
                Atom atom = (Atom) component;
                atomSavedStates.add(new AtomSavedState(
                    atom.getAtomicNumber(),
                    atom.getStrength(),
                    atom.getPathIndex(),
                    atom.getPosition()));
            } else if (component instanceof Tower) {
                Tower tower = (Tower) component;
                towerSavedStates.add(new TowerSavedState(
                    tower.getTowerTypeKey(),
                    tower.getPosition()));
            }
        }
    }

    @SerializedName("level")
    public int levelNumber;

    @SerializedName("componentSavedStates")
    public ArrayList<AtomSavedState> atomSavedStates = new ArrayList<>();

    @SerializedName("towerSavedState")
    public ArrayList<TowerSavedState> towerSavedStates = new ArrayList<>();
}
