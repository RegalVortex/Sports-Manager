package com.sportsmanager.core;

import java.util.Map;

public interface IPlayer {
    String getName();

    int getAge();

    int getPotential();

    void growOlder();

    String getPosition();

    Map<String, Integer> getAttributes();

    int getOverallRating();

    boolean isInjured();

    int getInjuryGamesRemaining();

    int getForm();

    String getFormLabel();

    void setInjured(int games);

    void decrementInjury();

    void train(String attribute, int amount);

    void setForm(int form);

    int getMatchesPlayed();
    int getWeeksInjured();
    void incrementMatchesPlayed();


}
