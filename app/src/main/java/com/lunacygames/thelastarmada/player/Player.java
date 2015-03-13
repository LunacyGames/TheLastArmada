package com.lunacygames.thelastarmada.player;

import java.util.ArrayList;

/**
 * Created by zeus on 3/10/15.
 */
public class Player {
    String name;

    private int hp;
    private int atk;
    private int satk;
    private int def;
    private int res;
    private int spd;
    private int exp;

    private int max_hp;
    private int max_atk;
    private int max_satk;


    private int max_def;
    private int max_res;
    private int max_spd;

    private ArrayList<Magic> magic;

    int[] texture;
    /**
     * Create a Player.
     *
     *
     */
    public Player(String name, int[] texture, int max_hp, int hp, int atk, int satk, int def, int res, int spd) {
        this.magic = new ArrayList<Magic>();
        this.name = name;
        this.hp = hp;
        this.atk = atk;
        this.satk = satk;
        this.def = def;
        this.res = res;
        this.spd = spd;
        this.max_hp = max_hp;
        this.max_atk = atk;
        this.max_satk = satk;
        this.max_def = def;
        this.max_res = res;
        this.max_spd = spd;
        this.texture = texture;
    }

    public void addMagic(Magic spell) {
        this.magic.add(spell);
    }

    public boolean hasMagic() {
        return !magic.isEmpty();
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getSatk() {
        return satk;
    }

    public void setSatk(int satk) {
        this.satk = satk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public void setMax_hp(int max_hp) {
        this.max_hp = max_hp;
    }

    public int getMax_atk() {
        return max_atk;
    }

    public void setMax_atk(int max_atk) {
        this.max_atk = max_atk;
    }

    public int getMax_satk() {
        return max_satk;
    }

    public void setMax_satk(int max_satk) {
        this.max_satk = max_satk;
    }

    public int getMax_def() {
        return max_def;
    }

    public void setMax_def(int max_def) {
        this.max_def = max_def;
    }

    public int getMax_res() {
        return max_res;
    }

    public void setMax_res(int max_res) {
        this.max_res = max_res;
    }

    public int getMax_spd() {
        return max_spd;
    }

    public void setMax_spd(int max_spd) {
        this.max_spd = max_spd;
    }

    public int[] getTexture() {
        return this.texture;
    }

    public ArrayList<Magic> getMagicList() {
        return magic;
    }

    public void addExp(int gain) {

    }
}
