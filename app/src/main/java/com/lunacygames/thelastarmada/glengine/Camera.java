package com.lunacygames.thelastarmada.glengine;

import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.player.PlayerList;
import com.lunacygames.thelastarmada.player.PlayerState;

/**
 * Created by zeus on 3/9/15.
 */
public class Camera {
    private static float[] pan;
    private static float[] oldPan;
    private static float[] panMax;

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
        switch(PlayerList.getState()) {
            case IDLE:
                break;
            case WALK_NORTH:
                pan[1] -= h/30.0f;

                if(pan[1] + sizeX <= oldPan[1]) {
                    pan[1] = oldPan[1] - sizeX;
                    PlayerList.setState(PlayerState.IDLE);
                }
                /* check whether we reached the top */
                if(pan[1] <= 0.0f) {
                    pan[1] = 0.0f;
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_SOUTH:
                pan[1] += h/30.0f;

                if(pan[1] - sizeX >= oldPan[1]) {
                    pan[1] = oldPan[1] + sizeX;
                    PlayerList.setState(PlayerState.IDLE);
                }

                /* check whether we reached the bottom */
                if(pan[1] >= panMax[1]) {
                    pan[1] = panMax[1];
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_EAST:
                pan[0] -= h/30.0f;

                if(pan[0] + sizeX <= oldPan[0]) {
                    pan[0] = oldPan[0] - sizeX;
                    PlayerList.setState(PlayerState.IDLE);
                }

                /* check whether we reached the far east side */
                if(pan[0] <= 0.0f) {
                    pan[0] = 0.0f;
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
            case WALK_WEST:
                pan[0] += h/30.0f;
                if(pan[0] - sizeX >= oldPan[0]) {
                    pan[0] = oldPan[0] + sizeX;
                    PlayerList.setState(PlayerState.IDLE);
                }
                /* check whether we reached the far west side */
                if(pan[0] >= panMax[0]) {
                    pan[0] = panMax[0];
                    PlayerList.setState(PlayerState.IDLE);
                }
                break;
        }
    }
}
