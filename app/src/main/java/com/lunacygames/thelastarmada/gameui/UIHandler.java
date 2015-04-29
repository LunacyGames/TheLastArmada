package com.lunacygames.thelastarmada.gameui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
import com.lunacygames.thelastarmada.glengine.SoundEngine;
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

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * User interface handler.
 * Creates, draws and maintains a widget list for a user interface.
 *
 * @author Orlando Arias
 * @author Eric Johansen
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
    public static final int PLAYER_SPRITE = 60;

    private static ActionEvent currentAction;
    private static float glow = 1.0f;

    /**
     * Reset the glow value for a widget.
     */
    public static void resetGlow() {
        glow = 0.0f;
    }

    public static void loadUI(final Context context, final GL10 gl) {
        Log.d("loadUI:", "Loading UI with state: " + GameState.getGameState());
        PlayerList.setPlayer(0);
        /* destroy old UI if we have one */
        if(ui != null) {
            for(UIWidget widget : ui)
                if(widget.getTag() > 3 && widget.getTag() != PLAYER_SPRITE)
                    widget.setTexture(gl, null);
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
            case MENU:
                createMenuUI(context, gl);
                break;
            case VICTORY:
                createVictoryScreen(context, gl);
                break;
            case GAME_OVER:
                createGameOverUI(context, gl);
                break;

            case NONE:
                /* empty UI */
                ui = new ArrayList<UIWidget>();
                break;
        }
    }

    /**
     * Create Menu UI
     * @param context   application context
     * @param gl
     */
    private static void createMenuUI(Context context, GL10 gl){
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();

        float[] size = new float[]{w, h};
        float[] position = new float[]{0, 0.05f*h};
        ui = new ArrayList<UIWidget>();

        /*background is blank*/
        int[] tex = TextureHandler.createTextureFromString(context, gl, " ", true,
                h, w, TextureHandler.TextAlign.ALIGN_LEFT);
        UIWidget widg = new UIWidget("", tex, DEFAULT_TAG, 0, 0, size, null);
        ui.add(widg);

        /* title */
        size = new float[]{w, 0.1f * h};
        tex = TextureHandler.createTextureFromString(context, gl, "Game Menu", false,
                (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
        widg = new UIWidget("menu", tex, DEFAULT_TAG, position[0], position[1],
                size, null);
        ui.add(widg);

        /*create the player list*/
        makePlayerList(context, gl);

        /*back button*/
        size = new float[]{0.2f*w, 0.15f*h};
        position = new float[]{0.8f*w, 0.8f*h};
        tex = TextureHandler.createTextureFromString(context, gl, "Back", false, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widg = new UIWidget("Back", tex, DEFAULT_TAG, position[0], position[1], size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                GameState.setGameState(GameStateList.LOAD_OVERWORLD_UI);
            }
        });
        ui.add(widg);

        /*save button*/
        position = new float[]{0.8f*w, 0.6f*h};
        tex = TextureHandler.createTextureFromString(context, gl, "Save", false, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widg = new UIWidget("Save", tex, DEFAULT_TAG, position[0], position[1], size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                GameState.setGameState(GameStateList.SAVE_GAME);
                SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
            }
        });
        ui.add(widg);
    }

    /**
     * Create Victory Screen
     * @param context   application context
     * @param gl
     */
    private static void createVictoryScreen(Context context, GL10 gl){
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();

        float[] size = new float[]{w, h};
        float[] position = new float[]{0, 0.05f*h};
        ui = new ArrayList<UIWidget>();

        /*background is blank*/
        int[] tex = TextureHandler.createTextureFromString(context, gl, " ", true,
                h, w, TextureHandler.TextAlign.ALIGN_LEFT);
        UIWidget widg = new UIWidget("", tex, DEFAULT_TAG, 0, 0, size, null);
        ui.add(widg);

        /* title */
        size = new float[]{w, 0.1f * h};
        tex = TextureHandler.createTextureFromString(context, gl, "Battle Victory!", false,
                (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
        widg = new UIWidget("Victory Screen", tex, DEFAULT_TAG, position[0], position[1],
                size, null);
        ui.add(widg);

        /*create the player list*/
        makePlayerList(context, gl);

        /*Accept button*/
        size = new float[]{0.2f*w, 0.15f*h};
        position = new float[]{0.8f*w, 0.8f*h};
        tex = TextureHandler.createTextureFromString(context, gl, "Accept", false, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widg = new UIWidget("Accept", tex, DEFAULT_TAG, position[0], position[1], size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                GameState.setGameState(GameStateList.LOAD_OVERWORLD_UI);
            }
        });
        ui.add(widg);
    }

    /**
     * create plater list to use in menu screen and end of battle screen
     * @param context
     * @param gl
     */
    private static void makePlayerList(Context context, GL10 gl){
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();

        int[] texture;
        UIWidget widget;
        float size[];

        /* character sprites*/
        size = new float[]{0.1f * w, 0.1f * w};
        float[] position = {
                0.10f * w, 0.15f * h,
                0.25f * w, 0.15f * h,
                0.40f * w, 0.15f * h,
                0.55f * w, 0.15f * h};
        float xpos;
        int tag = 0;

        for(Player p : PlayerList.getPlayerList()) {
            texture = p.getTexture();

            widget = new UIWidget(p.getName(), texture, PLAYER_SPRITE, position[2*tag], position[2*tag + 1],
                    size, null);
            ui.add(widget);
            tag++;
        }
        size = new float[]{0.1f*w, 0.05f*h};
        /*create label textures*/
        position = new float[]{0.0f*w, 0.42f*h};
        String[] lbls = {"LVL","HP","ATK","DEF","MAG","RES","SPD"};
        for(String s : lbls){
            texture = TextureHandler.createTextureFromString(context, gl, s, false, (int)size[1],
                    (int)size[0], TextureHandler.TextAlign.ALIGN_RIGHT);
            widget = new UIWidget(s, texture, DEFAULT_TAG, position[0], position[1], size, null);
            ui.add(widget);
            position[1]+= 0.07f*h;
        }

        /*Add Character information*/
        position = new float[]{0.1f*w, 0.35f*h};
        for(Player p : PlayerList.getPlayerList()){
            String name = p.getName();
            int[] tex = TextureHandler.createTextureFromString(context, gl, name, false, (int)size[1],
                    (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
            UIWidget widg = new UIWidget(name,tex, DEFAULT_TAG, position[0], position[1], size,null);
            ui.add(widg);
            position[1]+= 0.07f*h;
            int[] stats = p.getMaxStats();
            int lvl = p.getLevel();
            tex = TextureHandler.createTextureFromString(context, gl, Integer.toString(lvl),
                    false, (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
            widg = new UIWidget("",tex, DEFAULT_TAG, position[0], position[1], size,null);
            ui.add(widg);
            position[1]+= 0.07f*h;
            for(int stat : stats){
                tex = TextureHandler.createTextureFromString(context, gl, Integer.toString(stat),
                        false, (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
                widg = new UIWidget("", tex, DEFAULT_TAG, position[0], position[1], size, null);
                ui.add(widg);
                position[1]+=0.07f*h;
            }
            position[0]+= 0.15f*w;
            position[1] = 0.35f*h;
        }


        /*create Item List*/
        Bitmap potion = TextureHandler.loadBitmap(context, "extras/potion.png");
        Bitmap concoction = TextureHandler.loadBitmap(context, "extras/concoction.png");
        Bitmap forcevial = TextureHandler.loadBitmap(context, "extras/forcevial.png");
        Bitmap manavial = TextureHandler.loadBitmap(context, "extras/manavial.png");
        ArrayList<int[]> itemTexs = new ArrayList<int[]>();
        int[] potTex = TextureHandler.createTexture(potion, gl);
        itemTexs.add(potTex);
        int[] concTex = TextureHandler.createTexture(concoction, gl);
        itemTexs.add(concTex);
        int[] forcTex = TextureHandler.createTexture(forcevial, gl);
        itemTexs.add(forcTex);
        int[] manaTex = TextureHandler.createTexture(manavial, gl);
        itemTexs.add(manaTex);

        /*add potion textures to screen*/
        size = new float[]{0.05f*w, 0.05f*w};
        position = new float[]{0.7f*w, 0.1f*h};
        for(int[] tex : itemTexs){
            UIWidget widg = new UIWidget("", tex, DEFAULT_TAG, position[0], position[1], size, null);
            ui.add(widg);
            position[1] += 0.1f*h;
        }

        size = new float[]{0.1f*w, 0.025f*w};
        position = new float[]{0.75f*w, 0.1f*h};
        String[] potNames = new String[]{"Potion", "Concoction", "Force Vial", "Mana Vial"};
        int index = 0;
        for(String name : potNames){
            int[] tex = TextureHandler.createTextureFromString(context, gl, name, false, (int)size[1],
                    (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
            UIWidget widg = new UIWidget("", tex, DEFAULT_TAG, position[0], position[1], size, null);
            ui.add(widg);
            position[1] += 0.05f*h;
            tex = TextureHandler.createTextureFromString(context, gl,
                    "x "+Integer.toString(Inventory.getItemCount(index)), false, (int)size[1],
                    (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
            widg = new UIWidget("", tex, DEFAULT_TAG, position[0], position[1], size, null);
            ui.add(widg);
            position[1] += 0.05f*h;
            index++;
        }

    }

    /**
     *
     * Create Game Over UI
     * @param context
     * @param gl
     */
    private static void createGameOverUI(Context context, GL10 gl){
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();

        UIWidget widget;
        float[] size = new float[]{w,0.4f*w};
        float[] position = new float[]{0.5f*w, 0.5f*h};


        int[] tex = TextureHandler.createTextureFromString(context, gl, "Game Over", false,
                (int)size[1], w, TextureHandler.TextAlign.ALIGN_CENTER);
        widget = new UIWidget("", tex, DEFAULT_TAG, position[0], position[1], size, null);
        ui.add(widget);

        position[1] = 0.7f*h;
        size = new float[]{0.5f*w, 0.1f*w};

        tex = TextureHandler.createTextureFromString(context, gl, "Return to Main Menu", false,
                (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);

        widget = new UIWidget("Accept", tex, DEFAULT_TAG, position[0], position[1], size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                GameState.setGameState(GameStateList.INIT);
            }
        });
        ui.add(widget);
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
                        if(e.getAction() == MotionEvent.ACTION_DOWN) {
                            if (PlayerList.getState() != PlayerState.IDLE) return;
                            Camera.lockPan();
                            PlayerList.setState(PlayerState.WALK_EAST);
                            PlayerList.setPlayerWalking(true);
                        } else if(e.getAction() == MotionEvent.ACTION_CANCEL
                                || e.getAction() == MotionEvent.ACTION_UP) {

                            PlayerList.setPlayerWalking(false);
                        }
                    }
                });
        ui.add(widget);
        /* right button */
        widget = new UIWidget(null, textures.get(2), DEFAULT_TAG, 0.3f * h, 0.7f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_DOWN) {
                            if(PlayerList.getState() != PlayerState.IDLE) return;
                            Camera.lockPan();
                            PlayerList.setState(PlayerState.WALK_WEST);
                            PlayerList.setPlayerWalking(true);
                        } else if(e.getAction() == MotionEvent.ACTION_CANCEL
                                || e.getAction() == MotionEvent.ACTION_UP) {

                            PlayerList.setPlayerWalking(false);
                        }
                    }
                });
        ui.add(widget);
        /* up button */
        widget = new UIWidget(null, textures.get(0), DEFAULT_TAG, 0.15f * h, 0.55f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_DOWN) {
                            if (PlayerList.getState() != PlayerState.IDLE) return;
                            Camera.lockPan();
                            PlayerList.setState(PlayerState.WALK_NORTH);
                            PlayerList.setPlayerWalking(true);
                        } else if(e.getAction() == MotionEvent.ACTION_CANCEL
                                || e.getAction() == MotionEvent.ACTION_UP) {
                            PlayerList.setPlayerWalking(false);
                        }
                    }
                });
        ui.add(widget);
        /* down button */
        widget = new UIWidget(null, textures.get(1), DEFAULT_TAG, 0.15f* h, 0.85f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_DOWN) {
                            if (PlayerList.getState() != PlayerState.IDLE) return;
                            Camera.lockPan();
                            PlayerList.setState(PlayerState.WALK_SOUTH);
                            PlayerList.setPlayerWalking(true);
                        } else if(e.getAction() == MotionEvent.ACTION_CANCEL
                                || e.getAction() == MotionEvent.ACTION_UP) {
                            PlayerList.setPlayerWalking(false);
                        }
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
                        //PlayerList.setState(PlayerState.IDLE);
                        GameState.setGameState(GameStateList.TO_MENU);
                        SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
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
        Bitmap bmp = TextureHandler.loadBitmap(context, MapLoader.getBattleground());
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
                    if(SoundEngine.getInstance().isPlayingEffect()) return;
                    if(e.getAction() != MotionEvent.ACTION_UP) {
                        return;
                    }
                    if(BattleManager.getState() == BattleState.SELECT_TARGET) {
                        currentAction.setTarget(w.getTag());
                        BattleManager.updateState(0, currentAction);
                        SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                        UIHandler.resetGlow();
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
                            if(SoundEngine.getInstance().isPlayingEffect()) return;
                            if(BattleManager.getState() == BattleState.SELECT_TARGET) {
                                currentAction.setTarget(w.getTag());
                                BattleManager.updateState(0, currentAction);
                                UIHandler.resetGlow();
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/accept.ogg");
                            } else {
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/reject.ogg");
                            }
                        }
                    });
            ui.add(widget);
            texture = TextureHandler.createTextureFromString(context, gl, e.getName(),
                    true, (int)(.05f*h), (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);


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
            texture = TextureHandler.createTextureFromString(context, gl,
                    PlayerList.getPlayerList().get(i).getName(),
                    true, (int)(.05f * h), (int)(.30f * w), TextureHandler.TextAlign.ALIGN_LEFT);
            widget = new UIWidget(PlayerList.getPlayerList().get(i).getName(),
                    texture, DEFAULT_TAG, 0, (.8f + 0.05f * i) * h, size, null);
            ui.add(widget);
            /* then the HP */
            texture = TextureHandler.createTextureFromString(context, gl,
                    PlayerList.getPlayerList().get(i).getHp() + "/" +
                            PlayerList.getPlayerList().get(i).getMax_hp(),
                    true, (int)(.05f * h), (int)(.15f * w), TextureHandler.TextAlign.ALIGN_RIGHT);
            size[1] = 0.05f * h;
            size[0] = 0.15f * w;
            widget = new UIWidget("HP", texture, HP_LIST_TAG, 0.3f * w,
                    (0.8f + 0.05f * i) * h, size, null);
            ui.add(widget);
        }

        /* the attack button */
        size[0] = 0.25f * w;
        size[1] = 0.1f * h;
        texture = TextureHandler.createTextureFromString(context, gl, "Attack", true, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Attack", texture, ACTION_MENU_TAG, w - size[0], 0.6f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(SoundEngine.getInstance().isPlayingEffect()) return;
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
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/accept.ogg");
                            } else {
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/reject.ogg");
                            }
                        }
                    }
                });
        ui.add(widget);

        /* the magic button */
        texture = TextureHandler.createTextureFromString(context, gl, "Magic", true, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Magic", texture, ACTION_MENU_TAG, w - size[0], 0.7f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(SoundEngine.getInstance().isPlayingEffect()) return;
                        if(e.getAction() == MotionEvent.ACTION_UP) {
                                        /* if we are not selecting an action, bail out */
                            if(BattleManager.getState() != BattleState.SELECT_ACTION) {
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/reject.ogg");
                                return;
                            }
                                        /* if a player has no magic, return */
                            if(!PlayerList.getPlayerList().get(
                                    PlayerList.getPlayer()).hasMagic()) {
                                BattleManager.setState(BattleState.SELECT_ACTION);
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/reject.ogg");
                                return;
                            }
                            SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
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
        texture = TextureHandler.createTextureFromString(context, gl, "Item", true, (int)size[1],
                (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Item", texture, ACTION_MENU_TAG, w - size[0], 0.8f * h, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(SoundEngine.getInstance().isPlayingEffect()) return;
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
                            SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                        }
                    }
                });
        ui.add(widget);

        /* the optional run button */
        if(!BattleManager.isMandatory()) {
            texture = TextureHandler.createTextureFromString(context, gl, "Run", true, (int) size[1],
                    (int) size[0], TextureHandler.TextAlign.ALIGN_LEFT);
            widget = new UIWidget("Run", texture, ACTION_MENU_TAG, w - size[0], 0.9f * h, size,
                    new UICallback() {
                        @Override
                        public void onMotionEvent(MotionEvent e, UIWidget w) {
                            if(SoundEngine.getInstance().isPlayingEffect()) return;
                            if (e.getAction() == MotionEvent.ACTION_UP) {
                                SoundEngine.getInstance().
                                        playSoundEffect("sounds/effect/accept.ogg");
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
                    texture = TextureHandler.createTextureFromString(context, gl, m.getName(),
                            true, (int) size[1], (int) size[0], TextureHandler.TextAlign.ALIGN_LEFT);

                    widget = new UIWidget(m.getEffect(), texture, SPELL_TAG + i,
                            w - j * size[0], 0.9f * h, size,
                            new UICallback() {
                                @Override
                                public void onMotionEvent(MotionEvent e, UIWidget w) {
                                    Log.d("UIHandler", "clicked " + w.getCaption());
                                    if(SoundEngine.getInstance().isPlayingEffect()) return;
                                    if(e.getAction() != MotionEvent.ACTION_UP) return;
                                    /* failsafe if we are not selecting magic */
                                    if(BattleManager.getState() != BattleState.SELECT_MAGIC) {
                                        SoundEngine.getInstance().
                                                playSoundEffect("sounds/effect/reject.ogg");
                                        return;
                                    }
                                    currentAction.setExtraParameter(w.getCaption());
                                    for(int i = 0; i < PlayerList.getPlayerList().size(); i++)
                                        UIHandler.hideWidgetByTag(SPELL_TAG + i);
                                    SoundEngine.getInstance().
                                            playSoundEffect("sounds/effect/accept.ogg");
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
            texture = TextureHandler.createTextureFromString(context, gl, Inventory.getItemName(i),
                    true, (int)size[1], (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);

            widget = new UIWidget(Integer.toString(i), texture, INVENTORY_TAG,
                    xpos, 0.9f * h, size, new UICallback() {
                @Override
                public void onMotionEvent(MotionEvent e, UIWidget w) {
                    /* Need to check we are depressing the button */
                    if(SoundEngine.getInstance().isPlayingEffect()) return;
                    if(e.getAction() != MotionEvent.ACTION_UP) {
                        return;
                    }
                    /* Failsafe for incorrect action */
                    if(BattleManager.getState() != BattleState.SELECT_ITEM) {
                        SoundEngine.getInstance().playSoundEffect("sounds/effect/reject.ogg");
                        return;
                    }
                    /* Hack: we put the item ID on the caption */
                    int item = Integer.parseInt(w.getCaption());
                    /* Ensure we have the item */
                    if(Inventory.getItemCount(item) == 0) {
                        SoundEngine.getInstance().playSoundEffect("sounds/effect/reject.ogg");
                        TopMessage.showMessage("You have no more "
                                + Inventory.getItemName(item)+ "s!");
                        return;
                    }
                    Inventory.decrementItemCount(item);
                    currentAction.setExtraParameter(w.getCaption() + ",");
                    UIHandler.hideWidgetByTag(INVENTORY_TAG);
                    BattleManager.updateState(0, null);
                    SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
                }
            });
            widget.setVisible(false);
            ui.add(widget);
            xpos -= 0.25f * w;
        }

        /* back button, we only allow to revert current player's actions */
        size = new float[]{0.15f * w, 0.1f * h};
        texture = TextureHandler.createTextureFromString(context, gl, "Back", true, (int)size[1], (int)size[0],
                TextureHandler.TextAlign.ALIGN_LEFT);
        widget = new UIWidget("Back", texture, BACK_BUTTON_TAG, 0.85f* w, 0, size,
                new UICallback() {
                    @Override
                    public void onMotionEvent(MotionEvent e, UIWidget w) {
                        if(SoundEngine.getInstance().isPlayingEffect()) return;
                        if(e.getAction() != MotionEvent.ACTION_UP) return;
                        SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
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

        Bitmap logo = TextureHandler.loadBitmap(context, "titlescreen/logo.png");
        float size[] = {((float)logo.getHeight() * h) / ((float)logo.getWidth()), 0.5f * h};
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
        texture = TextureHandler.createTextureFromString(context, gl, "Start", true, (int)size[1],
                        (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
        /* and add it to the UI */
        Log.d("loadUI: ", "Placing start button at " + x + ", " + y);
        widget = new UIWidget("Start", texture, DEFAULT_TAG, x, y, size, new UICallback() {
            @Override
            public void onMotionEvent(MotionEvent e, UIWidget w) {
                if(e.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("UI: ", "start pressed!");
                    GameState.setGameState(GameStateList.LOAD_MAP);
                    SoundEngine.getInstance().playSoundEffect("sounds/effect/accept.ogg");
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
        for(UIWidget widget : ui) {
            /* if the current widget is a character selecting an action */
            if(widget.getTag() == PlayerList.getPlayer() && !TopMessage.isShown()) {
                /* compute glow parameter */
                float glowfctn = 1.5f - 0.5f*(float)Math.cos(glow);
                /* and set it */
                gl.glColor4f(glowfctn, glowfctn, glowfctn, 1f);
                /* increment glow counter */
                glow += Math.PI/10;
            }

            widget.onDraw(gl);
            /* further, reset the glow if necessary */
            if(widget.getTag() == PlayerList.getPlayer()) {
                gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            }

        }
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
     * @param context   Application context.
     * @param gl        OpenGL context.
     */
    public static void refresh(Context context, GL10 gl) {
        int i = 0;
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        for(UIWidget widget : ui) {
            /* if the widget refers to an HP value on the HP list, refresh its texture */
            if(widget.getTag() == HP_LIST_TAG) {
                int[] texture = TextureHandler.createTextureFromString(context, gl,
                        PlayerList.getPlayerList().get(i).getHp() + "/" +
                                PlayerList.getPlayerList().get(i).getMax_hp(),
                        true, (int)(.05f * h), (int)(.15f * w), TextureHandler.TextAlign.ALIGN_RIGHT);
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
