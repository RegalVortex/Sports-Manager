package com.sportsmanager.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SavedPlayer implements Serializable {

    private String name;
    private String position;
    private Map<String, Integer> attributes;
    private boolean injured;
    private int injuryGamesRemaining;

    public SavedPlayer(String name,
                       String position,
                       Map<String, Integer> attributes,
                       boolean injured,
                       int injuryGamesRemaining) {
        this.name = name;
        this.position = position;
        this.attributes = new HashMap<>(attributes);
        this.injured = injured;
        this.injuryGamesRemaining = injuryGamesRemaining;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Map<String, Integer> getAttributes() {
        return attributes;
    }

    public boolean isInjured() {
        return injured;
    }

    public int getInjuryGamesRemaining() {
        return injuryGamesRemaining;
    }
}