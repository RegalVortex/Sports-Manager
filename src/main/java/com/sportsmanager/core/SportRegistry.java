package com.sportsmanager.core;

import com.sportsmanager.sport.football.FootballSportFactory;
import com.sportsmanager.sport.volleyball.VolleyballSportFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportRegistry {

    private final Map<String, SportFactory> registry;

    public SportRegistry() {
        this.registry = new HashMap<>();

        register("football", new FootballSportFactory());
        register("volleyball", new VolleyballSportFactory());

    }

    public void register(String name, SportFactory factory) {
        if (name == null || name.isBlank() || factory == null) {
            return;
        }
        registry.put(name, factory);
    }

    public SportFactory getFactory(String name) {
        if (name == null) return null;
        return registry.get(name.toLowerCase());
    }

    public List<String> getAvailableSports() {
        return new ArrayList<>(registry.keySet());
    }
}
