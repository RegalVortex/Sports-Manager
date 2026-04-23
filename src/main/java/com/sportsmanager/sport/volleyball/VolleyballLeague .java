package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractLeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;

import java.util.List;

public class VolleyballLeague extends AbstractLeague {

    public VolleyballLeague(String name, List<ITeam> teams) {
        super(name, teams);
    }

    @Override
    protected IMatch createMatch(ITeam home, ITeam away, int week) {
        return new VolleyballMatch(home, away, week);
    }

    @Override
    protected void applyMatchResult(IMatch match) {
        MatchResult result = match.getResult();

        int homeSets = result.getHomeScore();
        int awaySets = result.getAwayScore();

        if (homeSets > awaySets) {
            if (homeSets == 3 && (awaySets == 0  awaySets == 1)) {
                result.getHomeTeam().addPoints(3);
            } else {
                result.getHomeTeam().addPoints(2);
                result.getAwayTeam().addPoints(1);
            }
        } else if (awaySets > homeSets) {
            if (awaySets == 3 && (homeSets == 0  homeSets == 1)) {
                result.getAwayTeam().addPoints(3);
            } else {
                result.getAwayTeam().addPoints(2);
                result.getHomeTeam().addPoints(1);
            }
        }
    }
}
