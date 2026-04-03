package com.sportsmanager.core;

import java.util.List;

public interface ISport {

    String getSportName();

    int getTeamSize();

    int getMaxSubstitutes();

    int getNumberOfPeriods();

    String getPeriodName();

    List<String> getValidPositions();

    SportFactory createFactory();
}