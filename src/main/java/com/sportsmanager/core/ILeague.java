package com.sportsmanager.core;

import java.util.List;


public interface ILeague {
String getName();

    List<ITeam> getTeams();

    int getCurrentWeek();

    List<IMatch> getAllFixtures();

    List<IMatch> getFixturesForWeek(int week);

    void advanceWeek();

    LeagueStandings getStandings();

    boolean isSeasonOver();

    ITeam getChampion();
}
