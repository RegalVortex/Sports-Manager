package com.sportsmanager.sport.football;

import com.sportsmanager.core.AbstractLeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LeagueStandings;
import com.sportsmanager.core.MatchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FootballLeague extends AbstractLeague {

    public FootballLeague(String name, List<ITeam> teams) {
        super(name, teams);
    }

    @Override
    protected IMatch createMatch(ITeam home, ITeam away, int week) {
        return new FootballMatch(home, away, week);
    }

    @Override
    protected void applyMatchResult(IMatch match) {
        MatchResult result = match.getResult();
        if (result == null) {
            return;
        }

        if (result.getHomeScore() > result.getAwayScore()) {
            result.getHomeTeam().addPoints(3);
        } else if (result.getAwayScore() > result.getHomeScore()) {
            result.getAwayTeam().addPoints(3);
        } else {
            result.getHomeTeam().addPoints(1);
            result.getAwayTeam().addPoints(1);
        }
    }

    @Override
    public LeagueStandings getStandings() {
        List<ITeam> ranked = new ArrayList<>(teams);
        ranked.sort(Comparator.comparingInt(ITeam::getPoints).reversed().thenComparing(ITeam::getName));
        return new LeagueStandings(ranked);
    }

    @Override
    public ITeam getChampion() {
        LeagueStandings standings = getStandings();
        if (standings.getTeams().isEmpty()) {
            return null;
        }
        return standings.getTeams().get(0);
    }
}
