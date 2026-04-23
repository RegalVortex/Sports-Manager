package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;

import java.util.List;
import java.util.Random;

public class VolleyballMatch extends AbstractMatch {

    private final Random random = new Random();

    public VolleyballMatch(ITeam homeTeam, ITeam awayTeam, int week) {
        super(homeTeam, awayTeam, week);
    }

    @Override
    protected void simulateMatch() {
        int homeSets = 0;
        int awaySets = 0;
        int setNumber = 1;

        fireEvent("Match Start: " + homeTeam.getName() + " vs " + awayTeam.getName());

        while (homeSets < 3 && awaySets < 3) {
            int[] setScore = simulateSet(homeTeam, awayTeam, homeSets == 2 && awaySets == 2);

            int homePoints = setScore[0];
            int awayPoints = setScore[1];

            if (homePoints > awayPoints) {
                homeSets++;
            } else {
                awaySets++;
            }

            fireEvent("Set " + setNumber + ": " + homeTeam.getName() + " " + homePoints +
                    " - " + awayPoints + " " + awayTeam.getName());

            setNumber++;
        }

        result = new MatchResult(homeTeam, awayTeam, homeSets, awaySets);
        fireEvent("Full Time: " + result.toString());
    }

    private int[] simulateSet(ITeam teamA, ITeam teamB, boolean finalSet) {
        double teamAStrength = calculateAttackPower(teamA) + calculateDefensePower(teamA);
        double teamBStrength = calculateAttackPower(teamB) + calculateDefensePower(teamB);

        int targetScore = finalSet ? 15 : 25;

        int teamAPoints = targetScore;
        int teamBPoints = targetScore;

        if (teamAStrength > teamBStrength) {
            teamBPoints = Math.max(finalSet ? 10 : 18, targetScore - random.nextInt(6));
            teamAPoints = Math.max(targetScore, teamBPoints + 2);
        } else if (teamBStrength > teamAStrength) {
            teamAPoints = Math.max(finalSet ? 10 : 18, targetScore - random.nextInt(6));
            teamBPoints = Math.max(targetScore, teamAPoints + 2);
        } else {
            if (random.nextBoolean()) {
                teamBPoints = Math.max(finalSet ? 13 : 23, targetScore - 1);
                teamAPoints = teamBPoints + 2;
            } else {
                teamAPoints = Math.max(finalSet ? 13 : 23, targetScore - 1);
                teamBPoints = teamAPoints + 2;
            }
        }

        return new int[]{teamAPoints, teamBPoints};
    }

    private double calculateAttackPower(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) {
            lineup = team.getSquad();
        }

        double total = 0;
        for (IPlayer player : lineup) {
            total += player.getAttributes().getOrDefault("spike", 50);
            total += player.getAttributes().getOrDefault("serve", 50);
            total += player.getAttributes().getOrDefault("set", 50);
        }

        ITactic tactic = team.getTactic();
        if (tactic != null) {
            total *= tactic.getAttackModifier();
        }

        return total / Math.max(1, lineup.size());
    }

    private double calculateDefensePower(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) {
            lineup = team.getSquad();
        }

        double total = 0;
        for (IPlayer player : lineup) {
            total += player.getAttributes().getOrDefault("block", 50);
            total += player.getAttributes().getOrDefault("receive", 50);
            total += player.getAttributes().getOrDefault("stamina", 50);
        }

        ITactic tactic = team.getTactic();
        if (tactic != null) {
            total *= tactic.getDefenseModifier();
        }

        return total / Math.max(1, lineup.size());
    }

    @Override
    protected void applyInjuries() {
        applyInjuriesToTeam(homeTeam);
        applyInjuriesToTeam(awayTeam);
    }

    private void applyInjuriesToTeam(ITeam team) {
        for (IPlayer player : team.getStartingLineup()) {
            int chance = random.nextInt(100);
            if (chance < 2) {
                int games = 1 + random.nextInt(3);
                player.setInjured(games);
                fireEvent(player.getName() + " got injured for " + games + " game(s).");
            }
        }
    }
}