package com.sportsmanager.sport.football;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;

import java.util.List;
import java.util.Random;

public class FootballMatch extends AbstractMatch {

    private final Random random = new Random();

    public FootballMatch(ITeam homeTeam, ITeam awayTeam, int week) {
        super(homeTeam, awayTeam, week);
    }

    @Override
    protected void simulateMatch() {
        int homeScore = simulateTeamScore(homeTeam, awayTeam);
        int awayScore = simulateTeamScore(awayTeam, homeTeam);

        result = new MatchResult(homeTeam, awayTeam, homeScore, awayScore);

        fireEvent("Kick-off: " + homeTeam.getName() + " vs " + awayTeam.getName());
        fireEvent("Full Time: " + result.toString());
    }

    private int simulateTeamScore(ITeam attackingTeam, ITeam defendingTeam) {
        double attackPower = calculateAttackPower(attackingTeam);
        double defensePower = calculateDefensePower(defendingTeam);

        double raw = (attackPower / defensePower) * 2.0;
        int score = (int) Math.round(raw + random.nextDouble());

        if (score < 0) {
            score = 0;
        }
        if (score > 6) {
            score = 6;
        }

        return score;
    }

    private double calculateAttackPower(ITeam team) {
        List<IPlayer> lineup = team.getStartingLineup();
        if (lineup.isEmpty()) {
            lineup = team.getSquad();
        }

        double total = 0;
        for (IPlayer player : lineup) {
            total += player.getAttributes().getOrDefault("shooting", 50);
            total += player.getAttributes().getOrDefault("pace", 50);
            total += player.getAttributes().getOrDefault("passing", 50);
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
            total += player.getAttributes().getOrDefault("defending", 50);
            total += player.getAttributes().getOrDefault("heading", 50);
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
            if (chance < 3) {
                int games = 1 + random.nextInt(4);
                player.setInjured(games);
                fireEvent(player.getName() + " got injured for " + games + " game(s).");
            }
        }
    }
}