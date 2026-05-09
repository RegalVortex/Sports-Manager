package com.sportsmanager.sport.volleyball;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballTacticTest {

    @Test
    void offensiveShouldHaveHighAttackModifier() {
        VolleyballTactic tactic = new VolleyballTactic("OFFENSIVE");

        assertEquals("OFFENSIVE", tactic.getName());
        assertEquals(1.15, tactic.getAttackModifier(), 0.001);
        assertEquals(0.90, tactic.getDefenseModifier(), 0.001);
    }

    @Test
    void defensiveShouldHaveHighDefenseModifier() {
        VolleyballTactic tactic = new VolleyballTactic("DEFENSIVE");

        assertEquals("DEFENSIVE", tactic.getName());
        assertEquals(0.90, tactic.getAttackModifier(), 0.001);
        assertEquals(1.15, tactic.getDefenseModifier(), 0.001);
    }

    @Test
    void balancedShouldHaveNeutralModifiers() {
        VolleyballTactic tactic = new VolleyballTactic("BALANCED");

        assertEquals("BALANCED", tactic.getName());
        assertEquals(1.00, tactic.getAttackModifier(), 0.001);
        assertEquals(1.00, tactic.getDefenseModifier(), 0.001);
    }

    @Test
    void unknownNameShouldDefaultToBalanced() {
        VolleyballTactic tactic = new VolleyballTactic("UNKNOWN");

        assertEquals(1.00, tactic.getAttackModifier(), 0.001);
        assertEquals(1.00, tactic.getDefenseModifier(), 0.001);
    }

    @Test
    void uppercaseNameShouldAlwaysWork() {
        // Tactic constructor internally uses toUpperCase(), so uppercase input is canonical.
        VolleyballTactic offensive  = new VolleyballTactic("OFFENSIVE");
        VolleyballTactic balanced   = new VolleyballTactic("BALANCED");
        VolleyballTactic defensive  = new VolleyballTactic("DEFENSIVE");

        assertEquals(1.15, offensive.getAttackModifier(),  0.001);
        assertEquals(1.00, balanced.getAttackModifier(),   0.001);
        assertEquals(0.90, defensive.getAttackModifier(),  0.001);
    }

    @Test
    void standardFormationsShouldContainThreeTactics() {
        List<VolleyballTactic> formations = VolleyballTactic.standardFormations();

        assertEquals(3, formations.size());
    }

    @Test
    void standardFormationsShouldContainExpectedNames() {
        List<VolleyballTactic> formations = VolleyballTactic.standardFormations();

        long offensiveCount = formations.stream()
                .filter(t -> t.getName().equalsIgnoreCase("OFFENSIVE")).count();
        long balancedCount = formations.stream()
                .filter(t -> t.getName().equalsIgnoreCase("BALANCED")).count();
        long defensiveCount = formations.stream()
                .filter(t -> t.getName().equalsIgnoreCase("DEFENSIVE")).count();

        assertEquals(1, offensiveCount, "Should have exactly one OFFENSIVE tactic");
        assertEquals(1, balancedCount, "Should have exactly one BALANCED tactic");
        assertEquals(1, defensiveCount, "Should have exactly one DEFENSIVE tactic");
    }

    @Test
    void toStringShouldReturnName() {
        VolleyballTactic tactic = new VolleyballTactic("BALANCED");
        assertEquals("BALANCED", tactic.toString());
    }
}
