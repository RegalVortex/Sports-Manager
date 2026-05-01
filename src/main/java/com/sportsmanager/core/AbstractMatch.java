package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMatch implements IMatch {

    protected ITeam homeTeam;
    protected ITeam awayTeam;
    protected int week;
    protected MatchResult result;
    protected List<String> commentary;
    protected List<IMatchEventListener> listeners;
    protected boolean played;

    public AbstractMatch(ITeam homeTeam, ITeam awayTeam, int week) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.week = week;
        this.commentary = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.played = false;
    }

    @Override
    public ITeam getHomeTeam() {
        return homeTeam;
    }

    @Override
    public ITeam getAwayTeam() {
        return awayTeam;
    }

    public int getWeek() {
        return week;
    }

    @Override
    public MatchResult getResult() {
        return result;
    }

    @Override
    public boolean isPlayed() {
        return played;
    }

    @Override
    public List<String> getCommentary() {
        return Collections.unmodifiableList(commentary);
    }

    @Override
    public void addMatchEventListener(IMatchEventListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    @Override
    public void removeEventListener(IMatchEventListener l) {
        listeners.remove(l);
    }

    protected void fireEvent(String event) {
        commentary.add(event);
        for (IMatchEventListener listener : listeners) {
            listener.onMatchEvent(event);
        }
    }

    protected void notifyListeners() {
        if (result == null) {
            return;
        }
        for (IMatchEventListener listener : listeners) {
            listener.onMatchComplete(result);
        }
    }

    @Override
    public final void simulate() {
        if (played) {
            return;
        }

        simulateMatch();
        applyInjuries();
        played = true;
        notifyListeners();
    }

    protected abstract void simulateMatch();



    public void restoreResult(MatchResult result) {
        this.result = result;
        this.played = true;
    }

    protected abstract void applyInjuries();
}
