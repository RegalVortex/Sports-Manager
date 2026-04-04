package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractLeague implements ILeague {

    protected String name;
    protected List<ITeam> teams;
    protected List<IMatch> fixtures;
    protected int currentWeek;
    protected int totalWeeks;

    public AbstractLeague(String name, List<ITeam> teams) {
        this.name = name;
        this.teams = new ArrayList<>(teams);
        this.fixtures = new ArrayList<>();
        this.currentWeek = 1;
        generateFixtures();
        this.totalWeeks = calculateTotalWeeks();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ITeam> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    @Override
    public int getCurrentWeek() {
        return currentWeek;
    }

    @Override
    public List<IMatch> getAllFixtures() {
        return Collections.unmodifiableList(fixtures);
    }

    @Override
    public List<IMatch> getFixturesForWeek(int week) {
        List<IMatch> weekly = new ArrayList<>();
        for (IMatch match : fixtures) {
            if (match instanceof AbstractMatch abstractMatch && abstractMatch.getWeek() == week) {
                weekly.add(match);
            }
        }
        return weekly;
    }

    @Override
    public void advanceWeek() {
        if (isSeasonOver()) {
            return;
        }

        for (ITeam team : teams) {
            ICoach coach = team.getCoach();
            if (coach != null) {
                coach.trainPlayers(team.getSquad());
            }
        }

        List<IMatch> weeklyMatches = getFixturesForWeek(currentWeek);
        for (IMatch match : weeklyMatches) {
            match.simulate();
            applyMatchResult(match);
        }

        for (ITeam team : teams) {
            for (IPlayer player : team.getSquad()) {
                player.decrementInjury();
            }
        }

        currentWeek++;
    }

    @Override
    public boolean isSeasonOver() {
        return currentWeek > totalWeeks;
    }

    protected void generateFixtures() {
        fixtures.clear();

        int week = 1;

        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                ITeam home = teams.get(i);
                ITeam away = teams.get(j);

                fixtures.add(createMatch(home, away, week++));
                fixtures.add(createMatch(away, home, week++));
            }
        }
    }

    protected int calculateTotalWeeks() {
        int maxWeek = 0;
        for (IMatch match : fixtures) {
            if (match instanceof AbstractMatch abstractMatch) {
                maxWeek = Math.max(maxWeek, abstractMatch.getWeek());
            }
        }
        return maxWeek;
    }

    protected abstract IMatch createMatch(ITeam home, ITeam away, int week);

    protected abstract void applyMatchResult(IMatch match);
}
