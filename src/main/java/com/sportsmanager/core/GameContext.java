package com.sportsmanager.core;

/**
 * Singleton that holds the active game state.
 *
 * Season number is the single source of truth stored in the league
 * (AbstractLeague.currentSeason).  GameContext delegates to it so that
 * there is no duplicate counter that can drift.
 */
public class GameContext {

    private static GameContext instance;
    private ISport activeSport;
    private ILeague league;
    private ITeam playerTeam;

    private GameContext() {
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

    /**
     * Returns the current season number.
     * Delegates to the active league so there is exactly one counter.
     * Falls back to 1 if no league is set yet.
     */
    public int getCurrentSeason() {
        return league != null ? league.getCurrentSeason() : 1;
    }

    /**
     * @deprecated Season is now advanced exclusively by
     *             {@code AbstractLeague.resetSeason()}.
     *             Calling this method is a no-op to prevent double-increment.
     */
    @Deprecated
    public void nextSeason() {
        // intentionally empty — season counter lives in AbstractLeague
    }
}
