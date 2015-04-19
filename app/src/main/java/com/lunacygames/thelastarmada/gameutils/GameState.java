package com.lunacygames.thelastarmada.gameutils;

/**
 * Created by zeus on 3/9/15.
 */
public class GameState {
    private static GameStateList gs;

    private static int gameFlags = 0;
    private static int bossFlags = 0;
    private static int chestFlags = 0;

    public static int getGameFlags() {
        return gameFlags;
    }

    public static int getChestFlags() {
        return chestFlags;
    }

    public static int getBossFlags() {
        return bossFlags;
    }

    public static boolean getBossFlag(int flag) {
        return (bossFlags & flag) == flag;
    }

    public static void setBossFlags(int bossFlags) {
        GameState.bossFlags |= bossFlags;
    }

    public static boolean getChestFlag(int flag) {
        return (chestFlags & flag) == flag;
    }

    public static void setChestFlags(int chestFlags) {
        GameState.chestFlags |= chestFlags;
    }

    public static void setGameState(GameStateList gameState) {
        gs = gameState;
    }

    public static GameStateList getGameState() {
        return gs;
    }

    public static boolean getGameFlag(int flag) {
        return (gameFlags & flag) == flag;
    }

    public static void setGameFlags(int gameFlags) {
        GameState.gameFlags |= gameFlags;
    }
}
