package com.lunacygames.thelastarmada.gamebattle;

import android.util.Log;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by zeus on 3/10/15.
 */
public class ActionEvent {
    private String event;
    private String parameter;
    private int player;
    private int target;
    private float speed;

    private static PriorityQueue<ActionEvent> eventList;


    public ActionEvent(String event) {
        this.event = event;
        this.parameter = "";
    }

    public static void emptyActionQueue() {
        /* we have at least four players and one enemy */
        eventList = new PriorityQueue<ActionEvent>(5, new Comparator<ActionEvent>() {
            @Override
            public int compare(ActionEvent lhs, ActionEvent rhs) {
                float speed_a = lhs.getPlayerSpeed();
                float speed_b = rhs.getPlayerSpeed();
                if(speed_a == speed_b)
                    return 0;
                else if(speed_a > speed_b)
                    return -1;
                else
                    return 1;
            }
        });
    }

    public static void enqueue(ActionEvent event) {
        Log.d("ActionEvent: ", "added event with speed " + event.getPlayerSpeed() + event.toString());

        eventList.add(event);
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setPlayerSpeed(int speed) {
        /* Allow the Random Number Goddess to play */
        Random rng = new Random();
        /* the idea is that we want some variations on the battle as to not make every turn
         * monolithic
         */
        this.speed = (0.7f * rng.nextFloat() + 0.4f) * (float)speed;
    }

    public float getPlayerSpeed() {
        return this.speed;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getEvent() {
        return this.event;
    }

    @Override
    public String toString() {
        return event + parameter + Integer.toString(player) + "," + Integer.toString(target);
    }

    public static ActionEvent getAction() {
        return eventList.remove();
    }

    public static boolean isEmpty() {
        return eventList.isEmpty();
    }

    public void setExtraParameter(String a) {
        this.parameter = a;
    }
}
