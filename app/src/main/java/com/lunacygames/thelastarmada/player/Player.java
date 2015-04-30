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
     * @param name String name of character
     * @param texture ArrayList of character's sprites
     *
     */
    public Player(String name, ArrayList<int[]> texture) {
        this.magic = new ArrayList<Magic>();
        this.name = name;
        this.texture = new ArrayList<int[]>(texture);
        this.exp = exp;
        this.level = level;
    }

    /**
     * set the rate of stat growth for the player
     * @param rate TODO: what are these?
     */
    public void setGrowthRate(int[] rate) {
        this.growth_rate = new int[6];
        System.arraycopy(rate, 0, this.growth_rate, 0, rate.length);
    }

    /**
     * Set the permanent stats of player
     * @param max_stats character's permanent stats
     */
    public void setMaxStats(int[] max_stats) {
        this.max_stats = new int[6];
        System.arraycopy(max_stats, 0, this.max_stats, 0, max_stats.length);
    }

    /**
     * Set base stats of player
     * @param base_stats character's stats that change with battle
     */
    public void setBaseStats(int[] base_stats) {
        this.base_stats = new int[6];
        System.arraycopy(base_stats, 0, this.base_stats, 0, base_stats.length);
    }

    /**
     * Reset base stats to max stats(usually after potion effect)
     */
    public void resetStats() {
        System.arraycopy(max_stats, 0, base_stats, 0, max_stats.length);
    }

    /**
     * TODO: what does this do?
     * @param growth_percent
     */
    public void setGrowthPercent(double[] growth_percent) {
        this.growth_percent = new double[6];
        System.arraycopy(growth_percent, 0, this.growth_percent, 0, growth_percent.length);
    }

    /**
     * Add spell to character's known spells list
     * @param spell magic to add to character
     */
    public void addMagic(Magic spell) {
        this.magic.add(spell);
    }

    /**
     * Determines if character is a caster
     * @return true if character has spells, false if not
     */
    public boolean hasMagic() {
        return !magic.isEmpty();
    }

    /**
     * Return character's HP
     * @return HP of character
     */
    public int getHp() {
        return this.base_stats[HP_STAT];
    }

    /**
     * set character's HP
     * @param hp health
     */
    public void setHp(int hp) {
        this.base_stats[HP_STAT] = hp;
    }

    /**
     * Retrun character's attack
     * @return ATK of character
     */
    public int getAtk() {
        return this.base_stats[ATK_STAT];
    }

    /**
     * set character's ATK
     * @param atk physical attack
     */
    public void setAtk(int atk) {
        this.base_stats[ATK_STAT] = atk;
    }

    /**
     * get character's SATK, their magic attack
     * @return int for SATK
     */
    public int getSatk() {
        return this.base_stats[SATK_STAT];
    }

    /**
     * Set character's SATK
     * @param satk magic attack
     */
    public void setSatk(int satk) {
        this.base_stats[SATK_STAT] = satk;
    }

    /**
     * Return character's defense
     * @return int defense of character
     */
    public int getDef() {
        return this.base_stats[DEF_STAT];
    }

    /**
     * Set character's defense value
     * @param def value to set def to
     */
    public void setDef(int def) {
        this.base_stats[DEF_STAT] = def;
    }

    /**
     * Return character's magic resistance
     * @return int character's magic resist
     */
    public int getRes() {
        return this.base_stats[RES_STAT];
    }

    /**
     * Set character's resistance to magic
     * @param res value to set RES to
     */
    public void setRes(int res) {
        this.base_stats[RES_STAT] = res;
    }

    /**
     * Return character's speed value
     * @return int character's speed
     */
    public int getSpd() {
        return this.base_stats[SPD_STAT];
    }

    /**
     * Set character's speed value
     * @param spd value to set speed to
     */
    public void setSpd(int spd) {
        this.base_stats[SPD_STAT] = spd;
    }

    /**
     * Get character's name
     * @return String character's name
     */
    public String getName() {
        return name;
    }

    /**
     * set the character's name
     * @param name name to set character's name to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get character's max HP
     * @return int character's max HP
     */
    public int getMax_hp() {
        return max_stats[HP_STAT];
    }

    /**
     * Get character's texture
     * @return int[] for character's texture
     */
    public int[] getTexture() {
        return this.texture.get(activeTexture);
    }

    /**
     * Get character's magic list
     * @return ArrayList of character's magic
     */
    public ArrayList<Magic> getMagicList() {
        return magic;
    }

    /**
     * add EXP to character
     * @param gain Base value of monster's EXP reward
     */
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

    /**
     * Set character's level
     * @param level int to set character's level to
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * TODO: What does this do?
     * @param exp
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     * Get character's base stats
     * @return int[] of character's base stats
     */
    public int[] getBaseStats() {
        return base_stats;
    }

    /**
     * Get character's max stats
     * @return int[] of character's max stats
     */
    public int[] getMaxStats() {
        return max_stats;
    }

    /**
     * Get character's level
     * @return int character's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * TODO: what does this do?
     * @return
     */
    public int getExp() {
        return exp;
    }
}
