package com.sportsmanager.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SavedPlayer implements Serializable {

    private String name;
    private String position;
    private Map<String, Integer> attributes;
    private boolean injured;
    private int injuryGamesRemaining;
    private int form;
    private int age;
    private int potential;
    private int matchesPlayed;
    private int weeksInjured;

    public SavedPlayer(String name,
                       String position,
                       Map<String, Integer> attributes,
                       boolean injured,
                       int injuryGamesRemaining,
                       int form,
                       int age,
                       int potential,
                       int matchesPlayed,
                       int weeksInjured) {
        this.name = name;
        this.position = position;
        this.attributes = new HashMap<>(attributes);
        this.injured = injured;
        this.injuryGamesRemaining = injuryGamesRemaining;
        this.form = form;
        this.age = age;
        this.potential = potential;
        this.matchesPlayed = matchesPlayed;
        this.weeksInjured = weeksInjured;
    }

    public String getName()                  { return name; }
    public String getPosition()              { return position; }
    public Map<String, Integer> getAttributes() { return attributes; }
    public boolean isInjured()               { return injured; }
    public int getInjuryGamesRemaining()     { return injuryGamesRemaining; }
    public int getForm()                     { return form; }
    public int getAge()                      { return age; }
    public int getPotential()                { return potential; }
    public int getMatchesPlayed()            { return matchesPlayed; }
    public int getWeeksInjured()             { return weeksInjured; }
}
