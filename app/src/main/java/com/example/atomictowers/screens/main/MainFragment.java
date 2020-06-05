package com.example.atomictowers.screens.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.R;
import com.example.atomictowers.databinding.FragmentMainBinding;

/**
 * The game's main menu.
 */
public class MainFragment extends Fragment {
    private MainViewModel mViewModel;
    private FragmentMainBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mBinding.setLifecycleOwner(this);

        mBinding.startButton.setOnClickListener(
            view -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mainFragment_to_gameFragment));

        mBinding.menuButton.setOnClickListener(this::showPopup);

        return mBinding.getRoot();
    }

    private void showPopup(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.settings:
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_mainFragment_to_settingsFragment);
                    return true;
                case R.id.instructions:
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_mainFragment_to_instructionsFragment);
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }
}
