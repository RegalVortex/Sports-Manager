package com.sportsmanager.core;

import java.util.List;


public interface IMatch {
  
 ITeam getHomeTeam();

    ITeam getAwayTeam();

    void simulate();

    MatchResult getResult();

    boolean isPlayed();

    List<String> getCommentary();

    void addMatchEventListener(IMatchEventListener l);

    void removeEventListener(IMatchEventListener l);
  
}
