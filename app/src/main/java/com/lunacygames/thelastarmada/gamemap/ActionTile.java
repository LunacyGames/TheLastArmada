package com.lunacygames.thelastarmada.gamemap;

/**
 * Created by tron on 4/19/15.
 */
public class ActionTile {
    private int x;
    private int y;
    private String actionScript;

    public ActionTile(int x, int y, String actionScript) {
        this.x = x;
        this.y = y;
        this.actionScript = actionScript;
    }

    public boolean hasAction(int x, int y) {
        return (this.x == x) && (this.y == y);
    }

    public String getActionScript() {
        return this.actionScript;
    }
}
