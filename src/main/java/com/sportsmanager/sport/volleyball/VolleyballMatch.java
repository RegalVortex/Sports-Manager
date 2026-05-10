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

        fireEvent("Mac basladi: " + homeTeam.getName() + " vs " + awayTeam.getName());

        while (homeSets < 3 && awaySets < 3) {
            int[] setScore = simulateSet(homeTeam, awayTeam, homeSets == 2 && awaySets == 2);

            int homePoints = setScore[0];
            int awayPoints = setScore[1];

            if (homePoints > awayPoints) {
                homeSets++;
            } else {
                awaySets++;
            }

            fireEvent(setNumber + ". set: " + homeTeam.getName() + " " + homePoints +
                    " - " + awayPoints + " " + awayTeam.getName());

            setNumber++;
        }

        result = new MatchResult(homeTeam, awayTeam, homeSets, awaySets);
        fireEvent("Mac bitti: " + result.toString());
    }



    private int[] simulateSet(ITeam teamA, ITeam teamB, boolean finalSet) {
        double teamAStrength = calculateAttackPower(teamA) + calculateDefensePower(teamA);
        double teamBStrength = calculateAttackPower(teamB) + calculateDefensePower(teamB);

        teamAStrength *= 1.05;
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
            double formMult = getFormMultiplier(player);
            total += player.getAttributes().getOrDefault("spike", 50) * formMult;
            total += player.getAttributes().getOrDefault("serve", 50) * formMult;
            total += player.getAttributes().getOrDefault("set", 50) * formMult;
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
            double formMult = getFormMultiplier(player);
            total += player.getAttributes().getOrDefault("block", 50) * formMult;
            total += player.getAttributes().getOrDefault("receive", 50) * formMult;
            total += player.getAttributes().getOrDefault("stamina", 50) * formMult;
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
                fireEvent(player.getName() + " " + games + " maclig sakatlandi.");
            }
        }
    }

    private double getFormMultiplier(IPlayer player) {
        switch (player.getForm()) {
            case 0: return 0.85;  // Bad
            case 2: return 1.10;  // Good
            case 3: return 1.20;  // Excellent
            default: return 1.00; // Normal
        }
    }
}
