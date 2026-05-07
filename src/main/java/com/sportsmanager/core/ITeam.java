package com.sportsmanager.core;

import java.util.List;

public interface ITeam {

       String getName();

    List<IPlayer> getSquad();

    List<IPlayer> getStartingLineup();

    ICoach getCoach();

    ITactic getTactic();

    void setTactic(ITactic tactic);

    void substitutePlayer(IPlayer out, IPlayer in);

    void addPoints(int points);

    int getPoints();

    void setCoach(ICoach coach);

    void resetPoints();

    int getTeamOverallRating();
   
}
