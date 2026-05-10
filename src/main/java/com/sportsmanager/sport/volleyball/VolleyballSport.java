package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.ISport;
import com.sportsmanager.core.SportFactory;

import java.util.Arrays;
import java.util.List;

public class VolleyballSport implements ISport {

    @Override
    public String getSportName() {
        return "Volleyball";
    }

    @Override
    public int getTeamSize() {
        return 6;
    }

    @Override
    public int getMaxSubstitutes() {
        return 6;
    }

    @Override
    public int getNumberOfPeriods() {
        return 5;
    }

    @Override
    public String getPeriodName() {
        return "Set";
    }

    @Override
    public List<String> getValidPositions() {
        return Arrays.asList(
                "SETTER",
                "OUTSIDE_HITTER",
                "MIDDLE_BLOCKER",
                "OPPOSITE",
                "LIBERO"
        );
    }

    @Override
    public SportFactory createFactory() {
        return new VolleyballSportFactory();
    }
}