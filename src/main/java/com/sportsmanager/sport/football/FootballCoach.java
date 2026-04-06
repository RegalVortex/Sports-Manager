package com.sportsmanager.sport.football;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;

import java.util.List;

public class FootballCoach implements ICoach {

    private String name;
    private String specialty;

    public FootballCoach(String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSpecialty() {
        return specialty;
    }

    @Override
    public void trainPlayers(List<IPlayer> players) {
        for (IPlayer player : players) {
            if (player.isInjured()) {
                continue;
            }

            if ("ATTACKING".equalsIgnoreCase(specialty)) {
                player.train("shooting", 2);
                player.train("pace", 1);
            } else if ("DEFENSIVE".equalsIgnoreCase(specialty)) {
                player.train("defending", 2);
                player.train("heading", 1);
            } else if ("FITNESS".equalsIgnoreCase(specialty)) {
                player.train("stamina", 2);
            }
        }
    }
}