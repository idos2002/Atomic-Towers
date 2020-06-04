package com.example.atomictowers.screens.instructions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.R;
import com.example.atomictowers.databinding.FragmentInstructionsBinding;

public class InstructionsFragment extends Fragment {
    FragmentInstructionsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_instructions, container, false);

        mBinding.backButton.setOnClickListener(
            view -> NavHostFragment.findNavController(this).popBackStack());

        return mBinding.getRoot();
    }
}