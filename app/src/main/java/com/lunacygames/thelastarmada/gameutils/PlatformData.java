package com.lunacygames.thelastarmada.gameutils;

import android.util.Log;

/**
 * Created by tron on 2/24/15.
 */
public class PlatformData {
    private static int height;
    private static int width;

    public static void setScreenHeight(int h) {
        height = h;
        Log.d("Screen: ", "Height = " + h);

    }

    public static void setScreenWidth(int w) {
        width = w;
        Log.d("Screen: ", "Width = " + w);
    }

    public static int getScreenHeight() {
        return height;
    }

    public static int getScreenWidth() {
        return width;
    }
}
