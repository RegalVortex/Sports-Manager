package com.sportsmanager.core;

public interface IMatchEventListener {

    void onMatchEvent(String event);

    void onMatchComplete(MatchResult result);
}