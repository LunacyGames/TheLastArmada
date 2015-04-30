package com.lunacygames.thelastarmada.player;

/**
 * Created by zeus on 3/11/15.
 */
public class Magic {
    private String name;
    private String effect;

    /**
     * Initialize magic
     * @param name      Name of spell
     * @param effect    effect of spell
     */
    public Magic(String name, String effect) {
        this.name = name;
        this.effect = effect;
    }

    /**
     * @return          String effect of spell
     */
    public String getEffect() {
        return effect;
    }

    /**
     *
     * @return          String name of spell
     */
    public String getName() {
        return name;
    }

}
