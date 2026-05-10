package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;

/**
 * Small read-only calculations shared by console screens.
 */
final class UiStats {

    private UiStats() {
    }

    static int played(ILeague league, ITeam team) {
        return league.getWins(team) + league.getDraws(team) + league.getLosses(team);
    }

    static int goalsFor(ILeague league, ITeam team) {
        int value = 0;
        for (IMatch match : league.getAllFixtures()) {
            MatchResult result = match.getResult();
            if (result == null) {
                continue;
            }
            if (result.getHomeTeam().equals(team)) {
                value += result.getHomeScore();
            } else if (result.getAwayTeam().equals(team)) {
                value += result.getAwayScore();
            }
        }
        return value;
    }

    static int goalsAgainst(ILeague league, ITeam team) {
        int value = 0;
        for (IMatch match : league.getAllFixtures()) {
            MatchResult result = match.getResult();
            if (result == null) {
                continue;
            }
            if (result.getHomeTeam().equals(team)) {
                value += result.getAwayScore();
            } else if (result.getAwayTeam().equals(team)) {
                value += result.getHomeScore();
            }
        }
        return value;
    }

    static int injuredCount(ITeam team) {
        int count = 0;
        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                count++;
            }
        }
        return count;
    }

    static int unavailableCount(ITeam team) {
        int count = injuredCount(team);
        for (IPlayer player : team.getSquad()) {
            if (!player.isInjured() && player.getForm() == 0) {
                count++;
            }
        }
        return count;
    }

    static int morale(ITeam team) {
        if (team.getSquad().isEmpty()) {
            return 50;
        }
        int formSum = 0;
        for (IPlayer player : team.getSquad()) {
            formSum += player.getForm();
        }
        int formScore = (int) Math.round((formSum / (team.getSquad().size() * 3.0)) * 60);
        int healthScore = 40 - Math.min(40, injuredCount(team) * 8);
        return Math.max(0, Math.min(100, formScore + healthScore));
    }

    static String ordinal(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
