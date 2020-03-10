package com.example.atomictowers.screens.game;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.util.Vector2;

import java.lang.reflect.InvocationTargetException;

public class GameViewModelFactory implements ViewModelProvider.Factory {

    private final GameRepository mGameRepository;
    private final Vector2 mGameDimensions;

    public GameViewModelFactory(@NonNull GameRepository gameRepository, @NonNull Vector2 gameDimensions) {
        mGameRepository = gameRepository;
        mGameDimensions = gameDimensions;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(GameRepository.class, Vector2.class)
                    .newInstance(mGameRepository, mGameDimensions);
        } catch (IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot create an instance of " + modelClass, e);
        }
    }
}
