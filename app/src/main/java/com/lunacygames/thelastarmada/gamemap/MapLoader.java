package com.lunacygames.thelastarmada.gamemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lunacygames.thelastarmada.gameutils.Interpreter;
import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;
import com.lunacygames.thelastarmada.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

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
    private static ArrayList<Boolean> mapWall;
    private static ArrayList<String> monsterList;
    private static ArrayList<ActionTile> actionTiles;
    private static int horizontal;
    private static String battleground;
    private static String bgMusic;

    /**
     * Load active map from a file.
     *
     * @param context   Application context.
     * @param gl        OpenGL ES environment to use.
     * @return          An ArrayList of MapEntities which can be rendered on screen if the map was
     * loaded. Null otherwise.
     */
    public static ArrayList<ArrayList<MapEntity>> loadMap(Context context, GL10 gl) {
        InputStream csv = null;
        /* select the map to load */
        switch(amap) {
            case OVERWORLD:
                csv = context.getResources().openRawResource(R.raw.map1);
                break;
            case DUNGEON:
                csv = context.getResources().openRawResource(R.raw.map2);
                break;
        }
        /* java does not allow to make an array of generics without employing hacks */
        ArrayList<ArrayList<MapEntity>> map = new ArrayList<ArrayList<MapEntity>>();
        /* object list for second layer */
        mapWall = new ArrayList<Boolean>();
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
            /* the next three lines are the map layer files */
            InputStream file;
            for(int i = 0; i < 3; i++) {
                s = reader.readLine();
                Log.d("MapLoad:", "Loading layer " + s);
                file = context.getAssets().open(s);
                map.add(loadLayer(file, textures, i == 1));
                /* deal with potential resource leak */
                file.close();
            }
            /* the next line is the monster list */
            s = reader.readLine();
            monsterList = new ArrayList<String>();
            for(String str : s.split(","))
                monsterList.add(str);
            /* the next line is file with the action tiles */
            s = reader.readLine();
            file = context.getAssets().open(s);
            loadActions(file);
            /* the next line is the battleground */
            battleground = reader.readLine();
            /* the next line is the background music */
            bgMusic = reader.readLine();
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

    private static void loadActions(InputStream file) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String s;
        String split[];
        ActionTile tile;
        actionTiles = new ArrayList<ActionTile>();
        try {
            while((s = reader.readLine()) != null && s.length() != 0) {
                split = s.split(",");
                tile = new ActionTile(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]), split[2]);
                actionTiles.add(tile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setActiveMap(MapType activeMap) {
        amap = activeMap;
    }

    public static MapType getActiveMap() {
        return amap;
    }

    public static String getBattleground() {
        return battleground;
    }

    public static String getBgMusic() {
        return bgMusic;
    }

    private static ArrayList<MapEntity>
    loadLayer(InputStream file, ArrayList<int[]> textures, boolean fillObjectList) {
        ArrayList<MapEntity> layer = new ArrayList<MapEntity>();
        int x, y;
        x = 0; y = 0;
        horizontal = 0;
        String s;
        float sizeX = (float) PlatformData.getScreenWidth() / 11.0f;
        float size[] = {sizeX, sizeX};

        BufferedReader reader = new BufferedReader(new InputStreamReader(file));

        try {
            int i;
            while((s=reader.readLine())!=null && s.length()!=0) {
                for(String t : s.split(",")) {
                    /* obtain the corresponding texture ID */
                    i = Integer.parseInt(t);
                    if(i == -1) {
                        layer.add(null);
                    } else {
                        int textureID[] = textures.get(i);
                        /* make a map entity */
                        MapEntity m = new MapEntity(textureID, x * sizeX, y * sizeX, size);
                        /* and add it to the map */
                        layer.add(m);
                    }
                    if(fillObjectList) mapWall.add(i != -1);
                    x++;
                    if(horizontal < x) horizontal++;
                }
                x = 0;
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* compute the maximum camera position */
        float[] cameraPan = {sizeX * horizontal, sizeX * y};
        /* and set it */
        Camera.setMaxPan(cameraPan);

        return layer;
    }

    public static String getRandomEnemy() {
        Random rng = new Random();
        return monsterList.get(rng.nextInt(monsterList.size()));
    }

    public static boolean hasObject(int x, int y) {
        boolean objectPresent;
        /* we allow to walk on action tiles even if they are blocked */
        objectPresent = mapWall.get(x + horizontal * y) & (!tileHasAction(x, y));
        return objectPresent;
    }

    public static boolean tileHasAction(int x, int y) {
        for(ActionTile tile : actionTiles) {
            if(tile.hasAction(x, y))
                return true;
        }

        return false;
    }

    public static boolean actionHandler(int x, int y) {
        for(ActionTile tile : actionTiles) {
            if(tile.hasAction(x, y))
                return Interpreter.execScript(tile.getActionScript());
        }
        return false;
    }

    public static int getCurrentMapCode() {
        switch(amap) {
            case VILLAGE:
                return 2;
            case DUNGEON:
                return 1;
            case OVERWORLD:
                return 0;
        }
        return 1;
    }

    public static MapType getMapType(int i) {
        switch(i) {
            case 0:
                return MapType.OVERWORLD;
            case 1:
                return MapType.DUNGEON;
            case 2:
                return MapType.VILLAGE;
        }
        return MapType.DUNGEON;
    }
}
