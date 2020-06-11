package com.example.atomictowers.screens.game;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.TowerType;
import com.example.atomictowers.data.game.game_state.SavedGameState;
import com.example.atomictowers.util.Vector2;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class GameViewModel extends ViewModel {
    private static final String TAG = GameViewModel.class.getSimpleName();

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public final Game game;

    public GameViewModel(@NonNull GameRepository gameRepository, @NonNull Vector2 gameDimensions,
                         @NonNull SavedGameState savedGameState) {
        Log.d(TAG, TAG + " created");
        game = new Game(gameRepository, gameDimensions, savedGameState);

        mCompositeDisposable.add(game.getHealthObservable()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(health -> mHealth.setValue(health), Throwable::printStackTrace));

        mCompositeDisposable.add(game.getEnergyObservable()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(energy -> mEnergy.setValue(energy), Throwable::printStackTrace));
    }

    private MutableLiveData<Integer> mHealth = new MutableLiveData<>(Game.MAX_HEALTH);

    public LiveData<Integer> getHealthPercent() {
        return Transformations.map(mHealth,
            health -> Math.round(((float) health / Game.MAX_HEALTH) * 100));
    }


    private MutableLiveData<Integer> mEnergy = new MutableLiveData<>(0);

    public LiveData<Integer> getEnergy() {
        return mEnergy;
    }


    private MutableLiveData<Boolean> mElectronShooterSelected = new MutableLiveData<>(false);

    public LiveData<Boolean> isElectronShooterSelected() {
        return mElectronShooterSelected;
    }

    public void selectElectronShooter() {
        mElectronShooterSelected.setValue(true);
        mPhotonicLaserSelected.setValue(false);
        game.selectTowerType(TowerType.ELECTRON_SHOOTER_TYPE_KEY);
    }


    private MutableLiveData<Boolean> mPhotonicLaserSelected = new MutableLiveData<>(false);

    public LiveData<Boolean> isPhotonicLaserSelected() {
        return mPhotonicLaserSelected;
    }

    public void selectPhotonicLaser() {
        mPhotonicLaserSelected.setValue(true);
        mElectronShooterSelected.setValue(false);
        game.selectTowerType(TowerType.PHOTONIC_LASER_TYPE_KEY);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        game.finish();
        Log.d(TAG, TAG + "cleared");
    }
}
