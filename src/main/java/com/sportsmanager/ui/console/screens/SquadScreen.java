package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.MenuRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Squad overview with simple filters and sorting.
 */
public class SquadScreen implements Screen {

    private final Screen parent;
    private String positionFilter = "ALL";
    private int sortMode;
    private boolean injuredOnly;
    private boolean pickingPosition;
    private String message;

    public SquadScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        ITeam team = GameContext.getInstance().getPlayerTeam();
        if (team == null) {
            ConsolePrinter.error("No active team.");
            ConsolePrinter.prompt();
            return;
        }

        HeaderRenderer.render("Squad - " + team.getName(),
            "OVR " + team.getTeamOverallRating() + " | Filter " + filterLabel() + " | Sort " + sortLabel());
        renderTable(buildPlayers(team), team);

        if (message != null) {
            ConsolePrinter.blank();
            ConsolePrinter.info(message);
            message = null;
        }

        if (pickingPosition) {
            renderPositionPicker(team);
        } else {
            HeaderRenderer.section("Actions");
            MenuRenderer.render(Arrays.asList(
                "View All Players",
                "Filter by Position",
                "Sort by Overall",
                "Sort by Form",
                "View Injured Players"
            ), true);
        }
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (ConsoleInput.isBack(input)) {
            if (pickingPosition) {
                pickingPosition = false;
                return this;
            }
            return parent;
        }
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }

        ITeam team = GameContext.getInstance().getPlayerTeam();
        if (pickingPosition) {
            return handlePositionChoice(input, team);
        }

        int choice = ConsoleInput.parseChoice(input);
        switch (choice) {
            case 1:
                positionFilter = "ALL";
                injuredOnly = false;
                sortMode = 0;
                message = "Showing all players.";
                return this;
            case 2:
                pickingPosition = true;
                return this;
            case 3:
                sortMode = 0;
                injuredOnly = false;
                message = "Sorted by overall rating.";
                return this;
            case 4:
                sortMode = 1;
                injuredOnly = false;
                message = "Sorted by form.";
                return this;
            case 5:
                injuredOnly = true;
                message = "Showing injured players only.";
                return this;
            default:
                ConsolePrinter.error("Invalid choice. Please enter a number between 0 and 5.");
                return this;
        }
    }

    private void renderTable(List<IPlayer> players, ITeam team) {
        String[] headers = {"Name", "Pos", "OVR", "Form", "Fitness", "Status"};
        int[] widths = {22, 14, 4, 8, 7, 14};
        List<String[]> rows = new ArrayList<>();
        for (IPlayer player : players) {
            rows.add(new String[]{
                player.getName(),
                player.getPosition(),
                String.valueOf(player.getOverallRating()),
                player.getFormLabel(),
                fitness(player),
                status(player, team)
            });
        }
        ConsolePrinter.blank();
        TableRenderer.render(headers, widths, rows);
    }

    private void renderPositionPicker(ITeam team) {
        List<String> positions = positions(team);
        HeaderRenderer.section("Choose Position");
        ConsolePrinter.line("  0. Back");
        for (int i = 0; i < positions.size(); i++) {
            ConsolePrinter.line("  " + (i + 1) + ". " + positions.get(i));
        }
        ConsolePrinter.blank();
    }

    private Screen handlePositionChoice(String input, ITeam team) {
        int choice = ConsoleInput.parseChoice(input);
        List<String> positions = positions(team);
        if (ConsoleInput.inRange(choice, 1, positions.size())) {
            positionFilter = positions.get(choice - 1);
            injuredOnly = false;
            pickingPosition = false;
            message = "Filtered by " + positionFilter + ".";
            return this;
        }
        ConsolePrinter.error("Invalid position. Please choose a listed number or 0 to go back.");
        return this;
    }

    private List<IPlayer> buildPlayers(ITeam team) {
        List<IPlayer> players = new ArrayList<>(team.getSquad());
        if (!"ALL".equals(positionFilter)) {
            players.removeIf(player -> !positionFilter.equalsIgnoreCase(player.getPosition()));
        }
        if (injuredOnly) {
            players.removeIf(player -> !player.isInjured());
        }
        if (sortMode == 1) {
            players.sort(Comparator.comparingInt(IPlayer::getForm).reversed()
                .thenComparing(Comparator.comparingInt(IPlayer::getOverallRating).reversed()));
        } else {
            players.sort(Comparator.comparingInt(IPlayer::getOverallRating).reversed());
        }
        return players;
    }

    private List<String> positions(ITeam team) {
        Set<String> values = new LinkedHashSet<>();
        for (IPlayer player : team.getSquad()) {
            values.add(player.getPosition());
        }
        return new ArrayList<>(values);
    }

    private String status(IPlayer player, ITeam team) {
        if (player.isInjured()) {
            return "Injured " + player.getInjuryGamesRemaining() + "w";
        }
        if (team.getStartingLineup().contains(player)) {
            return "Starting";
        }
        return "Fit";
    }

    private String fitness(IPlayer player) {
        if (player.isInjured()) {
            return "0%";
        }
        int fitness = 70 + player.getForm() * 10;
        return Math.min(100, fitness) + "%";
    }

    private String filterLabel() {
        if (injuredOnly) {
            return "Injured";
        }
        return positionFilter;
    }

    private String sortLabel() {
        return sortMode == 1 ? "Form" : "Overall";
    }

    private void showHelp() {
        ConsolePrinter.blank();
        ConsolePrinter.line("  Squad Help");
        ConsolePrinter.line("  View all players to reset filters.");
        ConsolePrinter.line("  Filter by position when you need a specific replacement.");
        ConsolePrinter.line("  Sort by form before a match, and check injured players before lineup changes.");
        ConsolePrinter.blank();
    }
}
