package com.sportsmanager.sport.volleyball;

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

public class VolleyballSportFactory implements SportFactory {

    @Override
    public ISport createSport() {
        return new VolleyballSport();
    }

    @Override
    public IPlayer createPlayer(String name, String position) {
        return VolleyballPlayer.generateRandom(position, name);
    }

    @Override
    public ITeam createTeam(String name, String logoPath) {
        return VolleyballTeam.generateRandom(name, logoPath);
    }

    @Override
    public IMatch createMatch(ITeam home, ITeam away, int week) {
        return new VolleyballMatch(home, away, week);
    }

    @Override
    public ILeague createLeague(String name, List<ITeam> teams) {
        return new VolleyballLeague(name, teams);
    }

    @Override
    public ICoach createCoach(String name, String specialty) {
        return new VolleyballCoach(name, specialty);
    }

    @Override
    public ITactic createDefaultTactic() {
        return new VolleyballTactic("BALANCED");
    }

    @Override
    public List<ITactic> getAvailableTactics() {
        return new ArrayList<ITactic>(VolleyballTactic.standardFormations());
    }
}