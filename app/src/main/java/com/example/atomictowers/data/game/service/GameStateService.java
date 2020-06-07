package com.example.atomictowers.data.game.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.atomictowers.data.game.GameRepository;

import java.util.Objects;

import io.reactivex.disposables.Disposable;

public class GameStateService extends Service {
    private static final String TAG = GameStateService.class.getSimpleName();
    public static final String GAME_STATE_INTENT_EXTRA_NAME = "game_state_extra_key";

    private Disposable saveStateDisposable;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        if (intent == null || intent.getExtras() == null) {
            Log.e(TAG, "Intent passed to service is null or has no extras");
            return START_NOT_STICKY;
        }
        SavedGameState savedGameState = (SavedGameState) Objects.requireNonNull(intent.getExtras())
            .getSerializable(GAME_STATE_INTENT_EXTRA_NAME);

        if (savedGameState != null) {
            saveStateDisposable = GameRepository.getInstance(getApplicationContext())
                .saveGameState(savedGameState)
                .subscribe(this::stopSelf, Throwable::printStackTrace);
        } else {
            Log.e(TAG, "intent passes to service is null");
            return START_NOT_STICKY;
        }

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (saveStateDisposable != null && !saveStateDisposable.isDisposed()) {
            saveStateDisposable.dispose();
        }

        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }
}
