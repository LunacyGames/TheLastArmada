package com.lunacygames.thelastarmada.player;

/**
 * Inventory manipulation
 *
 * @author Orlando Arias
 */
public class Inventory {
    private static Integer[] inventory;
    public static final int MAX_ITEM_NUMBER = 4;

    /**
     * Get the name of an item.
     *
     * @param itemID    Identification number of the item.
     * @return          String object containing the name of th item.
     */
    public static String getItemName(int itemID) {
        switch(itemID) {
            case 0:
                return "Potion";
            case 1:
                return "Concoction";
            case 2:
                return "Force Vial";
            case 3:
                return "Mana Vial";
        }
        return null;
    }

    /**
     *
     * @param itemID
     * @return
     */
    public static String getItemEffect(int itemID) {
        switch(itemID) {
            case 0:
                return "HPI20";
            case 1:
                return "HPI50";
            case 2:
                return "STR10";
            case 3:
                return "MAG10";
        }
        return null;
    }

    /**
     * Initializes the player's inventory.
     */
    public static void initInventory() {
        inventory = new Integer[MAX_ITEM_NUMBER];
    }

    public static void setItemCount(int itemID, int itemCount) {
        inventory[itemID] = itemCount;
    }

    public static void incrementItemCount(int itemID, int itemCount) {
        inventory[itemID] += itemCount;
    }

    public static void useItem(int itemID) {
        if(inventory[itemID] == 0) return;
        else inventory[itemID]--;
    }

    public static void decrementItemCount(int itemID) {
        if(inventory == null) return;
        else if(inventory[itemID] > 0) inventory[itemID]--;
    }

    public static int getItemCount(int itemID) {
        return inventory[itemID];
    }
}
