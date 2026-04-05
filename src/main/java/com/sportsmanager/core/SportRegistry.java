package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SportRegistry {

    private final Map<String, SportFactory> registry;

    public SportRegistry() {
        this.registry = new HashMap<>();
    }

    public void register(String name, SportFactory factory) {
        if (name == null  name.isBlank()  factory == null) {
            return;
        }
        registry.put(name, factory);
    }

    public SportFactory getFactory(String name) {
        return registry.get(name);
    }

    public List<String> getAvailableSports() {
        return new ArrayList<>(registry.keySet());
    }
}
