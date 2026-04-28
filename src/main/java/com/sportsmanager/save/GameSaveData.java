package com.sportsmanager.save;

import java.util.ArrayList;
import java.util.List;

public class GameSaveData {

    private String sportName;
    private String leagueName;
    private String playerTeamName;
    private int currentWeek;

    private List<SavedTeam> teams;
    private List<SavedMatch> matches;

    public GameSaveData(String sportName,
                        String leagueName,
                        String playerTeamName,
                        int currentWeek,
                        List<SavedTeam> teams,
                        List<SavedMatch> matches) {
        this.sportName = sportName;
        this.leagueName = leagueName;
        this.playerTeamName = playerTeamName;
        this.currentWeek = currentWeek;
        this.teams = new ArrayList<>(teams);
        this.matches = new ArrayList<>(matches);
    }

    public String getSportName() {
        return sportName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getPlayerTeamName() {
        return playerTeamName;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public List<SavedTeam> getTeams() {
        return teams;
    }

    public List<SavedMatch> getMatches() {
        return matches;
    }
}