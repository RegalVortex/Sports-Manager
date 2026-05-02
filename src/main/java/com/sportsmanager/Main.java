package com.sportsmanager;

import com.sportsmanager.core.*;
import com.sportsmanager.save.LoadedGame;
import com.sportsmanager.save.SaveLoadService;
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
        SportFactory factory = registry.getFactory(selectedSport);
        runDashboard(registry, factory, sport, league, playerTeam);
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

    private static void runDashboard(SportRegistry registry, SportFactory factory, ISport sport, ILeague league, ITeam playerTeam) {
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
            System.out.println("5. Change Tactic");
            System.out.println("6. Manage Lineup");
            System.out.println("7. Save Game");
            System.out.println("8. Load Game");
            System.out.println("9. Exit");

            int choice = readChoice(1, 9);

            if (choice == 1) {
                showSquad(playerTeam);

            } else if (choice == 2) {
                showCoach(playerTeam);

            } else if (choice == 3) {
                printStandings(league);

            } else if (choice == 4) {
                playNextWeek(league, playerTeam);

            }else if (choice == 5) {
                changeTactic(factory, playerTeam);

            }  else if (choice == 6) {
                manageLineup(playerTeam);

            }else if (choice == 7) {
                SaveLoadService.saveGame("savegame.dat", sport, league, playerTeam);

            } else if (choice == 8) {
                LoadedGame loadedGame = SaveLoadService.loadGame("savegame.dat", registry);

                if (loadedGame != null) {
                    sport = loadedGame.getSport();
                    league = loadedGame.getLeague();
                    playerTeam = loadedGame.getPlayerTeam();

                    System.out.println("Game loaded successfully.");
                }

            } else if (choice == 9) {
                running = false;
                System.out.println("Exiting Sports Manager...");
            }
        }
    }

    private static void changeTactic(SportFactory factory, ITeam team) {
        List<ITactic> tactics = factory.getAvailableTactics();

        System.out.println("\n=== Change Tactic ===");
        System.out.println("1. Defensive");
        System.out.println("2. Balanced");
        System.out.println("3. Aggressive");

        int choice = readChoice(1, 3);

        ITactic selectedTactic = null;

        if (choice == 1) {
            selectedTactic = findTactic(tactics, "5-3-2", "DEFENSIVE");
        } else if (choice == 2) {
            selectedTactic = findTactic(tactics, "4-4-2", "BALANCED");
        } else if (choice == 3) {
            selectedTactic = findTactic(tactics, "4-3-3", "OFFENSIVE");
        }

        if (selectedTactic != null) {
            team.setTactic(selectedTactic);
            System.out.println("Tactic changed to: " + selectedTactic.getName());
        } else {
            System.out.println("Tactic could not be changed.");
        }
    }

    private static ITactic findTactic(List<ITactic> tactics, String... possibleNames) {
        for (ITactic tactic : tactics) {
            for (String name : possibleNames) {
                if (tactic.getName().equalsIgnoreCase(name)) {
                    return tactic;
                }
            }
        }
        return null;
    }
    private static void showSquad(ITeam team) {
        System.out.println("\n=== Squad: " + team.getName() + " ===");

        for (IPlayer player : team.getSquad()) {
            System.out.println(
                    "- " + player.getName()
                            + " | " + player.getPosition()
                            + " | OVR: " + player.getOverallRating()
                            + " | Form: " + player.getFormLabel()
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

    private static void playNextWeek(ILeague league, ITeam playerTeam) {
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

        int injuredBefore = countTotalInjuredPlayers(league);

        System.out.println("\n========== WEEK " + week + " ==========");

        showMatchPreview(weeklyMatches, playerTeam);

        league.advanceWeek();

        MatchResult playerTeamResult = null;

        for (IMatch match : weeklyMatches) {
            MatchResult result = match.getResult();

            if (result != null) {
                System.out.println(result);

                if (result.getHomeTeam().equals(playerTeam) || result.getAwayTeam().equals(playerTeam)) {
                    playerTeamResult = result;
                }
            }

            for (String event : match.getCommentary()) {
                System.out.println("- " + event);
            }

            System.out.println();
        }

        printStandings(league);

        int injuredAfter = countTotalInjuredPlayers(league);
        int newInjuries = Math.max(0, injuredAfter - injuredBefore);

        showWeeklySummary(league, playerTeam, playerTeamResult, newInjuries);
    }

    private static int countTotalInjuredPlayers(ILeague league) {
        int count = 0;

        for (ITeam team : league.getTeams()) {
            count += countInjuredPlayers(team);
        }

        return count;
    }
    private static void showWeeklySummary(ILeague league, ITeam playerTeam, MatchResult playerTeamResult, int newInjuries) {
        System.out.println("\n=== Weekly Summary ===");

        if (playerTeamResult != null) {
            System.out.println("Your Result: " + playerTeamResult);
        } else {
            System.out.println("Your Team did not play this week.");
        }

        int rank = league.getStandings().getRankOf(playerTeam);

        System.out.println("League Position: " + rank);
        System.out.println("New Injuries This Week: " + newInjuries);
        System.out.println("Next Week: " + league.getCurrentWeek());
        System.out.println("======================");
    }
    private static void showMatchPreview(List<IMatch> weeklyMatches, ITeam playerTeam) {
        for (IMatch match : weeklyMatches) {
            boolean playerTeamIsHome = match.getHomeTeam().equals(playerTeam);
            boolean playerTeamIsAway = match.getAwayTeam().equals(playerTeam);

            if (playerTeamIsHome || playerTeamIsAway) {
                ITeam opponent = playerTeamIsHome ? match.getAwayTeam() : match.getHomeTeam();

                System.out.println("\n--- Match Preview ---");
                System.out.println("Your Team: " + playerTeam.getName());
                System.out.println("Opponent: " + opponent.getName());
                System.out.println("Opponent Points: " + opponent.getPoints());
                System.out.println("Your Tactic: " + playerTeam.getTactic().getName());
                System.out.println("Your Injured Players: " + countInjuredPlayers(playerTeam));
                System.out.println("Opponent Injured Players: " + countInjuredPlayers(opponent));
                System.out.println("---------------------\n");

                return;
            }
        }

        System.out.println("\nNo match for your team this week.\n");
    }

    private static int countInjuredPlayers(ITeam team) {
        int count = 0;

        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                count++;
            }
        }

        return count;
    }

    private static void manageLineup(ITeam team) {
        System.out.println("\n=== Manage Lineup ===");

        List<IPlayer> lineup = team.getStartingLineup();
        List<IPlayer> squad = team.getSquad();

        System.out.println("\nStarting Lineup:");
        for (int i = 0; i < lineup.size(); i++) {
            IPlayer player = lineup.get(i);
            System.out.println((i + 1) + ". " + player.getName()
                    + " | " + player.getPosition()
                    + " | OVR: " + player.getOverallRating()
                    + " | Injured: " + player.isInjured());
        }

        List<IPlayer> bench = new java.util.ArrayList<>();

        for (IPlayer player : squad) {
            if (!lineup.contains(player)) {
                bench.add(player);
            }
        }

        System.out.println("\nBench:");
        for (int i = 0; i < bench.size(); i++) {
            IPlayer player = bench.get(i);
            System.out.println((i + 1) + ". " + player.getName()
                    + " | " + player.getPosition()
                    + " | OVR: " + player.getOverallRating()
                    + " | Injured: " + player.isInjured());
        }

        if (bench.isEmpty()) {
            System.out.println("No bench players available.");
            return;
        }

        System.out.println("\nSelect player OUT from starting lineup:");
        int outChoice = readChoice(1, lineup.size());

        System.out.println("Select player IN from bench:");
        int inChoice = readChoice(1, bench.size());

        IPlayer outPlayer = lineup.get(outChoice - 1);
        IPlayer inPlayer = bench.get(inChoice - 1);

        String beforeOutName = outPlayer.getName();

        team.substitutePlayer(outPlayer, inPlayer);

        if (team.getStartingLineup().contains(inPlayer) && !team.getStartingLineup().contains(outPlayer)) {
            System.out.println("Substitution successful: " + beforeOutName + " OUT, " + inPlayer.getName() + " IN");
        } else {
            System.out.println("Substitution failed. The lineup rules may have been violated.");
        }
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
