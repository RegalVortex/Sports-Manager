package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractPlayer;

import java.util.Random;

public class VolleyballPlayer extends AbstractPlayer {

    public VolleyballPlayer(String name, String position) {
        super(name, position);
        attributes.put("serve", 50);
        attributes.put("spike", 50);
        attributes.put("block", 50);
        attributes.put("receive", 50);
        attributes.put("set", 50);
        attributes.put("stamina", 50);
    }

    @Override
    public int getOverallRating() {
        int serve = attributes.getOrDefault("serve", 50);
        int spike = attributes.getOrDefault("spike", 50);
        int block = attributes.getOrDefault("block", 50);
        int receive = attributes.getOrDefault("receive", 50);
        int set = attributes.getOrDefault("set", 50);
        int stamina = attributes.getOrDefault("stamina", 50);

        if ("LIBERO".equals(position)) {
            return (receive * 3 + stamina * 2 + serve + set) / 7;
        }

        if ("SETTER".equals(position)) {
            return (set * 3 + receive * 2 + serve + stamina + spike) / 8;
        }

        if ("MIDDLE_BLOCKER".equals(position)) {
            return (block * 3 + spike * 2 + stamina * 2 + serve) / 8;
        }

        if ("OPPOSITE".equals(position) || "OUTSIDE_HITTER".equals(position)) {
            return (spike * 3 + serve * 2 + receive + stamina + block) / 8;
        }

        return (serve + spike + block + receive + set + stamina) / 6;
    }

    public static VolleyballPlayer generateRandom(String position, String name) {
        VolleyballPlayer player = new VolleyballPlayer(name, position);
        Random random = new Random();

        player.attributes.put("serve", 50 + random.nextInt(41));
        player.attributes.put("spike", 50 + random.nextInt(41));
        player.attributes.put("block", 50 + random.nextInt(41));
        player.attributes.put("receive", 50 + random.nextInt(41));
        player.attributes.put("set", 50 + random.nextInt(41));
        player.attributes.put("stamina", 50 + random.nextInt(41));

        return player;
    }
}