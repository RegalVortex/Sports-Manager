package com.sportsmanager.setup;

import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;

import java.util.ArrayList;
import java.util.List;

public class GameSetupService {

    private final SportRegistry registry;

    public GameSetupService(SportRegistry registry) {
        this.registry = registry;
    }

    public SetupResult createGame(String sportName, LeaguePreset preset, String selectedTeamName) {
        SportFactory factory = registry.getFactory(sportName);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown sport: " + sportName);
        }

        ISport sport = factory.createSport();

        List<ITeam> teams = new ArrayList<>();
        ITeam playerTeam = null;

        for (String teamName : preset.getTeamNames()) {
            ITeam team = factory.createTeam(teamName, teamName + ".png");
            teams.add(team);

            if (teamName.equalsIgnoreCase(selectedTeamName)) {
                playerTeam = team;
            }
        }

        if (playerTeam == null) {
            throw new IllegalArgumentException("Selected team not found: " + selectedTeamName);
        }

        ILeague league = factory.createLeague(preset.getLeagueName(), teams);

        return new SetupResult(sport, league, playerTeam);
    }

    public static class SetupResult {

        private final ISport sport;
        private final ILeague league;
        private final ITeam playerTeam;

        public SetupResult(ISport sport, ILeague league, ITeam playerTeam) {
            this.sport = sport;
            this.league = league;
            this.playerTeam = playerTeam;
        }

        public ISport getSport() {
            return sport;
        }

        public ILeague getLeague() {
            return league;
        }

        public ITeam getPlayerTeam() {
            return playerTeam;
        }
    }
}