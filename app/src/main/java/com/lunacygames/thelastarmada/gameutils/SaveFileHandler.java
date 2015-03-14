package com.lunacygames.thelastarmada.gameutils;

import android.content.Context;
import android.util.Log;

import com.lunacygames.thelastarmada.gamemap.MapLoader;
import com.lunacygames.thelastarmada.gamemap.MapType;
import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.player.Inventory;
import com.lunacygames.thelastarmada.player.Player;
import com.lunacygames.thelastarmada.player.PlayerList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Save file management class.
 *
 * @author Orlando Arias
 */
public class SaveFileHandler {
    private static final String filename = "savefile.sav";

    /**
     * Checks whether the save file exists or not.
     *
     * @param context   Application context.
     * @return Returns true if the save file was found.
     */
    public static boolean saveExists(Context context) {
        boolean retVal = true;
        FileInputStream f;
        try {
            f = context.openFileInput(filename);
            f.close();
        } catch (Exception e) {
            retVal = false;
        }
        return retVal;
    }

    /**
     * Creates a stock save file.
     *
     * @param context   Application context.
     */
    public static void createStockSaveFile(Context context) {
        int[][] baseStats = {
                {30, 4, 4, 6, 2, 5},        /* Lothbrok */
                {28, 5, 5, 5, 2, 6},        /* Sigurd */
                {25, 4, 6, 3, 6, 7},        /* Brynhild */
                {22, 4, 6, 3, 8, 8}};       /* Aslaug */

        try {
            FileOutputStream f = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter fOut = new OutputStreamWriter(f);

            /* first we save the player's stats */
            for(int[] stats : baseStats) {
                savePlayerData(fOut, stats);   /* one time for current stats */
                savePlayerData(fOut, stats);   /* one time for base stats */
                fOut.write("1\n");    /* character level */
                fOut.write("0\n");    /* experience */
            }
            /* then the current position */
            fOut.write("6,6\n");
            /* then the current map */
            fOut.write("0\n");
            /* then, the game status */
            fOut.write("0\n");
            /* then, the inventory list */
            for(int i = 0; i < Inventory.MAX_ITEM_NUMBER; i++)
                fOut.write("5\n");
            /* lastly, close the file */
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a current save file.
     *
     * @param context   Application context.
     */
    public static void storeSaveFile(Context context) {
        try {
            FileOutputStream f = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter fOut = new OutputStreamWriter(f);
            /* save the player stats */
            for(Player p : PlayerList.getPlayerList()) {
                savePlayerData(fOut, p.getBaseStats());         /* current stats */
                savePlayerData(fOut, p.getMaxStats());          /* max stats */
                fOut.write(Integer.toString(p.getLevel()) + "\n");     /* current level */
                fOut.write(Integer.toString(p.getExp()) + "\n");       /* experience points */
            }
            /* then the current position */
            int[] position = Camera.getPosition();
            fOut.write(
                        Integer.toString(position[0]) +
                        "," +
                        Integer.toString(position[1]) +
                        "\n"
                    );
            /* then the current map */
            switch(MapLoader.getActiveMap()) {
                case OVERWORLD:
                    fOut.write("0\n");
                    break;
            }
            /* then the game status */
            fOut.write(Integer.toString(GameState.getGameFlags()) + "\n");
            /* then, the item list */
            for(int i = 0; i < Inventory.MAX_ITEM_NUMBER; i++) {
                fOut.write(Inventory.getItemCount(i) + "\n");
            }
            /* finally close the file */
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the current save file. The player list must be pre-populated before this function
     * is called. The item list must be initialized. The file must also exist.
     *
     * @param context   Application context.
     */
    public static void loadSaveFile(Context context) {
        try {
            FileInputStream f = context.openFileInput(filename);
            BufferedReader fileIn = new BufferedReader(new InputStreamReader(f));
            /* player stats */
            for(Player p : PlayerList.getPlayerList()) {
                p.setBaseStats(loadPlayerData(fileIn));      /* first the character stats */
                p.setMaxStats(loadPlayerData(fileIn));       /* then the max stats */
                p.setLevel(Integer.parseInt(fileIn.readLine()));    /* then the level */
                p.setExp(Integer.parseInt(fileIn.readLine()));      /* then the experience */
            }
            /* camera position */
            String s = fileIn.readLine();
            int[] position = new int[2];
            int i = 0;
            for(String coord : s.split(","))
                position[i++] = Integer.parseInt(coord);
            Camera.setPosition(position);
            /* current map */
            s = fileIn.readLine();
            switch(s) {
                case "0":
                    MapLoader.setActiveMap(MapType.OVERWORLD);
                    break;
                default:
                    Log.d("Loading: ", "Invalid map type");
            }
            /* game status */
            s = fileIn.readLine();
            GameState.setGameFlags(Integer.parseInt(s));
            /* the rest is the inventory list */
            for(i = 0; i < Inventory.MAX_ITEM_NUMBER; i++) {
                s = fileIn.readLine();
                Inventory.setItemCount(i, Integer.parseInt(s));
            }

            /* done loading, close file */
            fileIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves player stats as a list of comma separated values.
     * @param f     File to save into.
     * @param data  int array with player stats.
     * @throws IOException
     */
    private static void savePlayerData(OutputStreamWriter f, int[] data) throws IOException {
        /* loop through the data */
        for(int i = 0; i < data.length; i++) {
            f.write(Integer.toString(data[i]));
            /* generating a comma separated list */
            if(i < data.length - 1) f.write(",");
        }
        /* finishing it up with a newline */
        f.write("\n");
    }

    /**
     * Load and parse character stats string from save file.
     * @param f     Handle to save file.
     * @return      An array of integers containing the stats for the character.
     * @throws IOException
     */
    private static int[] loadPlayerData(BufferedReader f) throws IOException {
        int[] stats = new int[6];
        String s = f.readLine();
        int i = 0;
        for(String stat : s.split(","))
            stats[i++] = Integer.parseInt(stat);
        return stats;
    }
}
