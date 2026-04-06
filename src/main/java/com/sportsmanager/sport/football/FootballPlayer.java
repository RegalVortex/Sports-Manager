package com.sportsmanager.sport.football;

import com.sportsmanager.core.AbstractPlayer;

import java.util.Random;

public class FootballPlayer extends AbstractPlayer {

    public FootballPlayer(String name, String position) {
        super(name, position);
        attributes.put("pace", 50);
        attributes.put("shooting", 50);
        attributes.put("passing", 50);
        attributes.put("defending", 50);
        attributes.put("heading", 50);
        attributes.put("stamina", 50);
    }

    @Override
    public int getOverallRating() {
        int pace = attributes.getOrDefault("pace", 50);
        int shooting = attributes.getOrDefault("shooting", 50);
        int passing = attributes.getOrDefault("passing", 50);
        int defending = attributes.getOrDefault("defending", 50);
        int heading = attributes.getOrDefault("heading", 50);
        int stamina = attributes.getOrDefault("stamina", 50);

        if ("GK".equals(position)) {
            return (defending * 3 + heading + stamina * 2 + passing) / 7;
        }

        if ("ST".equals(position) || "LW".equals(position) || "RW".equals(position)) {
            return (pace * 2 + shooting * 3 + passing + heading + stamina) / 8;
        }

        if ("CB".equals(position) || "LB".equals(position) || "RB".equals(position)) {
            return (defending * 3 + heading * 2 + stamina * 2 + passing) / 8;
        }

        return (pace + shooting + passing * 2 + defending + heading + stamina) / 7;
    }

    public static FootballPlayer generateRandom(String position, String name) {
        FootballPlayer player = new FootballPlayer(name, position);
        Random random = new Random();

        player.attributes.put("pace", 50 + random.nextInt(41));
        player.attributes.put("shooting", 50 + random.nextInt(41));
        player.attributes.put("passing", 50 + random.nextInt(41));
        player.attributes.put("defending", 50 + random.nextInt(41));
        player.attributes.put("heading", 50 + random.nextInt(41));
        player.attributes.put("stamina", 50 + random.nextInt(41));

        return player;
    }
}