package com.sportsmanager.core;

import java.util.Map;

public interface IPlayer {
    String getName();

    String getPosition();

    Map<String, Integer> getAttributes();

    int getOverallRating();

    boolean isInjured();

    int getInjuryGamesRemaining();

    void setInjured(int games);

    void decrementInjury();

    void train(String attribute, int amount);
}
