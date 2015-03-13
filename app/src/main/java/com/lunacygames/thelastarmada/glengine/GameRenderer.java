package com.lunacygames.thelastarmada.glengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.text.format.Time;

import com.lunacygames.thelastarmada.gamemap.MapType;
import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
import com.lunacygames.thelastarmada.gamebattle.ActionEvent;
import com.lunacygames.thelastarmada.gamebattle.BattleManager;
import com.lunacygames.thelastarmada.gamebattle.BattleState;
import com.lunacygames.thelastarmada.gamebattle.Enemy;
import com.lunacygames.thelastarmada.gamemap.MapEntity;
import com.lunacygames.thelastarmada.gamemap.MapLoader;
import com.lunacygames.thelastarmada.gameui.UIHandler;
import com.lunacygames.thelastarmada.gameui.UIList;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;
import com.lunacygames.thelastarmada.player.Player;
import com.lunacygames.thelastarmada.player.PlayerList;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tron on 2/15/15.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private ArrayList<MapEntity> map;
    private Context context;
    private int fps;
    private long seconds;
    Time c;
    private int pan_direction;

    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        fps = 0;
        c = new Time();
        c.setToNow();
        seconds = c.toMillis(false);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // gl.glViewport(-width, -height, width *2, height *2);
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix
        gl.glOrthof(0.0f, width, height, 0.0f, -1.0f, 0.0f);

        //Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.0f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        fps++;
        c.setToNow();
        long s = c.toMillis(false);
        if(s - seconds >= 1000) {
            // Log.d("framerate: ", fps + "fps");
            fps = 0;
            seconds = s;
        }
        switch(GameState.getGameState()) {
            case INIT:
                Bitmap bitmap = TextureHandler.loadBitmap(context, "titlescreen/titlescreen.png");

                int[] title = TextureHandler.createTexture(bitmap, gl);
                float size[] =
                        {bitmap.getWidth(), bitmap.getHeight()};
                Camera.setMaxPan(size);
                Camera.setDefaultPan();
                pan_direction = 0;
                /* we need the background to scroll, so we make it a map entity */
                MapEntity m = new MapEntity(title, 0, 0, size);
                map = new ArrayList<MapEntity>();
                map.add(m);

                /* set up the UI */
                UIHandler.setActive(UIList.START);
                UIHandler.loadUI(context, gl);

                /* initialize the players */
                PlayerList.initPlayerList(context, gl);
                GameState.setGameState(GameStateList.TITLE_SCREEN);
                break;
            case TITLE_SCREEN:
                renderScreen(gl);
                break;
            case LOAD_MAP:
                MapLoader.setActiveMap(MapType.OVERWORLD);
                map = MapLoader.loadMap(context, gl);
            case LOAD_OVERWORLD_UI:
            case BATTLE_VICTORY:
                UIHandler.setActive(UIList.OVERWORLD);
                UIHandler.loadUI(context, gl);
                GameState.setGameState(GameStateList.OVERWORLD);
                break;
            case OVERWORLD:
                renderScreen(gl);
                break;
            case TO_BATTLE:
                BattleManager.reset();
                ActionEvent.emptyActionQueue();
                String[] enemies = { "fenrir" };
                for(Player p : PlayerList.getPlayerList()) {
                    p.resetStats();
                }
                Enemy.loadEnemyList(context, gl, enemies);
                UIHandler.setActive(UIList.BATTLE);
                UIHandler.loadUI(context, gl);
                GameState.setGameState(GameStateList.BATTLE);
                break;
            case BATTLE:
                UIHandler.refresh(gl);
                if(BattleManager.getState() == BattleState.START)
                    BattleManager.updateState(0, null);
                renderScreen(gl);
                break;
        }
    }

    private void renderScreen(GL10 gl) {

        /* redraw background */
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        float[] camera = Camera.getPan();

        switch(GameState.getGameState()) {
            case TITLE_SCREEN: {
                float[] maxPan = Camera.getMaxPan();
                gl.glTranslatef(-camera[0], -camera[1], 0.0f);
                switch(pan_direction) {
                    case 0:
                        camera[0] += PlatformData.getScreenWidth()/100;
                        if(camera[0] >= maxPan[0]) {
                            camera[0] = maxPan[0];
                            pan_direction = 1;
                        }
                        break;
                    case 1:
                        camera[1] += PlatformData.getScreenHeight()/100;
                        if(camera[1] >= maxPan[1]) {
                            camera[1] = maxPan[1];
                            pan_direction = 2;
                        }
                        break;
                    case 2:
                        camera[0] -= PlatformData.getScreenWidth()/100;
                        if(camera[0] <= 0) {
                            camera[0] = 0;
                            pan_direction = 3;
                        }
                        break;
                    default:
                        camera[1] -= PlatformData.getScreenHeight()/100;
                        if(camera[1] <= 0) {
                            camera[1] = 0;
                            pan_direction = 0;
                        }
                    break;
                }
                Camera.setPan(camera);
            }

        }
        // gl.glTranslatef(50, 50, -5);
        // gl.glTranslatef(-100*(1 + (float)Math.cos(t)), -100*(1 + (float)Math.sin(t)), 0.0f);
        // gl.glRotatef(180 * t/ (float)Math.PI, 0, 0, 5);
        // t += Math.PI/100;

        if(GameState.getGameState() == GameStateList.OVERWORLD) {
            gl.glTranslatef(-camera[0], -camera[1], 0.0f);
            Camera.update();
        }

        for(MapEntity e : map)
            e.onDraw(gl);

        if(GameState.getGameState() == GameStateList.OVERWORLD)
            PlayerList.onDraw(gl);

        UIHandler.drawUI(gl);

    }
}
