package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless helper that inspects a team's starting lineup.
 */
public final class LineupWarnings {

    private LineupWarnings() {
    }

    public static List<String> check(ITeam team) {
        List<String> warnings = new ArrayList<>();
        List<IPlayer> lineup = team.getStartingLineup();

        if (lineup.isEmpty()) {
            warnings.add("Starting lineup is empty - no players selected.");
            return warnings;
        }

        for (IPlayer player : lineup) {
            if (player.isInjured()) {
                warnings.add("Injured player in lineup: " + player.getName()
                    + " (" + player.getInjuryGamesRemaining() + " wk remaining)");
            }
        }

        if (teamHasGkPlayer(team) && !lineupHasPosition(lineup, "GK")) {
            warnings.add("No goalkeeper (GK) in starting lineup.");
        }

        int expected = expectedLineupSize(team);
        if (lineup.size() < expected) {
            warnings.add("Lineup has " + lineup.size() + " players (expected " + expected + ").");
        }

        return warnings;
    }

    public static boolean teamHasGkPlayer(ITeam team) {
        return teamHasPosition(team, "GK");
    }

    public static int expectedLineupSize(ITeam team) {
        if (teamHasGkPlayer(team)) {
            return 11;
        }
        if (team.getSquad().size() <= 18) {
            return 6;
        }
        return 11;
    }

    private static boolean teamHasPosition(ITeam team, String position) {
        for (IPlayer player : team.getSquad()) {
            if (position.equalsIgnoreCase(player.getPosition())) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineupHasPosition(List<IPlayer> lineup, String position) {
        for (IPlayer player : lineup) {
            if (position.equalsIgnoreCase(player.getPosition())) {
                return true;
            }
        }
        return false;
    }
}
