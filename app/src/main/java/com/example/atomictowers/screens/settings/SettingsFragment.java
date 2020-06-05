package com.example.atomictowers.screens.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.MainActivity;
import com.example.atomictowers.R;
import com.example.atomictowers.databinding.FragmentSettingsBinding;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    private SettingsViewModel mViewModel;
    private FragmentSettingsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        mBinding.backButton.setOnClickListener(
            view -> NavHostFragment.findNavController(this).popBackStack());

        initVolumeSettings();

        return mBinding.getRoot();
    }

    private void initVolumeSettings() {
        mViewModel.getVolumePercentage().setValue(getVolumePercentageFromPreferences());

        mViewModel.getVolumePercentage()
            .observe(getViewLifecycleOwner(), volumePercentage -> {
                float volume = volumePercentage / 100f;
                MainActivity.mediaPlayer.setVolume(volume, volume);
            });

        mViewModel.getSaveVolumePreferenceEvent()
            .observe(getViewLifecycleOwner(), shouldSave -> {
                if (shouldSave) {
                    saveVolumePreference();
                    mViewModel.saveVolumePreferenceComplete();
                }
            });
    }

    private int getVolumePercentageFromPreferences() {
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        float volume = sharedPref.getFloat(
            getString(R.string.key_music_volume), MainActivity.DEFAULT_MUSIC_VOLUME);
        return (int) (volume * 100);
    }

    private void saveVolumePreference() {
        Integer volumePercentage = mViewModel.getVolumePercentage().getValue();

        if (volumePercentage != null) {
            SharedPreferences sharedPref = Objects.requireNonNull(getActivity())
                .getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putFloat(getString(R.string.key_music_volume), volumePercentage / 100f);

            editor.apply();
        }
    }
}
