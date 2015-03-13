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
        int[] hp =  {30, 28, 25, 22};
        int[] def =  {4,  5,  4,  4};
        int[] res =  {4,  5,  6,  6};
        int[] atk =  {6,  5,  3,  3};
        int[] satk = {2,  2,  6,  8};
        int[] spd =  {5,  6,  7,  8};

        Magic[] a_spell = new Magic[]{new Magic("Heal", "H"), new Magic("Nova", "N")};
        Magic[] b_spell = new Magic[]{new Magic("Fire", "F"), new Magic("Ice", "I")};

        /* Totals:   51, 51, 51, 51 */
        for(int i = 0; i < 4; i++) {
            bmp = TextureHandler.loadBitmap(context,
                    "characters/" + names[i].toLowerCase() + "_battle.png");
            texture = TextureHandler.createTexture(bmp, gl);
            Player p = new Player(names[i], texture, hp[i] , hp[i], atk[i], satk[i], def[i], res[i], spd[i]);
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
