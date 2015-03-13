package com.lunacygames.thelastarmada.gamebattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lunacygames.thelastarmada.gameutils.TextureHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zeus on 3/11/15.
 */
public class Enemy {

    private static ArrayList<Enemy> enemyList;
    private static int activeEnemies;

    private int[] texture;
    private ArrayList<String> actionList;
    private float[] size;
    private ArrayList<String> ondefeat;

    private String name;

    private int hp;
    private int atk;
    private int satk;
    private int def;
    private int res;
    private int spd;

    private int max_hp;
    private int max_atk;
    private int max_satk;


    private int max_def;
    private int max_res;
    private int max_spd;


    public Enemy(Context context, GL10 gl, String enemy) {
        try {
            InputStream data = context.getAssets().open("enemies/" + enemy + "/data");
            BufferedReader read = new BufferedReader(new InputStreamReader(data));
            /* enemy name */
            String s = read.readLine();
            this.name = s;
            /* enemy sprite */
            s = read.readLine();
            Bitmap bmp = TextureHandler.loadBitmap(context, s);
            texture = TextureHandler.createTexture(bmp, gl);
            /* size */
            s = read.readLine();
            String[] split = s.split(",");
            this.size = new float[2];
            this.size[0] = Float.parseFloat(split[0]);
            this.size[1] = Float.parseFloat(split[1]);

            /* available actions */
            s = read.readLine();
            actionList = new ArrayList<String>();
            for(String action : s.split(",")) {
                actionList.add(action);
            }

            /* HP */
            s = read.readLine(); this.hp = Integer.parseInt(s);
            /* attack */
            s = read.readLine(); this.atk = Integer.parseInt(s);
            /* defence */
            s = read.readLine(); this.def = Integer.parseInt(s);
            /* magic attack */
            s = read.readLine(); this.satk = Integer.parseInt(s);
            /* resistance */
            s = read.readLine(); this.res = Integer.parseInt(s);
            /* speed */
            s = read.readLine(); this.spd = Integer.parseInt(s);

            /* defeat script */
            s = read.readLine();
            ondefeat = new ArrayList<String>();
            for(String action : s.split(";")) {
                ondefeat.add(action);
            }

            /* max stats */
            this.max_hp = this.hp;
            this.max_atk = this.atk;
            this.max_satk = this.satk;
            this.max_def = this.def;
            this.max_res = this.res;
            this.max_spd = this.spd;

            /* avoid resource leak */
            read.close();
            Log.d("Enemy: ", "loaded enemy " + this.name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadEnemyList(Context context, GL10 gl, String[] enemies) {
        enemyList = new ArrayList<Enemy>();
        for(String enemy : enemies)
            enemyList.add(new Enemy(context, gl, enemy));

        activeEnemies = enemyList.size();

    }

    public static ArrayList<Enemy> getEnemyList() {
        return enemyList;
    }

    public static int getActiveEnemies() {
        return activeEnemies;
    }

    public float[] getSize() {
        return this.size;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getOnDefeatScript() {
        return this.ondefeat;
    }

    public int getHp() {
        return hp;
    }

    public int getAtk() {
        return atk;
    }

    public int getSatk() {
        return satk;
    }

    public int getDef() {
        return def;
    }

    public int getRes() {
        return res;
    }

    public int getSpd() {
        return spd;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public int getMax_atk() {
        return max_atk;
    }

    public int getMax_satk() {
        return max_satk;
    }

    public int getMax_def() {
        return max_def;
    }

    public int getMax_res() {
        return max_res;
    }

    public int getMax_spd() {
        return max_spd;
    }

    public int[] getTexture() {
        return texture;
    }

    public String getEnemyAction() {
        Random r = new Random();
        int i = r.nextInt(actionList.size());
        return actionList.get(i);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public static void reduceEnemyCount() {
        if(activeEnemies > 0)
            activeEnemies--;
    }

    public void setDef(int defence) {
        this.def = defence;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }
}
