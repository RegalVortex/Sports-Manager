package com.sportsmanager.save;

import com.sportsmanager.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveLoadService {

    public static void saveGame(String filePath, ISport sport, ILeague league, ITeam playerTeam) {
        GameSaveData data = createSaveData(sport, league, playerTeam);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(data);
            System.out.println("Game saved successfully: " + filePath);
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public static LoadedGame loadGame(String filePath, SportRegistry registry) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            GameSaveData data = (GameSaveData) in.readObject();
            return rebuildGame(data, registry);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load failed: " + e.getMessage());
            return null;
        }
    }

    private static GameSaveData createSaveData(ISport sport, ILeague league, ITeam playerTeam) {
        List<SavedTeam> savedTeams = new ArrayList<>();

        for (ITeam team : league.getTeams()) {
            List<SavedPlayer> savedPlayers = new ArrayList<>();

            for (IPlayer player : team.getSquad()) {
                SavedPlayer savedPlayer = new SavedPlayer(
                        player.getName(),
                        player.getPosition(),
                        player.getAttributes(),
                        player.isInjured(),
                        player.getInjuryGamesRemaining()
                );
                savedPlayers.add(savedPlayer);
            }

            SavedTeam savedTeam = new SavedTeam(
                    team.getName(),
                    team.getPoints(),
                    team.getCoach() != null ? team.getCoach().getName() : "",
                    team.getCoach() != null ? team.getCoach().getSpecialty() : "",
                    team.getTactic() != null ? team.getTactic().getName() : "",
                    savedPlayers
            );

            savedTeams.add(savedTeam);
        }

        List<SavedMatch> savedMatches = new ArrayList<>();

        for (IMatch match : league.getAllFixtures()) {
            int week = 0;
            if (match instanceof AbstractMatch) {
                week = ((AbstractMatch) match).getWeek();
            }

            MatchResult result = match.getResult();

            SavedMatch savedMatch = new SavedMatch(
                    week,
                    match.getHomeTeam().getName(),
                    match.getAwayTeam().getName(),
                    result != null ? result.getHomeScore() : 0,
                    result != null ? result.getAwayScore() : 0,
                    match.isPlayed()
            );

            savedMatches.add(savedMatch);
        }

        return new GameSaveData(
                sport.getSportName(),
                league.getName(),
                playerTeam.getName(),
                league.getCurrentWeek(),
                savedTeams,
                savedMatches
        );
    }

    private static LoadedGame rebuildGame(GameSaveData data, SportRegistry registry) {
        SportFactory factory = registry.getFactory(data.getSportName());

        if (factory == null) {
            throw new IllegalArgumentException("Unknown sport in save file: " + data.getSportName());
        }

        ISport sport = factory.createSport();

        List<ITeam> teams = new ArrayList<>();
        ITeam playerTeam = null;

        for (SavedTeam savedTeam : data.getTeams()) {
            ITeam team = factory.createTeam(savedTeam.getName(), savedTeam.getName() + ".png");

            if (team instanceof AbstractTeam) {
                AbstractTeam abstractTeam = (AbstractTeam) team;

                abstractTeam.clearSquad();

                for (SavedPlayer savedPlayer : savedTeam.getPlayers()) {
                    IPlayer player = factory.createPlayer(savedPlayer.getName(), savedPlayer.getPosition());

                    player.getAttributes().clear();

                    for (Map.Entry<String, Integer> entry : savedPlayer.getAttributes().entrySet()) {
                        player.getAttributes().put(entry.getKey(), entry.getValue());
                    }

                    if (savedPlayer.isInjured()) {
                        player.setInjured(savedPlayer.getInjuryGamesRemaining());
                    } else {
                        player.setInjured(0);
                    }

                    abstractTeam.addPlayerToSquad(player);
                }

                List<IPlayer> lineup = new ArrayList<>();
                int teamSize = sport.getTeamSize();

                for (IPlayer player : abstractTeam.getSquad()) {
                    if (lineup.size() >= teamSize) {
                        break;
                    }
                    if (!player.isInjured()) {
                        lineup.add(player);
                    }
                }

                abstractTeam.setStartingLineup(lineup);
                abstractTeam.setPoints(savedTeam.getPoints());
                abstractTeam.setCoach(factory.createCoach(savedTeam.getCoachName(), savedTeam.getCoachSpecialty()));
                abstractTeam.setTactic(factory.createDefaultTactic());
            }

            teams.add(team);

            if (savedTeam.getName().equalsIgnoreCase(data.getPlayerTeamName())) {
                playerTeam = team;
            }
        }

        ILeague league = factory.createLeague(data.getLeagueName(), teams);

        if (league instanceof AbstractLeague) {
            ((AbstractLeague) league).setCurrentWeek(data.getCurrentWeek());
        }

        restoreMatches(data, league);

        return new LoadedGame(sport, league, playerTeam);
    }

    private static void restoreMatches(GameSaveData data, ILeague league) {
        for (SavedMatch savedMatch : data.getMatches()) {
            if (!savedMatch.isPlayed()) {
                continue;
            }

            for (IMatch match : league.getAllFixtures()) {
                boolean sameWeek = false;

                if (match instanceof AbstractMatch) {
                    sameWeek = ((AbstractMatch) match).getWeek() == savedMatch.getWeek();
                }

                boolean sameTeams =
                        match.getHomeTeam().getName().equals(savedMatch.getHomeTeamName()) &&
                                match.getAwayTeam().getName().equals(savedMatch.getAwayTeamName());

                if (sameWeek && sameTeams && match instanceof AbstractMatch) {
                    MatchResult result = new MatchResult(
                            match.getHomeTeam(),
                            match.getAwayTeam(),
                            savedMatch.getHomeScore(),
                            savedMatch.getAwayScore()
                    );

                    ((AbstractMatch) match).restoreResult(result);
                    break;
                }
            }
        }
    }
}
