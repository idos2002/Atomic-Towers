package com.example.atomictowers.screens.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.atomictowers.components.Game;
import com.example.atomictowers.util.Vector2;

// TODO: Handle animations - would be better to animate the other fragments entrance
//  and exit in a way that would seem like the GameView is animated,
//  like a growing circle from the center and such.
//  A loading screen could be a good solution!
public class GameView extends SurfaceView implements Runnable, LifecycleObserver {

    private static final String TAG = GameView.class.getSimpleName();

    private static final int MAX_FPS = 60;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;

    private final SurfaceHolder mSurfaceHolder;

    private Thread mGameThread;
    private boolean mRunning;

    private Game mGame;

    public GameView(Context context) {
        this(context, null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();

        // TODO: Change coordinates to pathMatrix or something else that describes
        //  the background and its path for atoms
        Log.d(TAG, "GameView created");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            throw new IllegalStateException("GameView must not have exact width");
        } else {
            height = heightSize;
            width = (int) Game.getDimensionsByScreenHeight(heightSize).x;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.i(TAG, "onSizeChanged() called");

        if (mGame != null) {
            mGame.updateDimensions(new Vector2(w, h));
        }
    }

    /**
     * Contains the game loop, which updates and renders (draws) the game to the GameView's canvas,
     * as well as keep the game at a constant game speed through all devices, as defined with the
     * game's {@linkplain #MAX_FPS}.
     */
    @Override
    public void run() {
        if (mGame != null) {
            mGame.resume();
        }

        Canvas canvas;
        long previousFrameTime = System.currentTimeMillis();

        while (mRunning) {
            if (mSurfaceHolder.getSurface().isValid()) {
                try {
                    long beginTime = System.currentTimeMillis();
                    canvas = mSurfaceHolder.lockCanvas();

                    // Measure the frame time (see: https://stackoverflow.com/questions/24561596/smoothing-out-android-game-loop)
                    long currentTime = System.currentTimeMillis();
                    long frameTime = currentTime - previousFrameTime;
                    previousFrameTime = currentTime;
                    mGame.update(frameTime / 1000.0f);

                    mGame.draw(canvas);
                    mSurfaceHolder.unlockCanvasAndPost(canvas);

                    long sleepTime = FRAME_PERIOD - (System.currentTimeMillis() - beginTime);

                    // Delay the thread to maintain a constant frame rate (`FRAME_PERIOD`)
                    // for each frame
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pause();
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause() {
        if (!mGame.isGamePaused()) {
            mGame.pause();
        }
        mRunning = false;
        try {
            // Stop the thread (rejoin the main thread)
            mGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "pause() called");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
        Log.d(TAG, "resume() called");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGame != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            mGame.putTowerOnMap(convertTouchCoordinatesToTileIndex(event.getX(), event.getY()));
            return true;
        }
        return false;
    }

    @NonNull
    private Vector2 convertTouchCoordinatesToTileIndex(float x, float y) {
        float tileSize = mGame.getTileSize();
        float xIndex = ((x - x % tileSize) / tileSize);
        float yIndex = ((y - y % tileSize) / tileSize);
        return new Vector2(xIndex, yIndex);
    }

    public void setGame(@NonNull Game game) {
        mGame = game;
    }
}
