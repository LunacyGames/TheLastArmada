package com.lunacygames.thelastarmada.player;

import com.lunacygames.thelastarmada.gameui.TopMessage;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by zeus on 3/10/15.
 */
public class Player {

    /* stat indexes */
    public static final int HP_STAT = 0;
    public static final int ATK_STAT = 1;
    public static final int DEF_STAT = 2;
    public static final int SATK_STAT = 3;
    public static final int RES_STAT = 4;
    public static final int SPD_STAT = 5;

    private final double BASE = 1.1;
    String name;

    private int[] base_stats;
    private double[] growth_percent;
    private int[] growth_rate;
    private int[] max_stats;
    private ArrayList<Magic> magic;
    private int activeTexture;

    ArrayList<int[]> texture;

    private int exp;
    private int level;


    /**
     * Create a Player.
     *
     *
     */
    public Player(String name, ArrayList<int[]> texture) {
        this.magic = new ArrayList<Magic>();
        this.name = name;
        this.texture = new ArrayList<>(texture);
        this.exp = exp;
        this.level = level;
    }

    public void setGrowthRate(int[] rate) {
        this.growth_rate = new int[6];
        System.arraycopy(rate, 0, this.growth_rate, 0, rate.length);
    }

    public void setMaxStats(int[] max_stats) {
        this.max_stats = new int[6];
        System.arraycopy(max_stats, 0, this.max_stats, 0, max_stats.length);
    }

    public void setBaseStats(int[] base_stats) {
        this.base_stats = new int[6];
        System.arraycopy(base_stats, 0, this.base_stats, 0, base_stats.length);
    }

    public void resetStats() {
        System.arraycopy(max_stats, 1, base_stats, 1, max_stats.length - 1);
    }

    public void setGrowthPercent(double[] growth_percent) {
        this.growth_percent = new double[6];
        System.arraycopy(growth_percent, 0, this.growth_percent, 0, growth_percent.length);
    }

    public void addMagic(Magic spell) {
        this.magic.add(spell);
    }

    public boolean hasMagic() {
        return !magic.isEmpty();
    }

    public int getHp() {
        return this.base_stats[HP_STAT];
    }

    public void setHp(int hp) {
        this.base_stats[HP_STAT] = hp;
    }

    public int getAtk() {
        return this.base_stats[ATK_STAT];
    }

    public void setAtk(int atk) {
        this.base_stats[ATK_STAT] = atk;
    }

    public int getSatk() {
        return this.base_stats[SATK_STAT];
    }

    public void setSatk(int satk) {
        this.base_stats[SATK_STAT] = satk;
    }

    public int getDef() {
        return this.base_stats[DEF_STAT];
    }

    public void setDef(int def) {
        this.base_stats[DEF_STAT] = def;
    }

    public int getRes() {
        return this.base_stats[RES_STAT];
    }

    public void setRes(int res) {
        this.base_stats[RES_STAT] = res;
    }

    public int getSpd() {
        return this.base_stats[SPD_STAT];
    }

    public void setSpd(int spd) {
        this.base_stats[SPD_STAT] = spd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax_hp() {
        return max_stats[HP_STAT];
    }

    public int[] getTexture() {
        return this.texture.get(activeTexture);
    }

    public ArrayList<Magic> getMagicList() {
        return magic;
    }

    public void addExp(int gain) {
        /* the higher the level, the less exp we get */
        int netGain = exp + (int)(gain / Math.pow(level, BASE));
        Random rng = new Random();
        while(netGain >= 100) {
            level++;
            TopMessage.showMessage(this.name + " is now level " + Integer.toString(level) + "!");
            netGain -= 100;
            /* on level up, we increase stats according to their growth rates and percents */
            for(int i = 0; i < 6; i++) {
                if(rng.nextDouble() < growth_percent[i])
                    /* if the Random Number Goddess favours the player,
                     * they get a nice stat boost */
                    max_stats[i] += growth_rate[i];
                else
                    /* otherwise, we are still nice to them, and give them a minor boost */
                    max_stats[i] += 1;
            }
        }
        exp = netGain;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int[] getBaseStats() {
        return base_stats;
    }

    public int[] getMaxStats() {
        return max_stats;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }
}
