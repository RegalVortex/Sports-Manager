package com.sportsmanager;

import com.sportsmanager.core.*;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.setup.PresetData;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        SportRegistry registry = new SportRegistry();
        GameSetupService setupService = new GameSetupService(registry);

        String selectedSport = chooseSport(registry);
        LeaguePreset selectedLeague = chooseLeague(selectedSport);
        String selectedTeamName = chooseTeam(selectedLeague);

        GameSetupService.SetupResult setup =
                setupService.createGame(selectedSport, selectedLeague, selectedTeamName);

        ISport sport = setup.getSport();
        ILeague league = setup.getLeague();
        ITeam playerTeam = setup.getPlayerTeam();

        runDashboard(sport, league, playerTeam);
    }

    private static String chooseSport(SportRegistry registry) {
        List<String> sports = registry.getAvailableSports();

        System.out.println("=== Select Sport ===");
        for (int i = 0; i < sports.size(); i++) {
            System.out.println((i + 1) + ". " + sports.get(i));
        }

        int choice = readChoice(1, sports.size());
        return sports.get(choice - 1);
    }

    private static LeaguePreset chooseLeague(String sportName) {
        List<LeaguePreset> leagues = PresetData.getLeaguesForSport(sportName);

        System.out.println("\n=== Select League ===");
        for (int i = 0; i < leagues.size(); i++) {
            System.out.println((i + 1) + ". " + leagues.get(i).getLeagueName());
        }

        int choice = readChoice(1, leagues.size());
        return leagues.get(choice - 1);
    }

    private static String chooseTeam(LeaguePreset league) {
        List<String> teams = league.getTeamNames();

        System.out.println("\n=== Select Your Team ===");
        for (int i = 0; i < teams.size(); i++) {
            System.out.println((i + 1) + ". " + teams.get(i));
        }

        int choice = readChoice(1, teams.size());
        return teams.get(choice - 1);
    }

    private static void runDashboard(ISport sport, ILeague league, ITeam playerTeam) {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("Sports Manager M3 Dashboard");
            System.out.println("Sport: " + sport.getSportName());
            System.out.println("League: " + league.getName());
            System.out.println("Your Team: " + playerTeam.getName());
            System.out.println("Coach: " + playerTeam.getCoach().getName());
            System.out.println("Tactic: " + playerTeam.getTactic().getName());
            System.out.println("Current Week: " + league.getCurrentWeek());
            System.out.println("=================================");

            System.out.println("1. View Squad");
            System.out.println("2. View Coach");
            System.out.println("3. View Standings");
            System.out.println("4. Next Week");
            System.out.println("5. Exit");

            int choice = readChoice(1, 5);

            if (choice == 1) {
                showSquad(playerTeam);
            } else if (choice == 2) {
                showCoach(playerTeam);
            } else if (choice == 3) {
                printStandings(league);
            } else if (choice == 4) {
                playNextWeek(league);
            } else if (choice == 5) {
                running = false;
                System.out.println("Exiting Sports Manager...");
            }
        }
    }

    private static void showSquad(ITeam team) {
        System.out.println("\n=== Squad: " + team.getName() + " ===");

        for (IPlayer player : team.getSquad()) {
            System.out.println(
                    "- " + player.getName()
                            + " | " + player.getPosition()
                            + " | OVR: " + player.getOverallRating()
                            + " | Injured: " + player.isInjured()
            );
        }
    }

    private static void showCoach(ITeam team) {
        ICoach coach = team.getCoach();

        System.out.println("\n=== Coach ===");
        System.out.println("Name: " + coach.getName());
        System.out.println("Specialty: " + coach.getSpecialty());
    }

    private static void playNextWeek(ILeague league) {
        if (league.isSeasonOver()) {
            System.out.println("\nSeason is already over.");

            ITeam champion = league.getChampion();
            if (champion != null) {
                System.out.println("Champion: " + champion.getName());
            }
            return;
        }

        int week = league.getCurrentWeek();
        List<IMatch> weeklyMatches = league.getFixturesForWeek(week);

        System.out.println("\n========== WEEK " + week + " ==========");

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
    }

    private static void printStandings(ILeague league) {
        System.out.println("\n=== Standings ===");

        int rank = 1;
        for (ITeam team : league.getStandings().getTeams()) {
            System.out.println(rank + ". " + team.getName() + " - " + team.getPoints() + " pts");
            rank++;
        }
    }

    private static int readChoice(int min, int max) {
        while (true) {
            System.out.print("Select option: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();

                if (choice >= min && choice <= max) {
                    return choice;
                }
            } else {
                scanner.next();
            }

            System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }
}