package com.lunacygames.thelastarmada.glengine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.gameui.UIHandler;

/**
 * Created by tron on 2/15/15.
 */
public class GameSurface extends GLSurfaceView {
    private final GameRenderer renderer;

    public GameSurface(Context context) {
        super(context);

        /* get a new renderer and attach it */
        renderer = new GameRenderer(context);

        /* set render mode to continuous */
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 1);
        // setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        this.setPreserveEGLContextOnPause(true);
        this.setRenderer(renderer);
    }

    @Override
    public void onPause() {
        super.onPause();
        // renderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // renderer.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        UIHandler.onClick(e, x, y);
        return true;
    }
}
