package com.sportsmanager.core;

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
            warnings.add("Ilk kadro bos - oyuncu secilmemis.");
            return warnings;
        }

        for (IPlayer player : lineup) {
            if (player.isInjured()) {
                warnings.add("Ilk kadroda sakat oyuncu: " + player.getName()
                    + " (" + player.getInjuryGamesRemaining() + " hafta kaldi)");
            }
        }

        if (teamHasGkPlayer(team) && !lineupHasPosition(lineup, "GK")) {
            warnings.add("Ilk kadroda kaleci (GK) yok.");
        }

        int expected = expectedLineupSize(team);
        if (lineup.size() < expected) {
            warnings.add("Kadroda " + lineup.size() + " oyuncu var (beklenen " + expected + ").");
        }

        return warnings;
    }

    public static boolean teamHasGkPlayer(ITeam team) {
        return teamHasPosition(team, "GK");
    }

    public static int expectedLineupSize(ITeam team) {
        return team.getExpectedLineupSize();
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
