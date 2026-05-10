package com.sportsmanager.save;

import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;

import java.io.Serializable;

public class LoadedGame implements Serializable {

    private ISport sport;
    private ILeague league;
    private ITeam playerTeam;

    public LoadedGame(ISport sport, ILeague league, ITeam playerTeam) {
        this.sport = sport;
        this.league = league;
        this.playerTeam = playerTeam;
    }

    public ISport getSport() {
        return sport;
    }

    public ILeague getLeague() {
        return league;
    }

    public ITeam getPlayerTeam() {
        return playerTeam;
    }
}