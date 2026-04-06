package com.sportsmanager.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchResultTest {

    @Test
    void shouldStoreScoresCorrectly() {
        ITeam home = new DummyTeam("Home FC");
        ITeam away = new DummyTeam("Away FC");

        MatchResult result = new MatchResult(home, away, 3, 2);

        assertEquals(3, result.getHomeScore());
        assertEquals(2, result.getAwayScore());
        assertEquals(home, result.getHomeTeam());
        assertEquals(away, result.getAwayTeam());
    }

    @Test
    void shouldReturnCorrectWinner() {
        ITeam home = new DummyTeam("Home FC");
        ITeam away = new DummyTeam("Away FC");

        MatchResult result = new MatchResult(home, away, 2, 1);

        assertEquals(home, result.getWinner());
    }

    @Test
    void shouldReturnNullWhenDraw() {
        ITeam home = new DummyTeam("Home FC");
        ITeam away = new DummyTeam("Away FC");

        MatchResult result = new MatchResult(home, away, 1, 1);

        assertNull(result.getWinner());
    }

    @Test
    void shouldFormatToStringCorrectly() {
        ITeam home = new DummyTeam("Galactic FC");
        ITeam away = new DummyTeam("Thunder United");

        MatchResult result = new MatchResult(home, away, 4, 3);

        assertEquals("Galactic FC 4 - 3 Thunder United", result.toString());
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