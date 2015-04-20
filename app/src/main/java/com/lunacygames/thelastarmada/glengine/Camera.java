package com.lunacygames.thelastarmada.glengine;

import android.util.Log;

import com.lunacygames.thelastarmada.gamebattle.ActionEvent;
import com.lunacygames.thelastarmada.gamebattle.BattleManager;
import com.lunacygames.thelastarmada.gamebattle.Enemy;
import com.lunacygames.thelastarmada.gamemap.MapLoader;
import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.player.Player;
import com.lunacygames.thelastarmada.player.PlayerList;
import com.lunacygames.thelastarmada.player.PlayerState;

import java.util.Random;

/**
 * Created by zeus on 3/9/15.
 */
public class Camera {
    private static float[] pan;
    private static float[] oldPan;
    private static float[] panMax;
    private static int[] position;
    private static int[] old_position;
    private static float delta;

    public static void setMaxPan(float[] max) {
        float x, y;
        x = max[0] - PlatformData.getScreenWidth();
        y = max[1] - PlatformData.getScreenHeight();
        float[] p = {x, y};
        panMax = p;
    }

    public static void setPan(float[] p) {
        pan = p;
    }

    public static void lockPan() {
        oldPan = pan.clone();
        delta = 0;
        old_position = position.clone();
    }

    public static void setDefaultPan() {
        float[] p = {(float)PlatformData.getScreenWidth(), (float)PlatformData.getScreenHeight()};
        pan = p;
        oldPan = p;
    }

    public static float[] getPan() {
        return pan;
    }

    public static float[] getMaxPan() {
        return panMax;
    }

    /**
     * Update the camera position relative to the player's movement. We use this shenanigan
     * to emulate movement on the map.
     */
    public static void update() {
        /* we use the height to compute the scroll speed */
        int h = PlatformData.getScreenHeight();
        float sizeX = PlatformData.getScreenWidth() / 11.0f;
        boolean onNewTile = false;
        switch(PlayerList.getState()) {
            case IDLE:
                return;
            case WALK_NORTH:
                if(MapLoader.hasObject(position[0], position[1] - 1)) {
                    PlayerList.setState(PlayerState.IDLE);
                    return;
                }

                pan[1] -= h/30.0f;

                if(pan[1] + sizeX <= oldPan[1]) {
                    pan[1] = oldPan[1] - sizeX;
                    position[1]--;
                    onNewTile = true;
                    checkContinuousWalk();
                }

                /* check whether we reached the top */
                if(pan[1] <= 0.0f) {
                    pan[1] = 0.0f;
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_SOUTH:
                if(MapLoader.hasObject(position[0], position[1] + 1)) {
                    PlayerList.setState(PlayerState.IDLE);
                    return;
                }

                pan[1] += h/30.0f;

                if(pan[1] - sizeX >= oldPan[1]) {
                    pan[1] = oldPan[1] + sizeX;
                    position[1]++;
                    onNewTile = true;
                    checkContinuousWalk();
                }

                /* check whether we reached the bottom */
                if(pan[1] >= panMax[1]) {
                    pan[1] = panMax[1];
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_EAST:
                if(MapLoader.hasObject(position[0] - 1, position[1])) {
                    PlayerList.setState(PlayerState.IDLE);
                    return;
                }
                pan[0] -= h/30.0f;

                if(pan[0] + sizeX <= oldPan[0]) {
                    pan[0] = oldPan[0] - sizeX;
                    position[0]--;
                    onNewTile = true;
                    checkContinuousWalk();
                }

                /* check whether we reached the far east side */
                if(pan[0] <= 0.0f) {
                    pan[0] = 0.0f;
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_WEST:
                if(MapLoader.hasObject(position[0] + 1, position[1])) {
                    PlayerList.setState(PlayerState.IDLE);
                    return;
                }
                pan[0] += h/30.0f;
                if(pan[0] - sizeX >= oldPan[0]) {
                    pan[0] = oldPan[0] + sizeX;
                    position[0]++;
                    onNewTile = true;
                    checkContinuousWalk();
                }
                /* check whether we reached the far west side */
                if(pan[0] >= panMax[0]) {
                    pan[0] = panMax[0];
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
        }


        /* check if we finished walking into a tile */
        if(onNewTile) {
            /* check if the tile we are in has events */
            if(MapLoader.tileHasAction(position[0], position[1])) {
                /* force the player into an idle state */
                PlayerList.setPlayerWalking(false);
                PlayerList.setState(PlayerState.IDLE);
                /* trigger them */
                MapLoader.actionHandler(position[0], position[1]);
                /* we don't trigger random battles if we are on an action tile */
                return;
            }
            /* check if we enter into a battle */
            if(!checkIfBattle(0.2f)) return;

            /* force the player into an idle state */
            PlayerList.setPlayerWalking(false);
            PlayerList.setState(PlayerState.IDLE);


            /* hosekeeping tasks to start the battle */
            BattleManager.reset();
            Enemy.resetEnemyQueue();
            ActionEvent.emptyActionQueue();

            /* get a random enemy for the map and add it to the queue */
            Enemy.addEnemy(MapLoader.getRandomEnemy());

            /* check if we can add a second enemey */
            if(checkIfBattle(0.5f)) Enemy.addEnemy(MapLoader.getRandomEnemy());

            /* reset player stats before battle */
            for(Player p : PlayerList.getPlayerList()) {
                p.resetStats();
            }

            /* we are going to fight stuff! */
            GameState.setGameState(GameStateList.TO_BATTLE_EFFECT);
        }
    }

    private static void checkContinuousWalk() {
        if(!PlayerList.isPlayerWalking()) {
            PlayerList.setState(PlayerState.IDLE);
        } else {
            lockPan();
        }
    }

    public static void setPosition(int[] position) {
        /* need to transform the absolute map position into a screen relative position */
        float sizeX = PlatformData.getTileSize();
        int x, y, offset;
        x = position[0] - 5;
        offset = (int)(PlatformData.getScreenHeight() / sizeX);
        Log.d("Camera: ", "vertical tiles " + offset);
        y = position[1] - offset/2;
        Camera.position = position;
        pan = new float[]{x * sizeX, y * sizeX};

    }

    public static int[] getPosition() {
        return position;
    }

    private static boolean checkIfBattle(float probability) {
        /* The Random Number Goddess decides */
        Random rnd = new Random();
        for(int i = 0; i < 3; i++)
            if(rnd.nextFloat() > probability) return false;

        return true;
    }
}
