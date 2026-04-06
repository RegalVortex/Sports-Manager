package com.sportsmanager.sport.football;

import com.sportsmanager.core.ISport;
import com.sportsmanager.core.SportFactory;

import java.util.Arrays;
import java.util.List;

public class FootballSport implements ISport {

    @Override
    public String getSportName() {
        return "Football";
    }

    @Override
    public int getTeamSize() {
        return 11;
    }

    @Override
    public int getMaxSubstitutes() {
        return 5;
    }

    @Override
    public int getNumberOfPeriods() {
        return 2;
    }

    @Override
    public String getPeriodName() {
        return "Half";
    }

    @Override
    public List<String> getValidPositions() {
        return Arrays.asList("GK", "CB", "LB", "RB", "CDM", "CM", "CAM", "LW", "RW", "ST");
    }

    @Override
    public SportFactory createFactory() {
        return new FootballSportFactory();
    }
}