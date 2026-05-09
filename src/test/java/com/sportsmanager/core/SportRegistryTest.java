package com.sportsmanager.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SportRegistryTest {

    @Test
    void shouldRegisterAndReturnFactory() {
        SportRegistry registry = new SportRegistry();
        DummyFactory factory = new DummyFactory();

        registry.register("Football", factory);

        assertEquals(factory, registry.getFactory("Football"));
    }

    @Test
    void shouldReturnNullForUnknownSport() {
        // "baseball" is not registered by default — should return null.
        SportRegistry registry = new SportRegistry();

        assertNull(registry.getFactory("baseball"));
    }

    @Test
    void shouldReturnAvailableSportsList() {
        // Constructor pre-registers football and volleyball.
        // Adding basketball → at least 3 sports total.
        SportRegistry registry = new SportRegistry();
        registry.register("basketball", new DummyFactory());

        List<String> sports = registry.getAvailableSports();

        assertTrue(sports.size() >= 3);
        assertTrue(sports.contains("basketball"));
        assertTrue(sports.contains("football"));
        assertTrue(sports.contains("volleyball"));
    }

    @Test
    void shouldIgnoreBlankName() {
        // Registering "" should not add a new entry.
        SportRegistry registry = new SportRegistry();
        int sizeBefore = registry.getAvailableSports().size();
        registry.register("", new DummyFactory());

        assertEquals(sizeBefore, registry.getAvailableSports().size());
    }

    @Test
    void shouldIgnoreNullFactory() {
        // Registering a null factory should leave "baseball" absent.
        SportRegistry registry = new SportRegistry();
        registry.register("baseball", null);

        assertNull(registry.getFactory("baseball"));
    }

    static class DummyFactory implements SportFactory {

        @Override
        public ISport createSport() {
            return null;
        }

        @Override
        public IPlayer createPlayer(String name, String position) {
            return null;
        }

        @Override
        public ITeam createTeam(String name, String logoPath) {
            return null;
        }

        @Override
        public IMatch createMatch(ITeam home, ITeam away, int week) {
            return null;
        }

        @Override
        public ILeague createLeague(String name, List<ITeam> teams) {
            return null;
        }

        @Override
        public ICoach createCoach(String name, String specialty) {
            return null;
        }

        @Override
        public ITactic createDefaultTactic() {
            return null;
        }

        @Override
        public List<ITactic> getAvailableTactics() {
            return new ArrayList<>();
        }
    }
}