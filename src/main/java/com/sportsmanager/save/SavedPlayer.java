package com.sportsmanager.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SavedPlayer implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private int goalsScored;
    private boolean inStartingLineup;
    private int startingLineupIndex;

    public SavedPlayer(String name,
                       String position,
                       Map<String, Integer> attributes,
                       boolean injured,
                       int injuryGamesRemaining,
                       int form,
                       int age,
                       int potential,
                       int matchesPlayed,
                       int weeksInjured,
                       int goalsScored,
                       boolean inStartingLineup,
                       int startingLineupIndex) {
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
        this.goalsScored = goalsScored;
        this.inStartingLineup = inStartingLineup;
        this.startingLineupIndex = startingLineupIndex;
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
    public int getGoalsScored()              { return goalsScored; }
    public boolean isInStartingLineup()      { return inStartingLineup; }
    public int getStartingLineupIndex()      { return startingLineupIndex; }
}
