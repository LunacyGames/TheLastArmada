package com.lunacygames.thelastarmada.gameutils;

import android.util.Log;

/**
 * Created by tron on 2/24/15.
 */
public class PlatformData {
    private static int height;
    private static int width;

    private static float tilesize;

    public static void setScreenHeight(int h) {
        height = h;
        Log.d("Screen: ", "Height = " + h);

    }

    public static void setScreenWidth(int w) {
        width = w;
        Log.d("Screen: ", "Width = " + w);
    }

    public static void calculateTileSize() {
        tilesize = getScreenWidth() / 11.0f;
    }

    public static int getScreenHeight() {
        return height;
    }

    public static int getScreenWidth() {
        return width;
    }

    public static float getTileSize() {
        return tilesize;
    }
}
