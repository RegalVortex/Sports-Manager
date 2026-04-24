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
            if (match instanceof AbstractMatch) {
                AbstractMatch abstractMatch = (AbstractMatch) match;
                if (abstractMatch.getWeek() == week) {
                    weekly.add(match);
                }
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

        List<ITeam> rotationTeams = new ArrayList<>(teams);

        if (rotationTeams.size() % 2 != 0) {
            rotationTeams.add(null);
        }

        int numberOfTeams = rotationTeams.size();
        int rounds = numberOfTeams - 1;
        int matchesPerWeek = numberOfTeams / 2;

        int week = 1;


        for (int round = 0; round < rounds; round++) {
            for (int matchIndex = 0; matchIndex < matchesPerWeek; matchIndex++) {
                ITeam home = rotationTeams.get(matchIndex);
                ITeam away = rotationTeams.get(numberOfTeams - 1 - matchIndex);

                if (home != null && away != null) {
                    fixtures.add(createMatch(home, away, week));
                }
            }

            rotateTeams(rotationTeams);
            week++;
        }


        int firstHalfFixtureCount = fixtures.size();

        for (int i = 0; i < firstHalfFixtureCount; i++) {
            IMatch firstLeg = fixtures.get(i);

            ITeam home = firstLeg.getAwayTeam();
            ITeam away = firstLeg.getHomeTeam();

            int reverseWeek = getWeekOfMatch(firstLeg) + rounds;

            fixtures.add(createMatch(home, away, reverseWeek));
        }
    }

    private void rotateTeams(List<ITeam> rotationTeams) {
        if (rotationTeams.size() <= 2) {
            return;
        }

        ITeam fixedTeam = rotationTeams.get(0);
        ITeam lastTeam = rotationTeams.remove(rotationTeams.size() - 1);

        rotationTeams.add(1, lastTeam);
        rotationTeams.set(0, fixedTeam);
    }

    private int getWeekOfMatch(IMatch match) {
        if (match instanceof AbstractMatch) {
            AbstractMatch abstractMatch = (AbstractMatch) match;
            return abstractMatch.getWeek();
        }
        return 0;
    }


    protected int calculateTotalWeeks() {
        int maxWeek = 0;
        for (IMatch match : fixtures) {
            if (match instanceof AbstractMatch) {
                AbstractMatch abstractMatch = (AbstractMatch) match;
                maxWeek = Math.max(maxWeek, abstractMatch.getWeek());
            }
        }
        return maxWeek;
    }

    protected abstract IMatch createMatch(ITeam home, ITeam away, int week);

    protected abstract void applyMatchResult(IMatch match);
}
