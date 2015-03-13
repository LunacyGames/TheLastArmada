package com.lunacygames.thelastarmada.gameutils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Orlando Arias
 *
 * With additions by Stanislav
 */
public class TextureHandler {
    private static final int WidgetColor = 0x7f0000ff;

    public enum TextAlign {
        ALIGN_LEFT,
        ALIGN_RIGHT,
        ALIGN_CENTER
    }

    /**
     * Create a texture from a bitmap.
     * @param bmp   Bitmap image to create a texture from.
     * @param gl    OpenGL ES context to use
     * @return      Texture generated from the bitmap.
     */
    public static int[] createTexture(Bitmap bmp, GL10 gl) {
        int[] texture = new int[1];
        /* create a texture pointer */
        gl.glGenTextures(1, texture, 0);
        /* and bind it */
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

        /* create a nearest filtered texture */
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        /* Create 2D texture image */
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

        return texture;
    }

    /**
     * Load a bitmap from the assets
     * @param context   Application context.
     * @param file      Path and filename to the bitmap.
     * @return          The loaded bitmap, if successful, null otherwise.
     */
    public static Bitmap loadBitmap(Context context, String file) {
        AssetManager assets = context.getAssets();


        InputStream stream;
        Bitmap bitmap = null;
        try {
            stream = assets.open(file);
            bitmap = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            /* again, screw it */
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Split a bitmap into a set of tiles.
     * @param tileset   Bitmap containint the tileset.
     * @param hTiles    Number of horizontal tiles.
     * @param vTiles    Number of vertical tiles.
     * @return          An ArrayList of bitmaps containing all sprites in the tileset.
     */
    public static ArrayList<Bitmap> loadTiles(Bitmap tileset, int hTiles, int vTiles) {
        ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();
        /* split the entire thing */
        for(int x = 0; x < hTiles; x++) {
            for(int y = 0; y < vTiles; y++) {
                bmpList.add(Bitmap.createBitmap(tileset, y * 64, x * 64, 64, 64));
            }
        }

        return bmpList;
    }

    /**
     * Create a texture from a string.
     * @param gl        OpenGL ES context to use
     * @param str       String
     * @param height    Height of the texture
     * @param width     Width of the texture
     * @param align     Text alignment
     * @return          Created texture.
     */
    public static int[] createTextureFromString(GL10 gl,
                                              String str, int height, int width, TextAlign align) {
        /* create a bitmap */
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(width, height, config);
        /* a canvas and a paint */
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        /* container for measurements */
        Rect bounds = new Rect();
        /* set paint properties */
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(0.75*(height - 4)));

        /* get bounds of text box */
        paint.getTextBounds(str, 0, str.length(), bounds);
        /* set canvas background */
        canvas.drawColor(WidgetColor);

        /* and draw text */
        switch(align) {
            case ALIGN_LEFT:
                canvas.drawText(str, 4, (height + bounds.height()) / 2, paint);
                break;
            case ALIGN_RIGHT:
                canvas.drawText(str, width - bounds.width() - 4,
                        (height + bounds.height()) / 2, paint);
                break;
            case ALIGN_CENTER:
                canvas.drawText(str, (width - bounds.width() - 4)/2,
                        (height + bounds.height()) / 2, paint);
                break;
        }
        /* return the texture generated from the bitmap */
        return createTexture(bmp, gl);
    }
}
