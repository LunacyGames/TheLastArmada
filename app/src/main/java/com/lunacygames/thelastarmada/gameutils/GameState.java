package com.lunacygames.thelastarmada.gameutils;

/**
 * Created by zeus on 3/9/15.
 */
public class GameState {
    private static GameStateList gs;

    public static void setGameState(GameStateList gameState) {
        gs = gameState;
    }

    public static GameStateList getGameState() {
        return gs;
    }
}
