package com.lunacygames.thelastarmada.gameutils;

import android.util.Log;

import com.lunacygames.thelastarmada.gamebattle.BattleManager;
import com.lunacygames.thelastarmada.gamebattle.BattleState;
import com.lunacygames.thelastarmada.gamebattle.Enemy;
import com.lunacygames.thelastarmada.gameui.TopMessage;
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
        String targetName;
        String sourceName, message;
        int source, target, hp;
        Log.d("Interpreter: ", "split[0] = " + split[0] + " split[1] = " + split[1]);
        boolean isEnemy = false;
        Random r = new Random();
        if(split[0].equalsIgnoreCase("ATK")) {
            /* attack command */
            processAttack(split[1]);
        } else if(split[0].equalsIgnoreCase("EXP")) {
            /* Gain experience */
            int gain = Integer.parseInt(split[1]);
            for(Player p : PlayerList.getPlayerList()) {
                p.addExp(gain);
            }
        } else if(split[0].equalsIgnoreCase("MAG")) {
            /* Magic spell */
            char spell = split[1].charAt(0);
            split[1] = split[1].substring(1);
            args = split[1].split(",");
            int damage;
            source = Integer.parseInt(args[0]);
            target = Integer.parseInt(args[1]);

            if(spell == 'H') {
                /* Heal */
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
                    targetName = Enemy.getEnemyList().get(target - 4).getName();
                    Enemy.getEnemyList().get(target - 4).setHp(hp);
                } else {
                    hp = PlayerList.getPlayerList().get(target).getHp();
                    hp += damage;
                    if (hp > PlayerList.getPlayerList().get(target).getMax_hp())
                        hp = PlayerList.getPlayerList().get(target).getMax_hp();

                    PlayerList.getPlayerList().get(target).setHp(hp);
                    targetName = PlayerList.getPlayerList().get(target).getName();
                }
                TopMessage.showMessage(targetName + " was healed by " +
                        Integer.toString(damage) + " points!");
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
                    targetName = Enemy.getEnemyList().get(target - 4).getName();
                    hp = Enemy.getEnemyList().get(target - 4).getHp();
                } else {
                    hp = PlayerList.getPlayerList().get(target).getHp();
                    targetName = PlayerList.getPlayerList().get(target).getName();
                }

                hp -= damage;
                if(hp < 0) hp = 0;

                if(target > 3) {
                    if(hp == 0) {
                        Enemy.reduceEnemyCount();
                        if(Enemy.getEnemyList().get(target - 4).getHp() == 0)
                            for(String s: Enemy.getEnemyList().get(target - 4).getOnDefeatScript())
                                Interpreter.doCommand(cmd);
                    }
                    Enemy.getEnemyList().get(target - 4).setHp(hp);
                } else {
                    if(hp == 0) {
                        /* TODO: Game over */
                    }
                    PlayerList.getPlayerList().get(target).setHp(hp);
                }

                TopMessage.showMessage(targetName + " was dealt " +
                        Integer.toString(damage) + " damage by Aslaug!");
            } else {
                /* offensive spell */
                int defence;
                /* attack spell */
                if(source > 3) {
                    /* attack came from a monster, check if it is alive */
                    if (Enemy.getEnemyList().get(source - 4).getHp() == 0) return;

                    damage = Enemy.getEnemyList().get(source - 4).getSatk();
                    sourceName = Enemy.getEnemyList().get(source - 4).getName();
                } else {
                    damage = PlayerList.getPlayerList().get(source).getSatk();
                    sourceName = PlayerList.getPlayerList().get(source).getName();
                }

                if(target > 3) {
                    if(Enemy.getActiveEnemies() == 0) return;
                    /* if we are attacking an enemy, ensure it is alive */
                    while(Enemy.getEnemyList().get(target - 4).getHp() == 0) {
                        /* if it isn't pick a random one */
                        target = r.nextInt(Enemy.getEnemyList().size()) + 4;
                    }

                    defence = Enemy.getEnemyList().get(target - 4).getRes();
                    targetName = Enemy.getEnemyList().get(target - 4).getName();
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
                    targetName = PlayerList.getPlayerList().get(target).getName();
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
                message = targetName + " was dealt " + Integer.toString(damage)
                        + " damage by " + sourceName + "!";
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
                        message += " The defence was reduced!";
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
                        message += " The speed was reduced!";
                    }
                }
                TopMessage.showMessage(message);
            }

        } else {
            Log.e("Interpreter: ", "bad command " + cmd);
        }

        if(Enemy.getActiveEnemies() == 0) {
            BattleManager.setState(BattleState.VICTORY);
            GameState.setGameState(GameStateList.BATTLE_VICTORY);
        }
    }

    private static void processAttack(String cmd) {
        String[] args = cmd.split(",");
        int damage, hp, defence;
        int source = Integer.parseInt(args[0]);
        int target = Integer.parseInt(args[1]);
        int tagOffset = PlayerList.getPlayerList().size();
        String sourceName, message, targetName;

        Random rng = new Random();  /* the random number goddess */
        float modifier;

        if(source >= tagOffset) {
            /* the source of the attack was an enemy, if it is dead, do nothing */
            if(Enemy.getEnemyList().get(source - tagOffset).getHp() == 0) return;
            damage = Enemy.getEnemyList().get(source - tagOffset).getAtk();
            sourceName = Enemy.getEnemyList().get(source - tagOffset).getName();
        } else {
            /* source of attack was the player */
            damage = PlayerList.getPlayerList().get(source).getAtk();
            sourceName = PlayerList.getPlayerList().get(source).getName();
        }

        if(target >= tagOffset) {
            /* target was an enemy */
            if(Enemy.getActiveEnemies() == 0) return;

            /* make sure the enemy is alive */
            while(Enemy.getEnemyList().get(target - tagOffset).getHp() == 0) {
                /* if it isn't pick a random one */
                target = rng.nextInt(Enemy.getEnemyList().size()) + tagOffset;
            }
            defence = Enemy.getEnemyList().get(target - tagOffset).getDef();
            targetName = Enemy.getEnemyList().get(target - tagOffset).getName();
            hp = Enemy.getEnemyList().get(target - tagOffset).getHp();
        } else {
            defence = PlayerList.getPlayerList().get(target).getDef();
            targetName = PlayerList.getPlayerList().get(target).getName();
            hp = PlayerList.getPlayerList().get(target).getHp();
        }

        /* compute damage */
        modifier = rng.nextFloat() * 0.4f + 0.7f;
        damage = (int)(modifier * (damage - defence));
        if(damage <= 0) damage = 1;
        hp -= damage;
        if(hp < 0) hp = 0;

        if(target >= tagOffset) {
            if(hp == 0) {
                Enemy.reduceEnemyCount();
                message = targetName + " was defeated by " + sourceName + "!";
                TopMessage.showMessage(message);
                for(String s : Enemy.getEnemyList().get(target - tagOffset).getOnDefeatScript())
                    Interpreter.doCommand(s);
            } else {
                message = targetName + " was was dealt " +
                        Integer.toString(damage) + " damage by " + sourceName + "!";
            }
            Enemy.getEnemyList().get(target - tagOffset).setHp(hp);
            TopMessage.showMessage(message);
        } else {
            if(hp == 0) {
                /*TODO: game over */
                message = targetName + " was defeated by " + sourceName +
                        "! A party member has perished!";
                GameState.setGameState(GameStateList.GAME_OVER_LOSS);
            } else {
                message = targetName + " was dealt " +
                        Integer.toString(damage) + " damage by " + sourceName + "!";
            }
            PlayerList.getPlayerList().get(target).setHp(hp);
            TopMessage.showMessage(message);
        }
    }
}
