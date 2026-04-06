package com.sportsmanager;

import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.sport.football.FootballSport;

public class Main {
    public static void main(String[] args) {
        FootballSport footballSport = new FootballSport();
        SportFactory factory = footballSport.createFactory();

        ITeam teamA = factory.createTeam("GS", "logoA.png");
        ITeam teamB = factory.createTeam("FB", "logoB.png");

        IMatch match = factory.createMatch(teamA, teamB, 1);
        match.simulate();

        MatchResult result = match.getResult();

        System.out.println("Sports Manager M2 running...");
        System.out.println("Sport: " + footballSport.getSportName());
        System.out.println("Match Result: " + result);

        System.out.println();
        System.out.println("Match Commentary:");
        for (String event : match.getCommentary()) {
            System.out.println("- " + event);
        }
    }
}