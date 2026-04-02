package com.sportsmanager.core;

import java.util.List;

public interface SportFactory {

    ISport createSport();

    IPlayer createPlayer(String name, String position);

    ITeam createTeam(String name, String logoPath);

    IMatch createMatch(ITeam home, ITeam away, int week);

    ILeague createLeague(String name, List<ITeam> teams);

    ICoach createCoach(String name, String specialty);

    ITactic createDefaultTactic();

    List<ITactic> getAvailableTactics();
}