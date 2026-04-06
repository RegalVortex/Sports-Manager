package com.sportsmanager.sport.football;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;

import java.util.ArrayList;
import java.util.List;

public class FootballSportFactory implements SportFactory {

    @Override
    public ISport createSport() {
        return new FootballSport();
    }

    @Override
    public IPlayer createPlayer(String name, String position) {
        return FootballPlayer.generateRandom(position, name);
    }

    @Override
    public ITeam createTeam(String name, String logoPath) {
        return FootballTeam.generateRandom(name, logoPath);
    }

    @Override
    public IMatch createMatch(ITeam home, ITeam away, int week) {
        return new FootballMatch(home, away, week);
    }

    @Override
    public ILeague createLeague(String name, List<ITeam> teams) {
        return new FootballLeague(name, teams);
    }

    @Override
    public ICoach createCoach(String name, String specialty) {
        return new FootballCoach(name, specialty);
    }

    @Override
    public ITactic createDefaultTactic() {
        return new FootballTactic("4-4-2");
    }

    @Override
    public List<ITactic> getAvailableTactics() {
        return new ArrayList<ITactic>(FootballTactic.standardFormations());
    }
}