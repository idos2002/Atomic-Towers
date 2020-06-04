package com.example.atomictowers.screens.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atomictowers.R;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.databinding.FragmentGameBinding;
import com.example.atomictowers.util.Vector2;

import java.util.Objects;

import io.reactivex.disposables.Disposable;

/**
 * Container for the game
 */
public class GameFragment extends Fragment {
    private static final String TAG = GameFragment.class.getSimpleName();

    private GameViewModel mViewModel;
    private FragmentGameBinding mBinding;
    private Disposable mGamePausedSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mBinding.gameView.pause();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false);
        mBinding.setLifecycleOwner(this);

        // Make GameView lifecycle aware
        getViewLifecycleOwner().getLifecycle().addObserver(mBinding.gameView);

        Log.d(TAG, TAG + " created");

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GameRepository repository = GameRepository.getInstance(
                Objects.requireNonNull(getActivity()).getApplicationContext());

        // See why at:
        // https://stackoverflow.com/questions/16925317/getwidth-and-getheight-always-returning-0-custom-view/16925529
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                GameViewModelFactory factory = new GameViewModelFactory(repository,
                    new Vector2(mBinding.gameView.getWidth(), mBinding.gameView.getHeight()));

                mViewModel = new ViewModelProvider(GameFragment.this, factory).get(GameViewModel.class);

                mBinding.setGameViewModel(mViewModel);
                mBinding.gameView.setGame(mViewModel.game);

                setListeners();
            }
        });
    }

    private void setListeners() {
        mBinding.pauseButton.setOnClickListener(view -> mBinding.gameView.pause());

        mGamePausedSubscription = mViewModel.game.getGamePausedSubject()
            .subscribe(isPaused -> {
                if (isPaused) {
                    showGamePauseDialog();
                }
            }, Throwable::printStackTrace);
    }

    private void showGamePauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_game_pause, null);

        builder.setView(layout);

        Dialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        layout.findViewById(R.id.resume_button).setOnClickListener(view -> {
            dialog.dismiss();
            mBinding.gameView.resume();
        });

        layout.findViewById(R.id.home_button).setOnClickListener(view -> {
            dialog.dismiss();
            NavHostFragment.findNavController(this).popBackStack(R.id.mainFragment, false);
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mGamePausedSubscription.isDisposed()) {
            mGamePausedSubscription.dispose();
        }
    }
}
