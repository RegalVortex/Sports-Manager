package com.sportsmanager.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sportName;
    private String leagueName;
    private String playerTeamName;
    private int currentWeek;
    private int currentSeason;

    private List<SavedTeam> teams;
    private List<SavedMatch> matches;

    public GameSaveData(String sportName,
                        String leagueName,
                        String playerTeamName,
                        int currentWeek,
                        int currentSeason,
                        List<SavedTeam> teams,
                        List<SavedMatch> matches) {
        this.sportName = sportName;
        this.leagueName = leagueName;
        this.playerTeamName = playerTeamName;
        this.currentWeek = currentWeek;
        this.currentSeason = currentSeason;
        this.teams = new ArrayList<>(teams);
        this.matches = new ArrayList<>(matches);
    }

    public String getSportName()       { return sportName; }
    public String getLeagueName()      { return leagueName; }
    public String getPlayerTeamName()  { return playerTeamName; }
    public int getCurrentWeek()        { return currentWeek; }
    public int getCurrentSeason()      { return currentSeason; }
    public List<SavedTeam> getTeams()  { return teams; }
    public List<SavedMatch> getMatches() { return matches; }
}
