package com.example.webbattle;

import jakarta.websocket.Session;

import java.util.ArrayList;

public class User {
    public String name;
    public ArrayList<Monster> monsters;
    // Refers to index of whichever monster is currently active
    public Monster active;
    public BattleSession session;

    // Set by battle class.
    public int id;

    public User(BattleSession session, String name, ArrayList<Monster> monsters) {
        this.session = session;
        this.name = name;
        this.monsters = monsters;
        this.active = monsters.get(0);
    }

    public void swapMonster(int monsterId){
        if(getMonster(monsterId).health > 0){
            this.active = getMonster(monsterId);
        }

        // Notify user that they cannot switch monsters if monster in desired index has no health
    }

    public Monster getMonster(int monsterId) {
        for (Monster monster : monsters) {
            if (monster.id == monsterId) {
                return monster;
            }
        }
        return null;
    }
}
