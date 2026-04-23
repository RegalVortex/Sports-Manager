package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;

import java.util.List;

public class VolleyballCoach implements ICoach {

    private final String name;
    private final String specialty;

    public VolleyballCoach(String name, String specialty) {
        this.name = name;
        this.specialty = specialty == null ? "BALANCED" : specialty.toUpperCase();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public void trainPlayers(List<IPlayer> players) {
        if (players == null) {
            return;
        }

        for (IPlayer player : players) {
            if (player == null || player.isInjured()) {
                continue;
            }

            switch (specialty) {
                case "ATTACKING":
                    player.train("spike", 2);
                    player.train("serve", 1);
                    break;
                case "DEFENSIVE":
                    player.train("block", 2);
                    player.train("receive", 1);
                    break;
                case "FITNESS":
                    player.train("stamina", 2);
                    break;
                default:
                    player.train("serve", 1);
                    player.train("receive", 1);
                    break;
            }
        }
    }
}
