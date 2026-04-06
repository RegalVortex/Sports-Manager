package com.sportsmanager.sport.football;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FootballTacticTest {

    @Test
    void shouldReturnCorrectName() {
        FootballTactic tactic = new FootballTactic("4-4-2");
        assertEquals("4-4-2", tactic.getName());
    }

    @Test
    void shouldHaveBalancedModifiersFor442() {
        FootballTactic tactic = new FootballTactic("4-4-2");
        assertEquals(1.0, tactic.getAttackModifier());
        assertEquals(1.0, tactic.getDefenseModifier());
    }

    @Test
    void shouldHaveHigherAttackFor433() {
        FootballTactic tactic = new FootballTactic("4-3-3");
        assertTrue(tactic.getAttackModifier() > 1.0);
        assertTrue(tactic.getDefenseModifier() < 1.0);
    }

    @Test
    void shouldHaveHigherDefenseFor532() {
        FootballTactic tactic = new FootballTactic("5-3-2");
        assertTrue(tactic.getDefenseModifier() > 1.0);
        assertTrue(tactic.getAttackModifier() < 1.0);
    }

    @Test
    void shouldReturnStandardFormations() {
        List<FootballTactic> tactics = FootballTactic.standardFormations();

        assertEquals(3, tactics.size());
        assertEquals("4-4-2", tactics.get(0).getName());
        assertEquals("4-3-3", tactics.get(1).getName());
        assertEquals("5-3-2", tactics.get(2).getName());
    }
}