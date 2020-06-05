package com.example.atomictowers.screens.about;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.R;
import com.example.atomictowers.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);

        mBinding.backButton.setOnClickListener(
            view -> NavHostFragment.findNavController(this).popBackStack());

        // To allow for hyperlinks to redirect outside the app
        mBinding.creditsContentText.setMovementMethod(LinkMovementMethod.getInstance());

        return mBinding.getRoot();
    }
}