package com.example.atomictowers.screens.game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.atomictowers.R;
import com.example.atomictowers.data.game.GameRepository;
import com.example.atomictowers.databinding.FragmentGameBinding;
import com.example.atomictowers.util.Vector2;

import java.util.Objects;

/**
 * Container for the game
 */
public class GameFragment extends Fragment {

    private static final String TAG = GameFragment.class.getSimpleName();

    private GameViewModel mViewModel;

    private FragmentGameBinding mBinding;

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

                mViewModel = ViewModelProviders.of(GameFragment.this, factory).get(GameViewModel.class);

                mBinding.gameView.setGame(mViewModel.game);
            }
        });
    }
}
