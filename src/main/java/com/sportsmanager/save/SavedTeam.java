package com.sportsmanager.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavedTeam implements Serializable {

    private String name;
    private int points;
    private String coachName;
    private String coachSpecialty;
    private String tacticName;
    private List<SavedPlayer> players;

    public SavedTeam(String name,
                     int points,
                     String coachName,
                     String coachSpecialty,
                     String tacticName,
                     List<SavedPlayer> players) {
        this.name = name;
        this.points = points;
        this.coachName = coachName;
        this.coachSpecialty = coachSpecialty;
        this.tacticName = tacticName;
        this.players = new ArrayList<>(players);
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public String getCoachName() {
        return coachName;
    }

    public String getCoachSpecialty() {
        return coachSpecialty;
    }

    public String getTacticName() {
        return tacticName;
    }

    public List<SavedPlayer> getPlayers() {
        return players;
    }
}
