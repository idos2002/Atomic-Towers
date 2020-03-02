//package com.example.atomictowers.components.towers;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.example.atomictowers.components.Component;
//import com.example.atomictowers.components.atoms.Atom;
//import com.example.atomictowers.data.game.WeaponAbility;
//import com.example.atomictowers.util.Vector2;
//
//// TODO: Will be a json data file object
//public abstract class Weapon implements Component {
//
//    private final int mId;
//
//    protected final WeaponAbility ability;
//
//    // TODO: Didn't notice the TODO above, should remove this constructor
//    //  or create a data object in data.game package
//    public Weapon(int id, WeaponAbility ability) {
//        mId = id;
//        this.ability = ability;
//    }
//
//    public int getId() {
//        return mId;
//    }
//
//    abstract void damage(@NonNull Atom atom);
//
//    abstract void updateTarget(@Nullable Vector2 target);
//}
