package com.example.atomictowers.screens.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.drawables.GameBackgroundDrawable;

// TODO: Handle animations - would be better to animate the other fragments entrance
//  and exit in a way that would seem like the GameView is animated,
//  like a growing circle from the center and such.
public class GameView extends SurfaceView implements Runnable, LifecycleObserver {

    private static final String TAG = GameView.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;

    private Thread mGameThread;
    private boolean mRunning;

    private Drawable mBackgroundDrawable;

    private Game mGame;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();

        // Bounds will be set in the onSizeChanged method
        mBackgroundDrawable = new GameBackgroundDrawable(9, 6);
        // TODO: Change coordinates to pathMatrix or something else that describes
        //  the background and its path for atoms

        Log.d(TAG, "GameView created");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBackgroundDrawable.setBounds(0, 0, w, h);

        if (mGame != null) {
            mGame.updateDimensions(w, h);
        }
    }

    /**
     * Contains all of the drawing code, used to draw to the view's surface.
     */
    @Override
    public void run() {
        Canvas canvas;

        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    int saveCount = canvas.save();

                    canvas.drawColor(Color.WHITE);
                    mBackgroundDrawable.draw(canvas);

                    mGame.update();
                    mGame.draw(canvas);

                    canvas.restoreToCount(saveCount);
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                    pause();
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause() {
        mRunning = false;
        try {
            // Stop the thread (rejoin the main thread)
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "pause() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();

        Log.d(TAG, "resume() called");
    }

    public void setGame(@NonNull Game game) {
        mGame = game;
    }
}
