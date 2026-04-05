package com.sportsmanager.core;

public class MatchResult {

    private final ITeam homeTeam;
    private final ITeam awayTeam;
    private final int homeScore;
    private final int awayScore;

    public MatchResult(ITeam homeTeam, ITeam awayTeam, int homeScore, int awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public ITeam getHomeTeam() {
        return homeTeam;
    }

    public ITeam getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public ITeam getWinner() {
        if (homeScore > awayScore) return homeTeam;
        if (awayScore > homeScore) return awayTeam;
        return null;
    }

    @Override
    public String toString() {
        return homeTeam.getName() + " " + homeScore + " - " + awayScore + " " + awayTeam.getName();
    }
}