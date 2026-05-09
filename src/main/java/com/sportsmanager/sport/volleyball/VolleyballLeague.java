package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractLeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LeagueStandings;
import com.sportsmanager.core.MatchResult;

import java.util.ArrayList;
import java.util.Comparator;
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
        if (result == null) {
            return;
        }

        int homeSets = result.getHomeScore();
        int awaySets = result.getAwayScore();

        // Voleybol puan sistemi: 3-0 veya 3-1 = 3P (kazanan) / 0P (kaybeden)
        //                         3-2 = 2P (kazanan) / 1P (kaybeden)
        if (homeSets > awaySets) {
            if (awaySets <= 1) {          // 3-0 veya 3-1: süpürme
                result.getHomeTeam().addPoints(3);
                // Kaybeden 0P alır
            } else {                      // 3-2: zorlu maç
                result.getHomeTeam().addPoints(2);
                result.getAwayTeam().addPoints(1);
            }
        } else if (awaySets > homeSets) {
            if (homeSets <= 1) {          // 0-3 veya 1-3: süpürülme
                result.getAwayTeam().addPoints(3);
                // Kaybeden 0P alır
            } else {                      // 2-3: zorlu maç
                result.getAwayTeam().addPoints(2);
                result.getHomeTeam().addPoints(1);
            }
        }
        // Beraberlik yok (voleybol her zaman bir kazanan üretir)
    }

    @Override
    public LeagueStandings getStandings() {
        List<ITeam> ranked = new ArrayList<>(teams);
        ranked.sort((a, b) -> {
            if (b.getPoints() != a.getPoints()) {
                return b.getPoints() - a.getPoints();
            }
            int sdDiff = getSetDifference(b) - getSetDifference(a);
            if (sdDiff != 0) {
                return sdDiff;
            }
            return a.getName().compareTo(b.getName());
        });
        return new LeagueStandings(ranked);
    }

    private int getSetDifference(ITeam team) {
        int setsWon = 0;
        int setsLost = 0;
        for (IMatch match : fixtures) {
            MatchResult result = match.getResult();
            if (result == null) continue;
            if (result.getHomeTeam().equals(team)) {
                setsWon += result.getHomeScore();
                setsLost += result.getAwayScore();
            } else if (result.getAwayTeam().equals(team)) {
                setsWon += result.getAwayScore();
                setsLost += result.getHomeScore();
            }
        }
        return setsWon - setsLost;
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
