package com.example.atomictowers.screens.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.R;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.data.game.Level;
import com.example.atomictowers.data.game.game_state.SavedGameState;
import com.example.atomictowers.databinding.FragmentMainBinding;
import com.example.atomictowers.screens.main.MainFragmentDirections.ActionMainFragmentToGameFragment;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

/**
 * The game's main menu.
 */
public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentMainBinding mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false);
        mBinding.setLifecycleOwner(this);

        mBinding.startButton.setOnClickListener(view -> {
            GameRepository repository = GameRepository.getInstance(
                Objects.requireNonNull(getActivity()).getApplicationContext());

            mCompositeDisposable.add(repository.hasSavedGameState().subscribe(hasSavedGameState -> {
                if (hasSavedGameState) {
                    mCompositeDisposable.add(repository.getSavedGameState().subscribe(
                        this::showResumeLastGameDialog,
                        Throwable::printStackTrace));
                } else {
                    ActionMainFragmentToGameFragment action =
                        MainFragmentDirections.actionMainFragmentToGameFragment(
                            new SavedGameState(Level.LEVEL_ONE)); // TODO: Update this!
                    NavHostFragment.findNavController(this).navigate(action);
                }
            }, Throwable::printStackTrace));
        });

        mBinding.menuButton.setOnClickListener(this::showPopupMenu);

        return mBinding.getRoot();
    }

    private void showPopupMenu(View view) {
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
                case R.id.about:
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_mainFragment_to_aboutFragment);
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private void showResumeLastGameDialog(@NonNull SavedGameState savedGameState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_resume_last_game, null);

        builder.setView(layout);

        Dialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        layout.findViewById(R.id.resume_button).setOnClickListener(view -> {
            ActionMainFragmentToGameFragment action =
                MainFragmentDirections.actionMainFragmentToGameFragment(savedGameState);
            NavHostFragment.findNavController(this).navigate(action);
            dialog.dismiss();
        });

        layout.findViewById(R.id.new_game_button).setOnClickListener(view -> {
            ActionMainFragmentToGameFragment action =
                MainFragmentDirections.actionMainFragmentToGameFragment(
                    new SavedGameState(Level.LEVEL_ONE)); // TODO: Update this!
            NavHostFragment.findNavController(this).navigate(action);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
}
