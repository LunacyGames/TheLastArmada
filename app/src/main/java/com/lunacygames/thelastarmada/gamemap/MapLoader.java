package com.lunacygames.thelastarmada.gamemap;

import android.content.Context;
import android.graphics.Bitmap;

import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;
import com.lunacygames.thelastarmada.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;

/**
 * Map loader class.
 *
 * This class decodes and load all map related assets from their corresponding files and provides
 * auxiliary functions for maps.
 * @author Orlando Arias
 */
public class MapLoader {

    private static MapType amap;

    /**
     * Load active map from a file.
     *
     * @param context   Application context.
     * @param gl        OpenGL ES environment to use.
     * @return          An ArrayList of MapEntities which can be rendered on screen if the map was
     * loaded. Null otherwise.
     */
    public static ArrayList<MapEntity> loadMap(Context context, GL10 gl) {
        InputStream csv = null;
        /* select the map to load */
        switch(amap) {
            case OVERWORLD:
                csv = context.getResources().openRawResource(R.raw.map1);
                break;
        }
        ArrayList<MapEntity> map = new ArrayList<MapEntity>();
        /* start reading from file, hopefully... */
        BufferedReader reader = new BufferedReader(new InputStreamReader(csv));
        try {
            /* our file specification is simple, the first line is the tileset file */
            String s = reader.readLine();
            Bitmap tileset = TextureHandler.loadBitmap(context, s);
            /* the second line is the grid of tiles in the tileset */
            String dimension[] = reader.readLine().split(",");
            int hTiles = Integer.parseInt(dimension[0]);
            int vTiles = Integer.parseInt(dimension[1]);
            /* split the tiles */
            ArrayList<Bitmap> tiles = TextureHandler.loadTiles(tileset, hTiles, vTiles);
            /* and make them into textures */
            ArrayList<int[]> textures = new ArrayList<int[]>();
            for(Bitmap b : tiles) {
                textures.add(TextureHandler.createTexture(b, gl));
            }
            /* then, the next line is the starting position on the map */
            dimension = reader.readLine().split(",");
            /* subsequent lines are tiles on the map */
            float sizeX = (float) PlatformData.getScreenWidth() / 11.0f;
            float size[] = {sizeX, sizeX};
            /* we have enough data to calculate initial camera position */
            float camera[] = {sizeX * Integer.parseInt(dimension[0]) - sizeX/2,
                    sizeX * Integer.parseInt(dimension[1]) - sizeX/2};
            int x, y;
            x = 0; y = 0;
            int horizontal = 0;
            /* java... */
            while((s=reader.readLine())!=null && s.length()!=0) {
                for(String t : s.split(",")) {
                    /* obtain the corresponding texture ID */
                    int textureID[] = textures.get(Integer.parseInt(t));
                    /* make a map entity */
                    MapEntity m = new MapEntity(textureID, x * sizeX, y * sizeX, size);
                    /* and add it to the map */
                    map.add(m);
                    x++;
                    if(horizontal < x) horizontal++;
                }
                x = 0;
                y++;
            }
            /* compute the maximum camera position */
            float[] cameraPan = {sizeX * horizontal, sizeX * y};
            /* set the camera position */
            Camera.setPan(camera);
            /* and compute its boundaries */
            Camera.setMaxPan(cameraPan);
        } catch (IOException e) {
            /* screw it... this means we messed up somewhere in the resource creation
             * that, or the platform is broken
             */
            e.printStackTrace();
            return null;
        }
        try {
            reader.close();
        } catch (IOException e) {
            /* java... */
            e.printStackTrace();
        }
        return map;
    }

    public static void setActiveMap(MapType activeMap) {
        amap = activeMap;
    }

    public static MapType getActiveMap() {
        return amap;
    }
}
