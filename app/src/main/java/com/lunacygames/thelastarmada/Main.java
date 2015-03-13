package com.lunacygames.thelastarmada;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.glengine.GameSurface;

public class Main extends Activity {

    private GameSurface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Main: ", "Starting application");

        /* full screen application, no title bar */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            );

        /* we gather some screen data and cache it so that we don't have to create objects
         * whenever we need it
         */
        Display d = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        PlatformData.setScreenHeight(size.y);
        PlatformData.setScreenWidth(size.x);
        /* initial game state */
        GameState.setGameState(GameStateList.INIT);


        surface = new GameSurface(this);
        this.setContentView(surface);
    }

    @Override
    protected void onPause() {
        super.onPause();
        surface.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface.onResume();
    }

}
