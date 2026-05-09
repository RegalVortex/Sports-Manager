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
        // AbstractPlayer.train() adds a potential bonus for young players (age<=23),
        // so the increase may be >= 5, not exactly 5. We assert at least +5.
        FootballPlayer player = new FootballPlayer("Player2", "ST");

        int before = player.getAttributes().get("shooting");
        player.train("shooting", 5);
        int after = player.getAttributes().get("shooting");

        assertTrue(after >= before + 5, "Shooting should increase by at least 5");
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
    void generatedRandomPlayerShouldHaveCorrectPosition() {
        // İsim artık rastgele üretiliyor; yalnızca pozisyon ve boş-olmayan isim doğrulanır.
        FootballPlayer player = FootballPlayer.generateRandom("ST");

        assertEquals("ST", player.getPosition());
        assertNotNull(player.getName());
        assertFalse(player.getName().isBlank());
    }

    @Test
    void generatedRandomPlayerShouldHavePositionSpecificAttributes() {
        // Kural: GK defending min 70, ST defending max 52 — üretilen değerler aralıkta olmalı.
        FootballPlayer gk = FootballPlayer.generateRandom("GK");
        FootballPlayer st = FootballPlayer.generateRandom("ST");

        // Her nitelik 1-99 arasında olmalı
        for (int v : gk.getAttributes().values()) {
            assertTrue(v >= 1 && v <= 99, "GK attribute out of range: " + v);
        }
        for (int v : st.getAttributes().values()) {
            assertTrue(v >= 1 && v <= 99, "ST attribute out of range: " + v);
        }

        // GK'nın min defending aralığı (70) ST'nin max aralığından (52) yüksek olmalı
        assertTrue(gk.getAttributes().get("defending") >= 70);
        assertTrue(st.getAttributes().get("defending") <= 52);
    }

    @Test
    void overallRatingShouldBeNonNegative() {
        FootballPlayer player = new FootballPlayer("Player6", "CM");

        assertTrue(player.getOverallRating() >= 0);
    }
}
