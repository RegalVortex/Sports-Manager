package com.sportsmanager.core;

import java.util.List;

public class LeagueStandings {

    private final List<ITeam> rankedTeams;

    public LeagueStandings(List<ITeam> rankedTeams) {
        this.rankedTeams = rankedTeams;

    }


    public List<ITeam> getTeams(){
        return  rankedTeams;
    }


    public  int getRankOf(ITeam team){
        return rankedTeams.indexOf(team)+1;

    }
}
