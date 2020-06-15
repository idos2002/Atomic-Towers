package com.example.atomictowers.data.game.game_state;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Component;
import com.example.atomictowers.components.Game;
import com.example.atomictowers.components.atoms.Atom;
import com.example.atomictowers.components.towers.Tower;
import com.example.atomictowers.components.towers.weapons.KineticWeapon;
import com.example.atomictowers.data.game.Level;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedGameState implements Serializable {
    public SavedGameState(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public SavedGameState(int levelNumber, int health, int energy, int numberOfCreatedAtoms,
                          @NonNull SparseArray<Component> components) {
        this.levelNumber = levelNumber;
        this.health = health;
        this.energy = energy;
        this.numberOfCreatedAtoms = numberOfCreatedAtoms;
        initSavedStates(components);
    }

    private void initSavedStates(@NonNull SparseArray<Component> components) {
        for (int i = 0; i < components.size(); i++) {
            int key = components.keyAt(i);
            Component component = components.get(key);

            if (component == null || component instanceof KineticWeapon) {
                break;
            } else if (component instanceof Atom) {
                Atom atom = (Atom) component;
                atomSavedStates.add(new AtomSavedState(
                    atom.getAtomicNumber(),
                    atom.getStrength(),
                    atom.getPathIndex(),
                    atom.getPosition(),
                    atom.isLastAtom()));
            } else if (component instanceof Tower) {
                Tower tower = (Tower) component;
                towerSavedStates.add(new TowerSavedState(
                    tower.getTowerTypeKey(),
                    tower.getPosition()));
            }
        }
    }

    @SerializedName("level")
    public int levelNumber = Level.LEVEL_ONE;

    @SerializedName("health")
    public int health = Game.MAX_HEALTH;

    @SerializedName("energy")
    public int energy = Game.INITIAL_ENERGY;

    @SerializedName("numberOfCreatedAtoms")
    public int numberOfCreatedAtoms = 0;

    @SerializedName("componentSavedStates")
    public ArrayList<AtomSavedState> atomSavedStates = new ArrayList<>();

    @SerializedName("towerSavedState")
    public ArrayList<TowerSavedState> towerSavedStates = new ArrayList<>();
}
