package com.sportsmanager.core;

import java.util.List;

public interface ICoach {

    String getName();
    String getSpecialty();
    int getQuality();
    void  trainPlayers(List<IPlayer> players);

}
