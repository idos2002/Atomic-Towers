package com.example.atomictowers.components.atoms;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.data.game.Element;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AtomSequencer {
    private static final String TAG = AtomSequencer.class.getSimpleName();

    public static class AtomSequenceItem {
        @SerializedName("atomicNumber")
        public int atomicNumber;

        @SerializedName("delay")
        public int delay;
    }

    private final Game mGame;
    private final List<AtomSequenceItem> mSequence;
    private final SparseArray<Element> mElements = new SparseArray<>();
    private final AtomicInteger mNumberOfCreatedAtoms = new AtomicInteger(0);

    private Disposable mInitElementsDisposable;
    private Disposable mSequencerDisposable;

    public AtomSequencer(@NonNull Game game, @NonNull List<Integer> elementsAtomicNumbers,
                         @NonNull List<AtomSequenceItem> sequence, int numberOfCreatedAtoms) {
        mGame = game;
        mSequence = sequence;
        mInitElementsDisposable = game.gameRepository.getElements().subscribe(elements -> {
            for (int atomicNumber : elementsAtomicNumbers) {
                mElements.append(atomicNumber, elements.get(atomicNumber));
            }
        }, Throwable::printStackTrace);
        mNumberOfCreatedAtoms.set(numberOfCreatedAtoms);
    }

    public int getNumberOfCreatedAtoms() {
        return mNumberOfCreatedAtoms.get();
    }

    public void start() {
        mSequencerDisposable = Observable.fromIterable(mSequence)
            .skip(mNumberOfCreatedAtoms.get())
            .concatMap(item -> Observable.just(item).delay(item.delay, TimeUnit.MILLISECONDS))
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                Element element = mElements.get(item.atomicNumber);
                if (mNumberOfCreatedAtoms.get() + 1 >= mSequence.size()) {
                    element.isLastAtom = true;
                }
                mGame.addComponent(Atom.class, element);
                mNumberOfCreatedAtoms.incrementAndGet();
            }, Throwable::printStackTrace, () -> Log.i(TAG, "All atoms created"));
    }

    public void pause() {
        if (mSequencerDisposable != null && !mSequencerDisposable.isDisposed()) {
            mSequencerDisposable.dispose();
        }
    }

    public void destroy() {
        if (mInitElementsDisposable != null && !mInitElementsDisposable.isDisposed()) {
            mInitElementsDisposable.dispose();
        }
        if (mSequencerDisposable != null && !mSequencerDisposable.isDisposed()) {
            mSequencerDisposable.dispose();
        }
    }
}
