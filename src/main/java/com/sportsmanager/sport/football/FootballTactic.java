package com.sportsmanager.sport.football;

import com.sportsmanager.core.ITactic;

import java.util.ArrayList;
import java.util.List;

public class FootballTactic implements ITactic {

    private String formation;
    private double attackMod;
    private double defenseMod;

    public FootballTactic(String formation) {
        this.formation = formation;
        calculateModifiers();
    }

    private void calculateModifiers() {
        switch (formation) {
            case "4-3-3":
                attackMod = 1.15;
                defenseMod = 0.95;
                break;
            case "5-3-2":
                attackMod = 0.95;
                defenseMod = 1.15;
                break;
            case "4-4-2":
            default:
                attackMod = 1.0;
                defenseMod = 1.0;
                break;
        }
    }

    @Override
    public String getName() {
        return formation;
    }

    @Override
    public double getAttackModifier() {
        return attackMod;
    }

    @Override
    public double getDefenseModifier() {
        return defenseMod;
    }

    public static List<FootballTactic> standardFormations() {
        List<FootballTactic> list = new ArrayList<>();
        list.add(new FootballTactic("4-4-2"));
        list.add(new FootballTactic("4-3-3"));
        list.add(new FootballTactic("5-3-2"));
        return list;
    }
}