package com.sportsmanager.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaguePreset {

    private final String sportName;
    private final String leagueName;
    private final List<String> teamNames;

    public LeaguePreset(String sportName, String leagueName, List<String> teamNames) {
        this.sportName = sportName;
        this.leagueName = leagueName;
        this.teamNames = new ArrayList<>(teamNames);
    }

    public String getSportName() {
        return sportName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public List<String> getTeamNames() {
        return Collections.unmodifiableList(teamNames);
    }
}
