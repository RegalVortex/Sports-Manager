package com.sportsmanager.core;

public class GameContext {


    private static GameContext instance;
    private ISport activeSport;
    private ILeague league;
    private ITeam playerTeam;
    private int currentSeason;

    private GameContext() {
        currentSeason = 1;

    }

    public static GameContext getInstance() {
        if (instance == null) {
            instance = new GameContext();
        }
        return instance;
    }

    public void startNewGame(ISport sport) {
        this.activeSport = sport;
        this.league = null;
        this.playerTeam = null;
        this.currentSeason = 1;
    }

    public ISport getSport() {
        return activeSport;
    }

    public void setSport(ISport sport) {
        this.activeSport = sport;
    }

    public ILeague getLeague() {
        return league;
    }

    public void setLeague(ILeague league) {
        this.league = league;
    }

    public ITeam getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(ITeam playerTeam) {
        this.playerTeam = playerTeam;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void nextSeason() {
        currentSeason++;
    }
}
