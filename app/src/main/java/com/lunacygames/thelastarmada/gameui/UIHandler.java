package com.lunacygames.thelastarmada.gameui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
import com.lunacygames.thelastarmada.player.Inventory;
import com.lunacygames.thelastarmada.player.Magic;
import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.player.Player;
import com.lunacygames.thelastarmada.player.PlayerList;
import com.lunacygames.thelastarmada.player.PlayerState;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;
import com.lunacygames.thelastarmada.gamebattle.ActionEvent;
import com.lunacygames.thelastarmada.gamebattle.BattleManager;
import com.lunacygames.thelastarmada.gamebattle.BattleState;
import com.lunacygames.thelastarmada.gamebattle.Enemy;
import com.lunacygames.thelastarmada.gamemap.MapLoader;
import com.lunacygames.thelastarmada.gamemap.MapType;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * User interface handler.
 * Creates, draws and maintains a widget list for a user interface.
 *
 * @author Orlando Arias
 */
public class UIHandler {
    private static ArrayList<UIWidget> ui;
    private static UIList active;

    public static final int DEFAULT_TAG = 1000;
    public static final int SPELL_TAG = 30;
    public static final int INVENTORY_TAG = 40;
    public static final int BACK_BUTTON_TAG = 50;
    public static final int ACTION_MENU_TAG = 10;
    public static final int HP_LIST_TAG = 20;

    private static ActionEvent currentAction;

    public static void loadUI(final Context context, final GL10 gl) {
        Log.d("loadUI:", "Loading UI with state: " + GameState.getGameState());
        PlayerList.setPlayer(0);
        /* destroy old UI if we have one */
        if(ui != null) {
            for(UIWidget widget : ui)
                if(widget.getTag() > 3) widget.setTexture(gl, null);
        }
        switch(active) {
            case START:
                uiCreateStart(context, gl);
                break;
            case OVERWORLD:
                createOverworldUI(context, gl);
                break;
            case BATTLE:
                createBattleUI(context, gl);
                break;

            case NONE:
                /* empty UI */
                ui = new ArrayList<UIWidget>();
                break;
        }
    }

    /**
     * Create overworld UI
     * @param context   Application context.
     * @param gl        OpenGL context.
     */
    private static void createOverworldUI(Context context, GL10 gl) {
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();

        /* prepare prerequisites for directional pad */
        ui = new ArrayList<UIWidget>();
        Bitmap buttons = TextureHandler.loadBitmap(context, "buttons/dpad.png");
        ArrayList<Bitmap> button = TextureHandler.loadTiles(buttons, 2, 2);
        ArrayList<int[]> textures = new ArrayList<int[]>();
        for(Bitmap bmp : button) {
            textures.add(TextureHandler.createTexture(bmp, gl));
        }
        /* left button */
        float[] size = {0.15f * h, 0.15f * h};
        UIWidget widget = new UIWidget(null, textures.get(3), DEFAULT_TAG, 0, 0.7f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(PlayerList.getState() != PlayerState.IDLE) return;
                        Log.d("UI: ", "Left");
                        Camera.lockPan();
                        PlayerList.setState(PlayerState.WALK_EAST);
                    }
                });
        ui.add(widget);
        /* right button */
        widget = new UIWidget(null, textures.get(2), DEFAULT_TAG, 0.3f * h, 0.7f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(PlayerList.getState() != PlayerState.IDLE) return;
                        Log.d("UI: ", "Right");
                        Camera.lockPan();
                        PlayerList.setState(PlayerState.WALK_WEST);
                    }
                });
        ui.add(widget);
        /* up button */
        widget = new UIWidget(null, textures.get(0), DEFAULT_TAG, 0.15f * h, 0.55f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(PlayerList.getState() != PlayerState.IDLE) return;
                        Log.d("UI: ", "Up");
                        Camera.lockPan();
                        PlayerList.setState(PlayerState.WALK_NORTH);
                    }
                });
        ui.add(widget);
        /* down button */
        widget = new UIWidget(null, textures.get(1), DEFAULT_TAG, 0.15f* h, 0.85f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(PlayerList.getState() != PlayerState.IDLE) return;
                        Log.d("UI: ", "Down");
                        Camera.lockPan();
                        PlayerList.setState(PlayerState.WALK_SOUTH);
                    }
                });
        ui.add(widget);
        buttons = TextureHandler.loadBitmap(context, "buttons/menu.png");
        int[] texture = TextureHandler.createTexture(buttons, gl);

        /* lovely java... need to create a new float array because otherwise the size
         * for all other widgets gets changed.
         * The best part is that this indirection happens because we actually have an
         * pass a pointer to this array to our widget, not a copy of the array, but Java
         * does not tell us that...
         */
        size = new float[]{0.45f * h, 0.15f * h};
        widget = new UIWidget(null, texture, DEFAULT_TAG, 0, 0, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        PlayerList.setState(PlayerState.IDLE);
                        Log.d("UI: ", "Menu pressed");
                        GameState.setGameState(GameStateList.TO_BATTLE);
                    }
                });
        ui.add(widget);
    }

    /**
     * Create battle screen UI.
     * @param context   Application context.
     * @param gl        OpenGL context.
     */
    private static void createBattleUI(Context context, GL10 gl) {
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        /* prepare the battle system UI */
        ui = new ArrayList<UIWidget>();
        int[] texture;
        UIWidget widget;
        float size[] = new float[2];
        /* background image */
        size[0] = w;
        size[1] = 1440.0f * w / 1920.0f;

        /* the background image depends on the active map */
        Bitmap bmp = null;
        switch(MapLoader.getActiveMap()) {
            case VILLAGE:
                break;
            case DUNGEON:
                break;
            case OVERWORLD:
                bmp = TextureHandler.loadBitmap(context, "battlescreen/overworld.png");
                break;
            default:
                bmp = TextureHandler.loadBitmap(context, "titlescreen/titlescreen.png");
                break;
        }
        texture = TextureHandler.createTexture(bmp, gl);
        widget = new UIWidget(null, texture, DEFAULT_TAG, 0, 0, size, null);
        ui.add(widget);


        /* characters */
        size = new float[]{0.1f * w, 0.1f * w};
        float[] position = {
                0.50f * w, 0.60f * h,
                0.55f * w, 0.75f * h,
                0.65f * w, 0.65f * h,
                0.70f * w, 0.50f * h};
        float xpos;
        int tag = 0;
        for(Player p : PlayerList.getPlayerList()) {
            texture = p.getTexture();

            widget = new UIWidget(p.getName(), texture, tag, position[2*tag], position[2*tag + 1],
                    size, new UICallback() {
                @Override
                public void onMotionEvent(MotionEvent e, UIWidget w) {
                    if(e.getAction() != MotionEvent.ACTION_UP) return;
                    if(BattleManager.getState() == BattleState.SELECT_TARGET) {
                        currentAction.setTarget(w.getTag());
                        BattleManager.updateState(0, currentAction);
                    }
                }
            });
            ui.add(widget);
            tag++;
        }

        /* enemies */
        xpos = 0;
        for(Enemy e : Enemy.getEnemyList()) {
            texture = e.getTexture();
            /* enemy is scaled with respect to player as specified in enemy description file */
            size = new float[2];
            size[0] = 0.1f * w * e.getScale()[0];
            size[1] = 0.1f * w * e.getScale()[1];
            widget = new UIWidget(e.getName(), texture, tag, xpos, .7f * h - size[1], size,
                    new UICallback() {
                        @Override
                        public void onMotionEvent(MotionEvent e, UIWidget w) {
                            if(BattleManager.getState() == BattleState.SELECT_TARGET) {
                                currentAction.setTarget(w.getTag());
                                BattleManager.updateState(0, currentAction);
                            }
                        }
                    });
            ui.add(widget);
            texture = TextureHandler.createTextureFromString(gl, e.getName(),
                    (int)(.05f*h), (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);


            size = new float[2];
            size[0] = 0.1f * w * e.getScale()[0];
            size[1] = 0.05f * h;
            widget = new UIWidget(e.getName() + "_lbl", texture, tag,
                    xpos, .75f * h - size[1], size , null);
            ui.add(widget);
            xpos += size[0] + 10;
            tag++;
        }


        /* make the character list */
        for(int i = 0; i < 4; i++) {
            /* first the name */
            size[1] = 0.05f * h;
            size[0] = 0.30f * w;
            texture = TextureHandler.createTextureFromString(gl,
                    PlayerList.getPlayerList().get(i).getName(),
                    (int)(.05f * h), (int)(.30f * w), TextureHandler.TextAlign.ALIGN_LEFT);
            widget = new UIWidget(PlayerList.getPlayerList().get(i).getName(),
                    texture, DEFAULT_TAG, 0, (.8f + 0.05f * i) * h, size, null);
            ui.add(widget);
            /* then the HP */
            texture = TextureHandler.createTextureFromString(gl,
                    PlayerList.getPlayerList().get(i).getHp() + "/" +
                            PlayerList.getPlayerList().get(i).getMax_hp(),
                    (int)(.05f * h), (int)(.15f * w), TextureHandler.TextAlign.ALIGN_RIGHT);
            size[1] = 0.05f * h;
            size[0] = 0.15f * w;
            widget = new UIWidget("HP", texture, HP_LIST_TAG, 0.3f * w,
                    (0.8f + 0.05f * i) * h, size, null);
            ui.add(widget);
        }

        /* the attack button */
        size[0] = 0.25f * w;
        size[1] = 0.1f * h;
        texture = TextureHandler.createTextureFromString(gl, "Attack", (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Attack", texture, ACTION_MENU_TAG, w - size[0], 0.6f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_UP) {
                            if(BattleManager.getState() == BattleState.SELECT_ACTION) {
                                currentAction = new ActionEvent("ATK");
                                currentAction.setPlayer(PlayerList.getPlayer());
                                int speed =
                                        PlayerList.getPlayerList()
                                                .get(PlayerList.getPlayer()).getSpd();
                                currentAction.setPlayerSpeed(speed);
                                BattleManager.updateState(1, null);
                                UIHandler.hideWidgetByTag(ACTION_MENU_TAG);
                                UIHandler.showWidgetByTag(BACK_BUTTON_TAG);
                            }
                        }
                    }
                });
        ui.add(widget);

        /* the magic button */
        texture = TextureHandler.createTextureFromString(gl, "Magic", (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Magic", texture, ACTION_MENU_TAG, w - size[0], 0.7f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_UP) {
                                        /* if we are not selecting an action, bail out */
                            if(BattleManager.getState() != BattleState.SELECT_ACTION)
                                return;
                                        /* if a player has no magic, return */
                            if(!PlayerList.getPlayerList().get(
                                    PlayerList.getPlayer()).hasMagic()) {
                                BattleManager.setState(BattleState.SELECT_ACTION);
                                return;
                            }
                            currentAction = new ActionEvent("MAG");
                            currentAction.setPlayer(PlayerList.getPlayer());
                            int speed =
                                    PlayerList.getPlayerList()
                                            .get(PlayerList.getPlayer()).getSpd();
                            currentAction.setPlayerSpeed(speed);

                            BattleManager.updateState(2, null);
                            UIHandler.showWidgetByTag(SPELL_TAG + PlayerList.getPlayer());
                            UIHandler.hideWidgetByTag(ACTION_MENU_TAG);
                            UIHandler.showWidgetByTag(BACK_BUTTON_TAG);
                        }
                    }
                });
        ui.add(widget);

        /* the item button */
        texture = TextureHandler.createTextureFromString(gl, "Item", (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Item", texture, ACTION_MENU_TAG, w - size[0], 0.8f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_UP) {
                            currentAction = new ActionEvent("ITM");
                            currentAction.setPlayer(PlayerList.getPlayer());
                            int speed =
                                    PlayerList.getPlayerList()
                                            .get(PlayerList.getPlayer()).getSpd();
                            currentAction.setPlayerSpeed(speed);

                            BattleManager.updateState(3, null);
                            UIHandler.hideWidgetByTag(ACTION_MENU_TAG);
                            UIHandler.showWidgetByTag(INVENTORY_TAG);
                            UIHandler.showWidgetByTag(BACK_BUTTON_TAG);
                        }
                    }
                });
        ui.add(widget);

        /* the optional run button */
        if(!BattleManager.isMandatory()) {
            texture = TextureHandler.createTextureFromString(gl, "Run", (int) size[1],
                    (int) size[0], TextureHandler.TextAlign.ALIGN_LEFT);
            widget = new UIWidget("Run", texture, ACTION_MENU_TAG, w - size[0], 0.9f * h, size,
                    new UICallback() {
                        @Override
                        public void onMotionEvent(MotionEvent e, UIWidget w) {
                            if (e.getAction() == MotionEvent.ACTION_UP) {
                                GameState.setGameState(GameStateList.LOAD_OVERWORLD_UI);
                                UIHandler.hideWidgetByTag(ACTION_MENU_TAG);
                            }
                        }
                    });
            ui.add(widget);
        }

        /* magic menu */
        size = new float[]{0.2f * w, 0.1f * h};
        int i = 0;
        for(Player p : PlayerList.getPlayerList()) {
            int j = 1;
            /* check if player has any spells */
            if(p.hasMagic()) {
                /* if it does, create its menu */
                for(Magic m : p.getMagicList()) {
                    /* we generate menu entries depending on the spell list */
                    texture = TextureHandler.createTextureFromString(gl, m.getName(),
                            (int) size[1], (int) size[0], TextureHandler.TextAlign.ALIGN_LEFT);

                    widget = new UIWidget(m.getEffect(), texture, SPELL_TAG + i,
                            w - j * size[0], 0.9f * h, size,
                            new UICallback() {
                                @Override
                                public void onMotionEvent(MotionEvent e, UIWidget w) {
                                    Log.d("UIHandler", "clicked " + w.getCaption());
                                    if(e.getAction() != MotionEvent.ACTION_UP) return;
                                    /* failsafe if we are not selecting magic */
                                    if(BattleManager.getState() != BattleState.SELECT_MAGIC) return;
                                    currentAction.setExtraParameter(w.getCaption());
                                    for(int i = 0; i < PlayerList.getPlayerList().size(); i++)
                                        UIHandler.hideWidgetByTag(SPELL_TAG + i);

                                    BattleManager.updateState(0, null);
                                }
                    });
                    widget.setVisible(false);
                    ui.add(widget);
                    j++;
                }
            }
            i++;
        }

        /* items menu */
        size = new float[]{0.25f * w, 0.1f * h};
        xpos = 0.75f * w;
        for(i = Inventory.MAX_ITEM_NUMBER - 1; i >= 0; i--) {
            texture = TextureHandler.createTextureFromString(gl, Inventory.getItemName(i),
                    (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);

            widget = new UIWidget(Integer.toString(i), texture, INVENTORY_TAG,
                    xpos, 0.9f * h, size, new UICallback() {
                @Override
                public void onMotionEvent(MotionEvent e, UIWidget w) {
                    /* Need to check we are depressing the button */
                    if(e.getAction() != MotionEvent.ACTION_UP) return;
                    /* Failsafe for incorrect action */
                    if(BattleManager.getState() != BattleState.SELECT_ITEM) return;
                    /* Hack: we put the item ID on the caption */
                    int item = Integer.parseInt(w.getCaption());
                    /* Ensure we have the item */
                    if(Inventory.getItemCount(item) == 0) {
                        TopMessage.showMessage("You have no more "
                                + Inventory.getItemName(item)+ "s!");
                        return;
                    }
                    Inventory.decrementItemCount(item);
                    currentAction.setExtraParameter(w.getCaption() + ",");
                    UIHandler.hideWidgetByTag(INVENTORY_TAG);
                    BattleManager.updateState(0, null);
                }
            });
            widget.setVisible(false);
            ui.add(widget);
            xpos -= 0.25f * w;
        }

        /* back button, we only allow to revert current player's actions */
        size = new float[]{0.15f * w, 0.1f * h};
        texture = TextureHandler.createTextureFromString(gl, "Back", (int)size[1], (int)size[0],
                TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Back", texture, BACK_BUTTON_TAG, 0.85f* w, 0, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() != MotionEvent.ACTION_UP) return;
                        BattleState s = BattleManager.revertAction();
                        switch(s) {
                            case SELECT_MAGIC:
                                UIHandler.showWidgetByTag(SPELL_TAG + PlayerList.getPlayer());
                                break;
                            case SELECT_ITEM:
                                UIHandler.showWidgetByTag(INVENTORY_TAG);
                                break;
                            case SELECT_ACTION:
                                UIHandler.showWidgetByTag(ACTION_MENU_TAG);
                                UIHandler.hideWidgetByTag(INVENTORY_TAG);
                                UIHandler.hideWidgetByTag(SPELL_TAG + PlayerList.getPlayer());
                                UIHandler.hideWidgetByTag(BACK_BUTTON_TAG);
                                break;
                        }
                    }
                });
        widget.setVisible(false);
        ui.add(widget);
    }

    private static void uiCreateStart(Context context, GL10 gl) {
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        /* start creating start screen UI */
        ui = new ArrayList<UIWidget>();
        /* prepare the logo */
        float size[] = {10.0f * h / 15.0f, 0.5f * h};
        Bitmap logo = TextureHandler.loadBitmap(context, "titlescreen/logo.png");
        int[] texture = TextureHandler.createTexture(logo, gl);
        float y = (0.8f*h - size[1])/2;
        float x = (w - size[0])/2;
        Log.d("loadUI: ", "Placing logo at " + x + ", " + y);
        /* create logo widget */
        UIWidget widget = new UIWidget(null, texture, DEFAULT_TAG, x, y, size, null);
        /* and add it to the UI */
        ui.add(widget);

        /* prepare start button */
        size[0] = 0.25f * w;
        size[1] = 0.1f * h;
        x = (w - size[0])/2;
        y = 0.8f * h;
        texture = TextureHandler.createTextureFromString(gl, "Start", (int)size[1],
                        (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
        /* and add it to the UI */
        Log.d("loadUI: ", "Placing start button at " + x + ", " + y);
        widget = new UIWidget("Start", texture, DEFAULT_TAG, x, y, size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                if(e.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("UI: ", "start pressed!");
                    GameState.setGameState(GameStateList.LOAD_MAP);
                    MapLoader.setActiveMap(MapType.OVERWORLD);
                }
            }
        });
        ui.add(widget);
    }

    /**
     * Draw the active UI
     * @param gl    OpenGL context.
     */
    public static void drawUI(GL10 gl) {
        if(ui == null) return;
        int width = PlatformData.getScreenWidth();
        int height = PlatformData.getScreenHeight();
        gl.glOrthof(0.0f, width, height, 0.0f, -1.0f, 0.0f);
        for(UIWidget widget : ui)
            widget.onDraw(gl);
    }

    /**
     * Distribute event signal across widgets.
     * @param e     Event type.
     * @param x     X coordinate of press.
     * @param y     Y coordinate of press.
     */
    public static void onMotionEvent(MotionEvent e, float x, float y) {
        if(ui == null) return;
        for(UIWidget widget : ui) {
            /* don't process events for invisible widgets */
            if(!widget.isVisible()) continue;
            /* otherwise, get the bounds of the widget */
            float[] bound = widget.getSize();
            float widgetX = widget.getX();
            float widgetY = widget.getY();
            /* and see if we hit it */
            if((widgetX <= x) && (widgetY <= y)
                    && (bound[0] + widgetX>= x) && (bound[1] + widgetY>= y)) {
                widget.onMotionEvent(e);
            }
        }
    }

    /**
     * Hides all widgets with specified tag value.
     * @param tag   Tag value of widgets to hide.
     */
    public static void hideWidgetByTag(int tag) {
        setWidgetVisibility(tag, false);
    }

    /**
     * Shows all widgets with specified tag value.
     * @param tag   Tag value of widgets to show.
     */
    public static void showWidgetByTag(int tag) {
        setWidgetVisibility(tag, true);
    }

    /**
     * Sets visibility attribute of all widgets with specified tag value.
     * @param tag       Tag value of widgets to set visibility attribute.
     * @param visible   Whether the specified widgets should be visible.
     */
    private static void setWidgetVisibility(int tag, boolean visible) {
        for(UIWidget w : ui) {
            if(w.getTag() == tag) {
                w.setVisible(visible);
            }
        }

    }

    /**
     * Refresh user interface.
     * @param gl    OpenGL context.
     */
    public static void refresh(GL10 gl) {
        int i = 0;
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        for(UIWidget widget : ui) {
            /* if the widget refers to an HP value on the HP list, refresh its texture */
            if(widget.getTag() == HP_LIST_TAG) {
                int[] texture = TextureHandler.createTextureFromString(gl,
                        PlayerList.getPlayerList().get(i).getHp() + "/" +
                                PlayerList.getPlayerList().get(i).getMax_hp(),
                        (int)(.05f * h), (int)(.15f * w), TextureHandler.TextAlign.ALIGN_RIGHT);
                widget.setTexture(gl, texture);
                i++;
            /* otherwise, if it is a monster */
            } else if(widget.getTag() > 3 && widget.getTag() < (Enemy.getEnemyList().size() + 4)) {
                /* if it died, remove him from the grid */
                if(Enemy.getEnemyList().get(widget.getTag() - 4).getHp() == 0) {
                    widget.setVisible(false);
                }
            }
        }

    }

    /**
     * Gets the active user interface.
     * @return          Active user interface.
     */
    public static UIList getActive() {
        return active;
    }

    /**
     * Sets the active user interface.
     * @param active    Active user interface.
     */
    public static void setActive(UIList active) {
        UIHandler.active = active;
    }
}
