package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.ITactic;

import java.util.ArrayList;
import java.util.List;

public class VolleyballTactic implements ITactic {

    private final String name;
    private final double attackModifier;
    private final double defenseModifier;

    public VolleyballTactic(String name) {
        this.name = name;

        switch (name.toUpperCase()) {
            case "OFFENSIVE":
                this.attackModifier = 1.15;
                this.defenseModifier = 0.90;
                break;
            case "DEFENSIVE":
                this.attackModifier = 0.90;
                this.defenseModifier = 1.15;
                break;
            case "BALANCED":
            default:
                this.attackModifier = 1.00;
                this.defenseModifier = 1.00;
                break;
        }
    }

    public static List<VolleyballTactic> standardFormations() {
        List<VolleyballTactic> tactics = new ArrayList<>();
        tactics.add(new VolleyballTactic("OFFENSIVE"));
        tactics.add(new VolleyballTactic("BALANCED"));
        tactics.add(new VolleyballTactic("DEFENSIVE"));
        return tactics;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getAttackModifier() {
        return attackModifier;
    }

    @Override
    public double getDefenseModifier() {
        return defenseModifier;
    }

    @Override
    public String toString() {
        return name;
    }
}
