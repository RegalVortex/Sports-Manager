package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Full league standings table. The player's team row is marked with ">".
 */
public class LeagueTableScreen implements Screen {

    private final Screen parent;

    public LeagueTableScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam playerTeam = ctx.getPlayerTeam();

        if (league == null) {
            ConsolePrinter.error("No active league.");
            ConsolePrinter.prompt();
            return;
        }

        HeaderRenderer.render(league.getName() + " - Table",
            "Season " + ctx.getCurrentSeason() + " | Current week " + league.getCurrentWeek());

        String[] headers = {"#", "Team", "P", "W", "D", "L", "GF", "GA", "GD", "Pts"};
        int[] widths = {3, 22, 3, 3, 3, 3, 4, 4, 4, 4};
        List<String[]> rows = new ArrayList<>();
        int rank = 1;
        int playerRankIndex = -1;

        for (ITeam team : league.getStandings().getTeams()) {
            if (team.equals(playerTeam)) {
                playerRankIndex = rank - 1;
            }
            int gf = UiStats.goalsFor(league, team);
            int ga = UiStats.goalsAgainst(league, team);
            int gd = gf - ga;
            rows.add(new String[]{
                String.valueOf(rank++),
                team.getName(),
                String.valueOf(UiStats.played(league, team)),
                String.valueOf(league.getWins(team)),
                String.valueOf(league.getDraws(team)),
                String.valueOf(league.getLosses(team)),
                String.valueOf(gf),
                String.valueOf(ga),
                signed(gd),
                String.valueOf(team.getPoints())
            });
        }

        ConsolePrinter.blank();
        TableRenderer.renderWithMarker(headers, widths, rows, playerRankIndex, ">");
        ConsolePrinter.blank();
        ConsolePrinter.line("  > = your team");
        ConsolePrinter.navHint();
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (ConsoleInput.isHelp(input)) {
            ConsolePrinter.blank();
            ConsolePrinter.line("  League Table Help: P=played, GF=for, GA=against, GD=difference, Pts=points.");
            ConsolePrinter.blank();
            return this;
        }
        return parent;
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }
}
