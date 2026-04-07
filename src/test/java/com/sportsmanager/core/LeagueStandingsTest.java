package com.sportsmanager.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeagueStandingsTest {

    @Test
    void shouldReturnTeamsInGivenOrder() {
        ITeam team1 = new DummyTeam("A");
        ITeam team2 = new DummyTeam("B");

        List<ITeam> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        LeagueStandings standings = new LeagueStandings(teams);

        assertEquals(2, standings.getTeams().size());
        assertEquals(team1, standings.getTeams().get(0));
        assertEquals(team2, standings.getTeams().get(1));
    }

    @Test
    void shouldReturnCorrectRankForFirstTeam() {
        ITeam team1 = new DummyTeam("A");
        ITeam team2 = new DummyTeam("B");

        List<ITeam> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        LeagueStandings standings = new LeagueStandings(teams);

        assertEquals(1, standings.getRankOf(team1));
    }

    @Test
    void shouldReturnCorrectRankForSecondTeam() {
        ITeam team1 = new DummyTeam("A");
        ITeam team2 = new DummyTeam("B");

        List<ITeam> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        LeagueStandings standings = new LeagueStandings(teams);

        assertEquals(2, standings.getRankOf(team2));
    }

    @Test
    void shouldReturnZeroWhenTeamIsNotInStandings() {
        ITeam team1 = new DummyTeam("A");
        ITeam team2 = new DummyTeam("B");
        ITeam team3 = new DummyTeam("C");

        List<ITeam> teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);

        LeagueStandings standings = new LeagueStandings(teams);

        assertEquals(0, standings.getRankOf(team3));
    }

    static class DummyTeam implements ITeam {

        private final String name;

        DummyTeam(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<IPlayer> getSquad() {
            return new ArrayList<>();
        }

        @Override
        public List<IPlayer> getStartingLineup() {
            return new ArrayList<>();
        }

        @Override
        public ICoach getCoach() {
            return null;
        }

        @Override
        public ITactic getTactic() {
            return null;
        }

        @Override
        public void setTactic(ITactic tactic) {
        }

        @Override
        public void substitutePlayer(IPlayer out, IPlayer in) {
        }

        @Override
        public void addPoints(int points) {
        }

        @Override
        public int getPoints() {
            return 0;
        }
    }
}