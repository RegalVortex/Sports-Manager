package com.sportsmanager;

import com.sportsmanager.core.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        SportRegistry registry = new SportRegistry();

        // Burayı "football" veya "volleyball" yaparak test ediyoruz bu aşamada!!!!!
        String selectedSport = "volleyball";

        SportFactory factory = registry.getFactory(selectedSport);

        if (factory == null) {
            System.out.println("Invalid sport selected: " + selectedSport);
            return;
        }

        ISport sport = factory.createSport();

        List<ITeam> teams = new ArrayList<>();
        teams.add(factory.createTeam("Team A", "logoA.png"));
        teams.add(factory.createTeam("Team B", "logoB.png"));
        teams.add(factory.createTeam("Team C", "logoC.png"));
        teams.add(factory.createTeam("Team D", "logoD.png"));

        ILeague league = factory.createLeague(sport.getSportName() + " League", teams);

        System.out.println("=================================");
        System.out.println("Sports Manager M3 League Simulation");
        System.out.println("Sport: " + sport.getSportName());
        System.out.println("League: " + league.getName());
        System.out.println("Teams: " + league.getTeams().size());
        System.out.println("=================================");
        System.out.println();

        while (!league.isSeasonOver()) {
            int week = league.getCurrentWeek();

            System.out.println("===== Week " + week + " =====");

            List<IMatch> weeklyMatches = league.getFixturesForWeek(week);

            league.advanceWeek();

            for (IMatch match : weeklyMatches) {
                MatchResult result = match.getResult();

                if (result != null) {
                    System.out.println(result);
                }

                for (String event : match.getCommentary()) {
                    System.out.println("- " + event);
                }

                System.out.println();
            }

            printStandings(league);
            System.out.println();
        }

        System.out.println("===== Season Finished =====");

        ITeam champion = league.getChampion();
        if (champion != null) {
            System.out.println("Champion: " + champion.getName());
        }
    }

    private static void printStandings(ILeague league) {
        System.out.println("Current Standings:");

        int rank = 1;
        for (ITeam team : league.getStandings().getTeams()) {
            System.out.println(rank + ". " + team.getName() + " - " + team.getPoints() + " pts");
            rank++;
        }
    }
}