package com.lunacygames.thelastarmada.gameutils;

import android.util.Log;

import com.lunacygames.thelastarmada.gamebattle.BattleManager;
import com.lunacygames.thelastarmada.gamebattle.BattleState;
import com.lunacygames.thelastarmada.gamebattle.Enemy;
import com.lunacygames.thelastarmada.player.Player;
import com.lunacygames.thelastarmada.player.PlayerList;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by zeus on 3/12/15.
 */
public class Interpreter {
    public static void doCommand(String cmd) {
        Log.d("Interpreter: ", "processing command " + cmd);
        String[] split = new String[2];
        split[0] = cmd.substring(0, 3);
        split[1] = cmd.substring(3);
        String[] args;
        int source, target, hp;
        Log.d("Interpreter: ", "split[0] = " + split[0] + " split[1] = " + split[1]);
        boolean isEnemy = false;
        Random r = new Random();
        if(split[0].equalsIgnoreCase("ATK")) {
            args = split[1].split(",");
            source = Integer.parseInt(args[0]);
            target = Integer.parseInt(args[1]);
            float modifier = 0.4f * r.nextFloat() + 0.7f;
            if (source > 3) {
                /* this is an enemy, see if it died */
                if (Enemy.getEnemyList().get(source - 4).getHp() == 0) return;
                isEnemy = true;
                Log.d("Interpreter: ", "enemy attack " + source);
            } else {
                if (target > 3) {
                    /* if we killed all enemies, nothing to do */
                    if(Enemy.getActiveEnemies() == 0) return;
                    /* if we are attacking an enemy, ensure it is alive */
                    while(Enemy.getEnemyList().get(target - 4).getHp() == 0) {
                        /* if it isn't pick a random one */
                        target = r.nextInt(Enemy.getEnemyList().size()) + 4;
                    }
                }
            }
            if (isEnemy) {
                int damage;
                hp = PlayerList.getPlayerList().get(target).getHp();
                /* compute damage */
                damage = (int)(modifier * ((Enemy.getEnemyList().get(source - 4).getAtk()
                        - PlayerList.getPlayerList().get(target).getDef())));
                /* ensure that enemy does at least 1 damage */
                if(damage <= 0) damage = 1;
                hp -= damage;
                if (hp <= 0) {
                    Log.d("Interpreter: ", "Game Over");
                    hp = 0;
                }
                Log.d("Interpreter: ", "New player HP" + hp);
                PlayerList.getPlayerList().get(target).setHp(hp);
            } else {
                /* TODO: allow for the player to attack himself */
                hp = Enemy.getEnemyList().get(target - 4).getHp();
                /* compute damage */
                int damage = (int)(modifier * (PlayerList.getPlayerList().get(source).getAtk() -
                        Enemy.getEnemyList().get(target - 4).getDef()));
                /* ensure that at least we hit for 1 */
                if(damage <= 0) damage = 1;
                hp -= damage;
                if (hp <= 0) {
                    Log.d("Interpreter: ", "Enemy defeated");
                    hp = 0;
                    Enemy.reduceEnemyCount();
                    ArrayList<String> cmdList =
                            Enemy.getEnemyList().get(target - 4).getOnDefeatScript();
                    for(String s : cmdList) {
                        Interpreter.doCommand(s);
                    }
                }
                Log.d("Interpreter: ", "New enemy HP" + hp);
                Enemy.getEnemyList().get(target - 4).setHp(hp);
                if(Enemy.getActiveEnemies() == 0) {
                    BattleManager.setState(BattleState.VICTORY);
                    GameState.setGameState(GameStateList.BATTLE_VICTORY);
                }
            }
        } else if(split[0].equalsIgnoreCase("EXP")) {
            int gain = Integer.parseInt(split[1]);
            for(Player p : PlayerList.getPlayerList()) {
                p.addExp(gain);
            }

        } else if(split[0].equalsIgnoreCase("MAG")) {
            char spell = split[1].charAt(0);
            split[1] = split[1].substring(1);
            args = split[1].split(",");
            int damage;
            source = Integer.parseInt(args[0]);
            target = Integer.parseInt(args[1]);
            if(spell == 'H') {
                Log.d("Interpreter: ", "processing heal spell " + cmd);

                /* source was a monster */
                if (source > 3) {
                    /* needs to be alive to do anything */
                    if(Enemy.getEnemyList().get(source - 4).getMax_hp() == 0) return;
                    damage = (int) ((0.4f * r.nextFloat() + 0.7) *
                            Enemy.getEnemyList().get(source - 4).getSatk());
                } else {
                    damage = (int) ((0.4f * r.nextFloat() + 0.7) *
                            PlayerList.getPlayerList().get(source).getSatk());
                }

                /* target is a monster */
                if (target > 3) {
                    /* if we killed all enemies, nothing to do */
                    if(Enemy.getActiveEnemies() == 0) return;
                    /* if we are attacking an enemy, ensure it is alive */
                    while(Enemy.getEnemyList().get(target - 4).getHp() == 0) {
                        /* if it isn't pick a random one */
                        target = r.nextInt(Enemy.getEnemyList().size()) + 4;
                    }

                    hp = Enemy.getEnemyList().get(target - 4).getHp();
                    hp += damage;
                    if (hp > Enemy.getEnemyList().get(target - 4).getMax_hp())
                        hp = Enemy.getEnemyList().get(target - 4).getMax_hp();

                    Enemy.getEnemyList().get(target - 4).setHp(hp);
                } else {
                    hp = PlayerList.getPlayerList().get(target).getHp();
                    hp += damage;
                    if (hp > PlayerList.getPlayerList().get(target).getMax_hp())
                        hp = PlayerList.getPlayerList().get(target).getMax_hp();

                    PlayerList.getPlayerList().get(target).setHp(hp);
                }
            } else if(spell == 'N') {
                /* nova gets its own special treatment, only Aslaugh gets the spell */
                damage = PlayerList.getPlayerList().get(3).getHp() / 2;
                /* Aslaugh cuts her HP in half for this, rounding up */
                PlayerList.getPlayerList().get(3).setHp(PlayerList.getPlayerList().get(3).getHp()
                        - damage);
                if(target > 3) {
                    if(Enemy.getActiveEnemies() == 0) return;
                    /* if we are attacking an enemy, ensure it is alive */
                    while(Enemy.getEnemyList().get(target - 4).getHp() == 0) {
                        /* if it isn't pick a random one */
                        target = r.nextInt(Enemy.getEnemyList().size()) + 4;
                    }

                    hp = Enemy.getEnemyList().get(target - 4).getHp();
                } else {
                    hp = PlayerList.getPlayerList().get(target).getHp();
                }

                hp -= damage;
                if(hp < 0) hp = 0;

                if(target > 3) {
                    if(hp == 0) {
                        Enemy.reduceEnemyCount();
                    }
                    Enemy.getEnemyList().get(target - 4).setHp(hp);
                } else {
                    if(hp == 0) {
                        /* TODO: Game over */
                    }
                    PlayerList.getPlayerList().get(target).setHp(hp);
                }

            } else {
                int defence;
                /* attack spell */
                if(source > 3) {
                    /* attack came from a monster, check if it is alive */
                    if (Enemy.getEnemyList().get(source - 4).getHp() == 0) return;

                    damage = Enemy.getEnemyList().get(source - 4).getSatk();

                } else {
                    damage = PlayerList.getPlayerList().get(source).getSatk();
                }

                if(target > 3) {
                    if(Enemy.getActiveEnemies() == 0) return;
                    /* if we are attacking an enemy, ensure it is alive */
                    while(Enemy.getEnemyList().get(target - 4).getHp() == 0) {
                        /* if it isn't pick a random one */
                        target = r.nextInt(Enemy.getEnemyList().size()) + 4;
                    }

                    defence = Enemy.getEnemyList().get(target - 4).getRes();
                    damage = (int)((0.7f * r.nextFloat() + 0.4) * (damage - defence));
                    if(damage <= 0) damage = 1;
                    hp = Enemy.getEnemyList().get(target - 4).getHp();
                    hp -= damage;

                    if(hp <= 0) {
                        hp = 0;
                        Enemy.reduceEnemyCount();
                        for(String s : Enemy.getEnemyList().get(target - 4).getOnDefeatScript()) {
                            Interpreter.doCommand(s);
                        }
                    }

                    Enemy.getEnemyList().get(target - 4).setHp(hp);

                } else {
                    hp = PlayerList.getPlayerList().get(target).getHp();
                    defence = PlayerList.getPlayerList().get(target).getRes();
                    damage = (int)((0.7f * r.nextFloat() + 0.4) * (damage - defence));
                    if(damage <= 0) damage = 1;
                    hp -= damage;
                    if(hp <= 0) {
                        /* TODO: Game over */
                        hp = 0;
                    }

                    PlayerList.getPlayerList().get(target).setHp(hp);
                }

                /* additional spell effects */
                if(spell == 'F') {
                    if(r.nextFloat() <= 0.15f) {
                        defence = (target > 3) ? Enemy.getEnemyList().get(target - 4).getDef() :
                                PlayerList.getPlayerList().get(target).getDef();
                        defence = (int)(0.9f * defence);
                        if(target > 3) {
                            Enemy.getEnemyList().get(target - 4).setDef(defence);
                        } else {
                            PlayerList.getPlayerList().get(target).setDef(defence);
                        }
                    }
                } else if(spell == 'I') {
                    if(r.nextFloat() <= 0.15f) {
                        defence = (target > 3) ? Enemy.getEnemyList().get(target - 4).getSpd() :
                                PlayerList.getPlayerList().get(target).getSpd();
                        defence = (int)(0.9f * defence);
                        if(target > 3) {
                            Enemy.getEnemyList().get(target - 4).setSpd(defence);
                        } else {
                            PlayerList.getPlayerList().get(target).setSpd(defence);
                        }

                    }
                }
            }
        } else {
            Log.e("Interpreter: ", "bad command " + cmd);
        }
    }
}
