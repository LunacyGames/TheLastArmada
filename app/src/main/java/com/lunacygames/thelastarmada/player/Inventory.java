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
     * Return Item effect
     *
     * @param itemID    ID of item
     * @return          Item effect if itemID valid, null if not
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
     *
     */
    public static void initInventory() {
        inventory = new Integer[MAX_ITEM_NUMBER];
    }

    /**
     * Set item count of given itemID
     * @param itemID        id of item
     * @param itemCount     quantity of item
     */
    public static void setItemCount(int itemID, int itemCount) {
        inventory[itemID] = itemCount;
    }

    /**
     * Increment the given itemID's quantity
     * @param itemID        id of item
     * @param itemCount     quantity of item
     */
    public static void incrementItemCount(int itemID, int itemCount) {
        inventory[itemID] += itemCount;
    }

    /**
     * @deprecated by decrementItemCount
     * @param itemID        id of item
     */
    public static void useItem(int itemID) {
        if(inventory[itemID] == 0) return;
        else inventory[itemID]--;
    }

    /**
     * Decrement number of itemIDs in inventory
     * @param itemID        id of item
     */
    public static void decrementItemCount(int itemID) {
        if(inventory == null) return;
        else if(inventory[itemID] > 0) inventory[itemID]--;
    }

    /**
     * Get number of item specified by itemID
     * @param itemID        id of item
     * @return              quantity of itemID in inventory
     */
    public static int getItemCount(int itemID) {
        return inventory[itemID];
    }
}
