package com.sportsmanager.sport.volleyball;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballPlayerTest {

    @Test
    void shouldStoreNameAndPositionCorrectly() {
        VolleyballPlayer player = new VolleyballPlayer("Hande Baladın", "SETTER");

        assertEquals("Hande Baladın", player.getName());
        assertEquals("SETTER", player.getPosition());
    }

    @Test
    void shouldContainDefaultAttributes() {
        VolleyballPlayer player = new VolleyballPlayer("Test", "LIBERO");
        Map<String, Integer> attributes = player.getAttributes();

        assertEquals(6, attributes.size());
        assertTrue(attributes.containsKey("serve"));
        assertTrue(attributes.containsKey("spike"));
        assertTrue(attributes.containsKey("block"));
        assertTrue(attributes.containsKey("receive"));
        assertTrue(attributes.containsKey("set"));
        assertTrue(attributes.containsKey("stamina"));
    }

    @Test
    void trainingShouldIncreaseAttribute() {
        // AbstractPlayer.train() adds a potential bonus for young players (age<=23),
        // so the increase may be >= 5, not exactly 5. We assert at least +5.
        VolleyballPlayer player = new VolleyballPlayer("Test", "SETTER");

        int before = player.getAttributes().get("spike");
        player.train("spike", 5);
        int after = player.getAttributes().get("spike");

        assertTrue(after >= before + 5, "Spike should increase by at least 5");
    }

    @Test
    void shouldHandleInjuryCorrectly() {
        VolleyballPlayer player = new VolleyballPlayer("Test", "LIBERO");

        player.setInjured(4);

        assertTrue(player.isInjured());
        assertEquals(4, player.getInjuryGamesRemaining());
    }

    @Test
    void decrementInjuryShouldReduceRemainingGames() {
        VolleyballPlayer player = new VolleyballPlayer("Test", "OUTSIDE_HITTER");

        player.setInjured(3);
        player.decrementInjury();

        assertEquals(2, player.getInjuryGamesRemaining());
        assertTrue(player.isInjured());
    }

    @Test
    void injuryShouldEndWhenCounterReachesZero() {
        VolleyballPlayer player = new VolleyballPlayer("Test", "MIDDLE_BLOCKER");

        player.setInjured(1);
        player.decrementInjury();

        assertFalse(player.isInjured());
        assertEquals(0, player.getInjuryGamesRemaining());
    }

    @Test
    void generatedRandomPlayerShouldHaveCorrectPosition() {
        VolleyballPlayer player = VolleyballPlayer.generateRandom("SETTER");

        assertEquals("SETTER", player.getPosition());
        assertNotNull(player.getName());
        assertFalse(player.getName().isBlank());
    }

    @Test
    void setterShouldHaveHighSetAttribute() {
        // SETTER: set aralığı 75-92; LIBERO: spike aralığı 30-48 — örtüşmez.
        VolleyballPlayer setter = VolleyballPlayer.generateRandom("SETTER");
        VolleyballPlayer libero = VolleyballPlayer.generateRandom("LIBERO");

        // Her nitelik geçerli aralıkta olmalı
        for (int v : setter.getAttributes().values()) {
            assertTrue(v >= 1 && v <= 99, "SETTER attribute out of range: " + v);
        }
        for (int v : libero.getAttributes().values()) {
            assertTrue(v >= 1 && v <= 99, "LIBERO attribute out of range: " + v);
        }

        // SETTER set >= 75, LIBERO spike <= 48 — ayrışık aralıklar
        assertTrue(setter.getAttributes().get("set") >= 75,
                "SETTER set should be >= 75");
        assertTrue(libero.getAttributes().get("spike") <= 48,
                "LIBERO spike should be <= 48");
    }

    @Test
    void liberoShouldHaveHighReceiveAttribute() {
        // LIBERO: receive aralığı 75-92
        VolleyballPlayer libero = VolleyballPlayer.generateRandom("LIBERO");
        assertTrue(libero.getAttributes().get("receive") >= 75,
                "LIBERO receive should be >= 75");
    }

    @Test
    void middleBlockerShouldHaveHighBlockAttribute() {
        // MIDDLE_BLOCKER: block aralığı 72-90
        VolleyballPlayer mb = VolleyballPlayer.generateRandom("MIDDLE_BLOCKER");
        assertTrue(mb.getAttributes().get("block") >= 72,
                "MIDDLE_BLOCKER block should be >= 72");
    }

    @Test
    void overallRatingShouldBeNonNegative() {
        VolleyballPlayer player = new VolleyballPlayer("Test", "OPPOSITE");
        assertTrue(player.getOverallRating() >= 0);
    }
}
