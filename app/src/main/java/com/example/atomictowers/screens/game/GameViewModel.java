package com.example.atomictowers.screens.game;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.game_state.SavedGameState;
import com.example.atomictowers.util.Vector2;

public class GameViewModel extends ViewModel {
    private static final String TAG = GameViewModel.class.getSimpleName();

    public final Game game;

    public GameViewModel(@NonNull GameRepository gameRepository, @NonNull Vector2 gameDimensions,
                         @NonNull SavedGameState savedGameState) {
        Log.d(TAG, TAG + " created");
        game = new Game(gameRepository, gameDimensions, savedGameState);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        game.finish();
        Log.d(TAG, TAG + "cleared");
    }
}
