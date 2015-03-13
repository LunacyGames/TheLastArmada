package com.lunacygames.thelastarmada.gameui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.glengine.Camera;
import com.lunacygames.thelastarmada.gameutils.GameState;
import com.lunacygames.thelastarmada.gameutils.GameStateList;
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
 * Created by zeus on 2/20/15.
 */
public class UIHandler {
    private static ArrayList<UIWidget> ui;
    private static UIList active;

    private static final int DEFAULT_TAG = 1000;

    private static ActionEvent currentAction;

    public static void loadUI(final Context context, final GL10 gl) {
        Log.d("loadUI:", "Loading UI with state: " + GameState.getGameState());
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        PlayerList.setPlayer(0);
        /* destroy old UI if we have one */
        if(ui != null) {
            for(UIWidget widget : ui)
                if(widget.getTag() > 3) widget.setTexture(gl, null);
        }
        switch(active) {
            case START: {
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
                texture =
                        TextureHandler.createTextureFromString(gl, "Start", (int)size[1],
                                (int)size[0], TextureHandler.TextAlign.ALIGN_CENTER);
                /* and add it to the UI */
                Log.d("loadUI: ", "Placing start button at " + x + ", " + y);
                widget = new UIWidget("Start", texture, DEFAULT_TAG, x, y, size, new UICallback() {
                    @Override
                    public void onClick(MotionEvent e, UIWidget w) {
                        if(e.getAction() == MotionEvent.ACTION_UP) {
                            Log.d("UI: ", "start pressed!");
                            GameState.setGameState(GameStateList.LOAD_MAP);
                            MapLoader.setActiveMap(MapType.OVERWORLD);
                        }
                    }
                });
                ui.add(widget);
            }
            break;
            case OVERWORLD: {
                    /* overworld UI */
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
                                public void onClick(MotionEvent e, UIWidget w) {
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
                                public void onClick(MotionEvent e, UIWidget w) {
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
                                public void onClick(MotionEvent e, UIWidget w) {
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
                                public void onClick(MotionEvent e, UIWidget w) {
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
                                public void onClick(MotionEvent e, UIWidget w) {
                                    PlayerList.setState(PlayerState.IDLE);
                                    Log.d("UI: ", "Menu pressed");
                                    GameState.setGameState(GameStateList.TO_BATTLE);
                                }
                            });
                    ui.add(widget);

                }
                break;
            case BATTLE: {
                    /* prepare the battle system UI */
                    ui = new ArrayList<UIWidget>();
                    int[] texture;
                    UIWidget widget;
                    float size[] = new float[2];
                    /* background image */
                    size[0] = w;
                    size[1] = 1440.0f * (float)h/(float)w;

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
                    /* TODO: scale */
                    size = new float[]{64.0f, 64.0f};
                    float xpos;
                    int tag = 0;
                    xpos = w / 2;
                    for(Player p : PlayerList.getPlayerList()) {
                        texture = p.getTexture();
                            /* TODO: add callback */
                        widget = new UIWidget(p.getName(), texture, tag, xpos, 0.7f * h - size[1],
                                size, new UICallback() {
                            @Override
                            public void onClick(MotionEvent e, UIWidget w) {
                                Log.d("UIHandler: ", "clicked player " + w.getTag());
                                if(e.getAction() != MotionEvent.ACTION_UP) return;
                                if(BattleManager.getState() == BattleState.SELECT_TARGET) {
                                    currentAction.setTarget(w.getTag());
                                    BattleManager.updateState(0, currentAction);
                                }
                            }
                        });
                        ui.add(widget);
                        xpos += size[0] + 10;
                        tag++;
                    }

                    /* enemies */
                    /*TODO: scale sprite? */

                    xpos = 0;
                    for(Enemy e : Enemy.getEnemyList()) {
                        texture = e.getTexture();
                        size = new float[2];
                        size[0] = e.getSize()[0];
                        size[1] = e.getSize()[1];
                        /* TODO: add callback */
                        widget = new UIWidget(e.getName(), texture, tag, xpos, .7f * h - size[1], size,
                                new UICallback() {
                                    @Override
                                    public void onClick(MotionEvent e, UIWidget w) {
                                        Log.d("UIHandler: ", "Enemy clicked: " + w.getTag());
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
                        size[0] = e.getSize()[0];
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
                        widget = new UIWidget("HP", texture, 20, 0.3f * w,
                                (0.8f + 0.05f * i) * h, size, null);
                        ui.add(widget);
                    }

                    /* the attack button */
                    size[0] = 0.25f * w;
                    size[1] = 0.1f * h;
                    texture = TextureHandler.createTextureFromString(gl, "Attack", (int)size[1],
                            (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
                    widget = new UIWidget("Attack", texture, 10, w - size[0], 0.6f * h, size,
                            new UICallback() {
                                @Override
                                public void onClick(MotionEvent e, UIWidget w) {
                                    if(e.getAction() == MotionEvent.ACTION_UP) {
                                        if(BattleManager.getState() == BattleState.SELECT_ACTION) {
                                            currentAction = new ActionEvent("ATK");
                                            currentAction.setPlayer(PlayerList.getPlayer());
                                            int speed =
                                                    PlayerList.getPlayerList()
                                                            .get(PlayerList.getPlayer()).getSpd();
                                            currentAction.setPlayerSpeed(speed);
                                            BattleManager.updateState(1, null);
                                            UIHandler.hideWidgetByTag(10);
                                        }
                                    }
                                }
                            });
                    ui.add(widget);

                    /* the magic button */
                    texture = TextureHandler.createTextureFromString(gl, "Magic", (int)size[1],
                            (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
                    widget = new UIWidget("Magic", texture, 10, w - size[0], 0.7f * h, size,
                            new UICallback() {
                                @Override
                                public void onClick(MotionEvent e, UIWidget w) {
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
                                        UIHandler.showWidgetByTag(30 + PlayerList.getPlayer());
                                        UIHandler.hideWidgetByTag(10);
                                    }
                                }
                            });
                    ui.add(widget);

                    /* the item button */
                    texture = TextureHandler.createTextureFromString(gl, "Item", (int)size[1],
                            (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
                    widget = new UIWidget("Item", texture, 10, w - size[0], 0.8f * h, size,
                            new UICallback() {
                                @Override
                                public void onClick(MotionEvent e, UIWidget w) {
                                    if(e.getAction() == MotionEvent.ACTION_UP) {
                                        currentAction = new ActionEvent("ITM");
                                        currentAction.setPlayer(PlayerList.getPlayer());
                                        int speed =
                                                PlayerList.getPlayerList()
                                                        .get(PlayerList.getPlayer()).getSpd();
                                        currentAction.setPlayerSpeed(speed);

                                        BattleManager.updateState(3, null);
                                        UIHandler.hideWidgetByTag(10);
                                        /* TODO: item related stuff here */
                                    }
                                }
                            });
                    ui.add(widget);

                    /* the optional run button */
                    if(BattleManager.isMandatory()) break;
                    texture = TextureHandler.createTextureFromString(gl, "Run", (int)size[1],
                            (int)size[0], TextureHandler.TextAlign.ALIGN_LEFT);
                    widget = new UIWidget("Run", texture, 10, w - size[0], 0.9f * h, size,
                            new UICallback() {
                                @Override
                                public void onClick(MotionEvent e, UIWidget w) {
                                    if(e.getAction() == MotionEvent.ACTION_UP) {
                                        GameState.setGameState(GameStateList.LOAD_OVERWORLD_UI);
                                        UIHandler.hideWidgetByTag(10);
                                    }
                                }
                            });
                    ui.add(widget);

                    /* magic menu */
                    size = new float[]{0.2f * w, 0.1f * h};
                    for(int i = 2; i < 4; i++) {
                        int j = 1;
                        for(Magic m : PlayerList.getPlayerList().get(i).getMagicList()) {
                            /* we generate menu entries depending on the spell list */
                            texture = TextureHandler.createTextureFromString(gl, m.getName(),
                                    (int) size[1], (int) size[0], TextureHandler.TextAlign.ALIGN_LEFT);
                            widget = new UIWidget(m.getEffect(), texture, 30 + i,
                                    w - j * size[0], 0.9f * h, size, new UICallback() {
                                @Override
                                public void onClick(MotionEvent e, UIWidget w) {
                                    Log.d("UIHandler", "clicked " + w.getCaption());
                                    if(e.getAction() != MotionEvent.ACTION_UP) return;
                                    /* failsafe if we are not selecting magic */
                                    if(BattleManager.getState() != BattleState.SELECT_MAGIC) return;
                                    currentAction.append(w.getCaption());
                                    UIHandler.hideWidgetByTag(32);
                                    UIHandler.hideWidgetByTag(33);
                                    BattleManager.updateState(0, null);
                                }
                            });
                            widget.setVisible(false);
                            ui.add(widget);
                            j++;
                        }
                    }
                }
                break;
            case NONE:
                /* empty UI */
                ui = new ArrayList<UIWidget>();
                break;
        }


    }

    public static void drawUI(GL10 gl) {
        if(ui == null) return;
        int width = PlatformData.getScreenWidth();
        int height = PlatformData.getScreenHeight();
        gl.glOrthof(0.0f, width, height, 0.0f, -1.0f, 0.0f);
        for(UIWidget widget : ui)
            widget.onDraw(gl);
    }

    public static void onClick(MotionEvent e, float x, float y) {
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
                widget.onClick(e);
            }
        }
    }

    public static void hideWidgetByTag(int tag) {
        setWidgetVisibility(tag, false);
    }

    public static void showWidgetByTag(int tag) {
        setWidgetVisibility(tag, true);
    }

    private static void setWidgetVisibility(int tag, boolean hide) {
        for(UIWidget w : ui) {
            if(w.getTag() == tag) {
                w.setVisible(hide);
            }
        }

    }

    public static void refresh(GL10 gl) {
        int i = 0;
        int h = PlatformData.getScreenHeight();
        int w = PlatformData.getScreenWidth();
        for(UIWidget widget : ui) {
            if(widget.getTag() == 20) {
                int[] texture = TextureHandler.createTextureFromString(gl,
                        PlayerList.getPlayerList().get(i).getHp() + "/" +
                                PlayerList.getPlayerList().get(i).getMax_hp(),
                        (int)(.05f * h), (int)(.15f * w), TextureHandler.TextAlign.ALIGN_RIGHT);
                widget.setTexture(gl, texture);
                i++;
            } else if(widget.getTag() > 3 && widget.getTag() < (Enemy.getEnemyList().size() + 4)) {
                if(Enemy.getEnemyList().get(widget.getTag() - 4).getHp() == 0) {
                    widget.setVisible(false);
                }
            }
        }

    }

    public static UIList getActive() {
        return active;
    }

    public static void setActive(UIList active) {
        UIHandler.active = active;
    }
}
