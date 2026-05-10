package com.sportsmanager.sport.football;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;

import java.util.List;
import java.util.Random;

public class FootballCoach implements ICoach {

    private static final String[] COACH_FIRST = {
        "Vincenzo", "Carlo", "Pep", "Jürgen", "Diego", "Jorge",
        "Fatih", "Hamza", "Volkan", "Okan", "Ersun", "Hüseyin",
        "Roberto", "Rafael", "Maurizio", "Thomas", "Julian"
    };
    private static final String[] COACH_LAST = {
        "Terim", "Hamzaoğlu", "Kahraman", "Buruk", "Yanal", "Güneş",
        "Italiano", "Ancelotti", "Allegri", "Conte", "Guardiola",
        "Klopp", "Mourinho", "Simeone", "Emery", "Tuchel", "Nagelsmann"
    };

    private String name;
    private String specialty;
    private int quality;

    public FootballCoach(String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
        this.quality = new Random().nextInt(10) + 1; // 1-10 rastgele
    }

    public static String randomCoachName() {
        Random r = new Random();
        return COACH_FIRST[r.nextInt(COACH_FIRST.length)] + " " +
               COACH_LAST[r.nextInt(COACH_LAST.length)];
    }

    @Override
    public int getQuality() {
        return quality;
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
        int bonus = quality / 5; // quality 1-4 → 0, quality 5-9 → 1, quality 10 → 2

        for (IPlayer player : players) {
            if (player.isInjured()) continue;

            if ("ATTACKING".equalsIgnoreCase(specialty)) {
                player.train("shooting", 2 + bonus);
                player.train("pace", 1 + bonus);
            } else if ("DEFENSIVE".equalsIgnoreCase(specialty)) {
                player.train("defending", 2 + bonus);
                player.train("heading", 1 + bonus);
            } else if ("FITNESS".equalsIgnoreCase(specialty)) {
                player.train("stamina", 2 + bonus);
            }
        }
    }
}