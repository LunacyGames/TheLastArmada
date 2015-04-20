package com.lunacygames.thelastarmada.glengine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.gameui.TopMessage;
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
        /* set context for Sound Engine */
        SoundEngine.getInstance().setContext(context);
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
        if(TopMessage.isShown())
            TopMessage.onMotionEvent(e);
        else
            UIHandler.onMotionEvent(e, x, y);

        return true;
    }
}
