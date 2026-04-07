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
        SportRegistry registry = new SportRegistry();

        assertNull(registry.getFactory("Volleyball"));
    }

    @Test
    void shouldReturnAvailableSportsList() {
        SportRegistry registry = new SportRegistry();
        registry.register("Football", new DummyFactory());
        registry.register("Basketball", new DummyFactory());

        List<String> sports = registry.getAvailableSports();

        assertEquals(2, sports.size());
        assertTrue(sports.contains("Football"));
        assertTrue(sports.contains("Basketball"));
    }

    @Test
    void shouldIgnoreBlankName() {
        SportRegistry registry = new SportRegistry();
        registry.register("", new DummyFactory());

        assertTrue(registry.getAvailableSports().isEmpty());
    }

    @Test
    void shouldIgnoreNullFactory() {
        SportRegistry registry = new SportRegistry();
        registry.register("Football", null);

        assertNull(registry.getFactory("Football"));
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