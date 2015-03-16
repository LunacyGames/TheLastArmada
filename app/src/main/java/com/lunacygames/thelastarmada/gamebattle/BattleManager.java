package com.lunacygames.thelastarmada.gamebattle;

import android.util.Log;

import com.lunacygames.thelastarmada.gameui.TopMessage;
import com.lunacygames.thelastarmada.gameutils.Interpreter;
import com.lunacygames.thelastarmada.player.PlayerList;
import com.lunacygames.thelastarmada.gameui.UIHandler;

import java.util.Random;

/**
 * Created by zeus on 3/11/15.
 */
public class BattleManager {
    private static BattleState state;
    private static boolean mandatory;

    public static void reset() {
        state = BattleState.START;
    }

    public static void setMandatory(boolean m) {
        mandatory = m;
    }

    public static void updateState(int key, ActionEvent e) {
        switch(state) {
            case START:
                Log.d("BattleManager: ", "Switching to SELECT_ACTION");
                /* ensure the menu is visible */
                UIHandler.showWidgetByTag(10);
                state = BattleState.SELECT_ACTION;
                break;
            case SELECT_ACTION:
                if(key == 1) {
                    Log.d("BattleManager: ", "Switching to SELECT_TARGET");
                    state = BattleState.SELECT_TARGET;
                } else if(key == 2) {
                    Log.d("BattleManager: ", "Switching to SELECT_MAGIC");
                    state = BattleState.SELECT_MAGIC;
                } else if(key == 3) {
                    Log.d("BattleManager: ", "Switching to SELECT_ITEM");
                    state = BattleState.SELECT_ITEM;
                }
                break;
            case SELECT_MAGIC:
                state = BattleState.SELECT_TARGET;
                break;
            case SELECT_ITEM:
                break;
            case SELECT_TARGET:
                Log.d("BattleManager: ", "Switching to ADD_PLAYER_ACTION");
                state = BattleState.ADD_PLAYER_ACTION;
            case ADD_PLAYER_ACTION:
                ActionEvent.enqueue(e);
                int player = PlayerList.getPlayer();
                if(player == 3) {
                    Log.d("BattleManager: ", "Adding AI actions");
                    PlayerList.setPlayer(0);
                    ActionEvent event;
                    /* go through the enemy list */
                    int i = 4;
                    Random r = new Random();
                    for(Enemy enemy : Enemy.getEnemyList()) {
                        event = new ActionEvent(enemy.getEnemyAction());
                        event.setPlayerSpeed(enemy.getSpd());
                        event.setPlayer(i++);
                        event.setTarget(r.nextInt(4));
                        ActionEvent.enqueue(event);
                    }
                    Log.d("BattleManager: ", "Processing action queue");
                    state = BattleState.PROCESS_ACTION_QUEUE;
                    UIHandler.resetGlow();
                } else {
                    Log.d("BattleManager: ", "Players remain, switching to SELECT_ACTION");
                    PlayerList.setPlayer(player + 1);
                    state = BattleState.SELECT_ACTION;
                    /* ensure the menu is visible */
                    UIHandler.showWidgetByTag(UIHandler.ACTION_MENU_TAG);
                }
                break;
            case PROCESS_ACTION_QUEUE:
                state = BattleState.SELECT_ACTION;
                /* ensure the menu is visible */
                UIHandler.showWidgetByTag(UIHandler.ACTION_MENU_TAG);
                break;

        }
    }

    public static void setState(BattleState s) {
        state = s;
    }

    public static BattleState getState() {
        return state;
    }

    public static boolean isMandatory() {
        return mandatory;
    }
}
