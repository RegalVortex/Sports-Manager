package com.sportsmanager.core;

import java.util.List;

public interface ICoach {

    String getName();
    String getSpecialty();
    void  trainPlayers(List<IPlayer> players);

}
