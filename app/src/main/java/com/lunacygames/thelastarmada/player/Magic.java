package com.lunacygames.thelastarmada.player;

/**
 * Created by zeus on 3/11/15.
 */
public class Magic {
    private String name;
    private String effect;

    public Magic(String name, String effect) {
        this.name = name;
        this.effect = effect;
    }

    public String getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

}
