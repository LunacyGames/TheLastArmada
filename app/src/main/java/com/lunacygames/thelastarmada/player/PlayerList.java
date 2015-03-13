package com.lunacygames.thelastarmada.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lunacygames.thelastarmada.gamemap.MapEntity;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zeus on 3/10/15.
 */
public class PlayerList {

    private static PlayerState state;
    private static ArrayList<Player> players;
    static MapEntity m;
    private static int player;

    public static void initPlayerList(Context context, GL10 gl) {
        players = new ArrayList<Player>();
        String[] names = { "Lothbrok", "Sigurd", "Brynhild", "Aslaug"};
        int[] texture;
        Bitmap bmp;
        /* base stats */
        int[][] base_stats = {
                {30, 4, 4, 6, 2, 5},
                {28, 5, 5, 5, 2, 6},
                {25, 4, 6, 3, 6, 7},
                {22, 4, 6, 3, 8, 8}};

        int[][] growth_rate = {
                {5, 4, 3, 1, 2, 2},
                {4, 3, 3, 1, 3, 3},
                {3, 2, 2, 4, 3, 3},
                {4, 1, 1, 4, 4, 3}};

        double[][] growth_percent = {
                {0.50, 0.80, 0.50, 0.20, 0.30, 0.80},
                {0.60, 0.60, 0.60, 0.10, 0.60, 0.60},
                {0.60, 0.10, 0.50, 0.70, 0.55, 0.55},
                {0.50, 0.05, 0.50, 0.70, 0.60, 0.55}
        };

        Magic[] a_spell = new Magic[]{new Magic("Heal", "H"), new Magic("Nova", "N")};
        Magic[] b_spell = new Magic[]{new Magic("Fire", "F"), new Magic("Ice", "I")};

        ArrayList<int[]> textureList;
        /* Totals:   51, 51, 51, 51 */
        for(int i = 0; i < 4; i++) {
            bmp = TextureHandler.loadBitmap(context,
                    "characters/" + names[i].toLowerCase() + "_battle.png");
            texture = TextureHandler.createTexture(bmp, gl);
            textureList = new ArrayList<>();
            textureList.add(texture);
            Player p = new Player(names[i], textureList, 0, 1);
            for(int j = 0; j < base_stats[i].length; j++)
                Log.d("Player: ", "adding base stat " + i + " " + base_stats[i][j]);
            p.setBaseStats(base_stats[i]);
            p.setMaxStats(base_stats[i]);
            p.setGrowthRate(growth_rate[i]);
            p.setGrowthPercent(growth_percent[i]);
            players.add(p);
        }
        for(Magic m : a_spell)
            players.get(3).addMagic(m);

        for(Magic m : b_spell)
            players.get(2).addMagic(m);

        loadPlayerSprite(context, gl);
        PlayerList.state = PlayerState.IDLE;
    }

    public static void loadPlayerSprite(Context context, GL10 gl) {
        Bitmap bmp = TextureHandler.loadBitmap(context, "characters/lothbrok.png");
        ArrayList<Bitmap> sprites = TextureHandler.loadTiles(bmp, 4, 3);
        ArrayList<int[]> textures = new ArrayList<int[]>();
        for(Bitmap b : sprites) {
            textures.add(TextureHandler.createTexture(b, gl));
        }
        float sizeX = PlatformData.getScreenWidth()/11.0f;
        float[] size = {sizeX, sizeX};
        float hpos = (PlatformData.getScreenWidth() - sizeX) / 2.0f;
        float vpos = (float)Math.floor(PlatformData.getScreenHeight() / (2.0f * sizeX));
        Log.d("PlayerSprite: ", "Player sprite at " + hpos + ", " + (vpos*sizeX));
        m = new MapEntity(textures.get(0), hpos, vpos * sizeX, size);
    }

    public static void onDraw(GL10 gl) {
        int width = PlatformData.getScreenWidth();
        int height = PlatformData.getScreenHeight();
        /* set up the window */
        gl.glOrthof(0.0f, width, height, 0.0f, -1.0f, 0.0f);
        /* load an identity matrix onto the stack */
        gl.glPushMatrix();
        gl.glLoadIdentity();
        m.onDraw(gl);
        gl.glPopMatrix();
        /* and pop it back out */
    }

    public static void setPlayer(int p) {
        player = p;
    }

    public static int getPlayer() {
        return player;
    }
    public static ArrayList<Player> getPlayerList() {
        return players;
    }

    public static PlayerState getState() {
        return state;
    }

    public static void setState(PlayerState state) {
        PlayerList.state = state;
    }
}
