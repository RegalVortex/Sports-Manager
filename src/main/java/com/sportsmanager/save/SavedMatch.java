package com.sportsmanager.save;

import java.io.Serializable;

public class SavedMatch implements Serializable {

    private int week;
    private String homeTeamName;
    private String awayTeamName;
    private int homeScore;
    private int awayScore;
    private boolean played;

    public SavedMatch(int week,
                      String homeTeamName,
                      String awayTeamName,
                      int homeScore,
                      int awayScore,
                      boolean played) {
        this.week = week;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.played = played;
    }

    public int getWeek() {
        return week;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public boolean isPlayed() {
        return played;
    }
}