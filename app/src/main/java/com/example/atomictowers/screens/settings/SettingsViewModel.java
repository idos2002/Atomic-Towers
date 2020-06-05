package com.example.atomictowers.screens.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final MutableLiveData<Integer> mVolumePercentage = new MutableLiveData<>();

    public MutableLiveData<Integer> getVolumePercentage() {
        return mVolumePercentage;
    }

    private final MutableLiveData<Boolean> mSaveVolumePreferenceEvent = new MutableLiveData<>();

    public LiveData<Boolean> getSaveVolumePreferenceEvent() {
        return mSaveVolumePreferenceEvent;
    }

    public void saveVolumePreference() {
        mSaveVolumePreferenceEvent.setValue(true);
    }

    public void saveVolumePreferenceComplete() {
        mSaveVolumePreferenceEvent.setValue(false);
    }
}
