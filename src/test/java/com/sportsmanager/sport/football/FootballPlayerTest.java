package com.sportsmanager.sport.football;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FootballPlayerTest {

    @Test
    void shouldStoreNameAndPositionCorrectly() {
        FootballPlayer player = new FootballPlayer("Messi", "RW");

        assertEquals("Messi", player.getName());
        assertEquals("RW", player.getPosition());
    }

    @Test
    void shouldContainDefaultAttributes() {
        FootballPlayer player = new FootballPlayer("Player1", "CM");
        Map<String, Integer> attributes = player.getAttributes();

        assertEquals(6, attributes.size());
        assertTrue(attributes.containsKey("pace"));
        assertTrue(attributes.containsKey("shooting"));
        assertTrue(attributes.containsKey("passing"));
        assertTrue(attributes.containsKey("defending"));
        assertTrue(attributes.containsKey("heading"));
        assertTrue(attributes.containsKey("stamina"));
    }

    @Test
    void trainingShouldIncreaseAttribute() {
        FootballPlayer player = new FootballPlayer("Player2", "ST");

        int before = player.getAttributes().get("shooting");
        player.train("shooting", 5);
        int after = player.getAttributes().get("shooting");

        assertEquals(before + 5, after);
    }

    @Test
    void shouldHandleInjuryCorrectly() {
        FootballPlayer player = new FootballPlayer("Player3", "CB");

        player.setInjured(3);

        assertTrue(player.isInjured());
        assertEquals(3, player.getInjuryGamesRemaining());
    }

    @Test
    void decrementInjuryShouldReduceRemainingGames() {
        FootballPlayer player = new FootballPlayer("Player4", "LB");

        player.setInjured(2);
        player.decrementInjury();

        assertEquals(1, player.getInjuryGamesRemaining());
        assertTrue(player.isInjured());
    }

    @Test
    void injuryShouldEndWhenCounterReachesZero() {
        FootballPlayer player = new FootballPlayer("Player5", "GK");

        player.setInjured(1);
        player.decrementInjury();

        assertFalse(player.isInjured());
        assertEquals(0, player.getInjuryGamesRemaining());
    }

    @Test
    void generatedRandomPlayerShouldHaveCorrectNameAndPosition() {
        FootballPlayer player = FootballPlayer.generateRandom("ST", "Random Striker");

        assertEquals("Random Striker", player.getName());
        assertEquals("ST", player.getPosition());
    }

    @Test
    void overallRatingShouldBeNonNegative() {
        FootballPlayer player = new FootballPlayer("Player6", "CM");

        assertTrue(player.getOverallRating() >= 0);
    }
}
