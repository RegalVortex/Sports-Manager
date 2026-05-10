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
import java.util.Map;

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
            ConsolePrinter.error("Aktif lig yok.");
            ConsolePrinter.prompt();
            return;
        }

        HeaderRenderer.render(league.getName() + " - Tablo",
            "Sezon " + ctx.getCurrentSeason() + " | Mevcut hafta " + league.getCurrentWeek());

        String[] headers = {"#", "Takim", "P", "G", "B", "M", "GF", "GA", "AV", "Pts"};
        int[] widths = {3, 22, 3, 3, 3, 3, 4, 4, 4, 4};
        List<String[]> rows = new ArrayList<>();
        int rank = 1;
        int playerRankIndex = -1;
        Map<ITeam, int[]> goalTotals = UiStats.goalTotals(league);

        for (ITeam team : league.getStandings().getTeams()) {
            if (team.equals(playerTeam)) {
                playerRankIndex = rank - 1;
            }
            int[] goals = goalTotals.getOrDefault(team, new int[]{0, 0});
            int gf = goals[0];
            int ga = goals[1];
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
        ConsolePrinter.line("  > = senin takimin");
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
            ConsolePrinter.line("  Lig Tablosu Yardimi: P=oynanan, GF=atilan, GA=yenilen, AV=averaj, Pts=puan.");
            ConsolePrinter.blank();
            return this;
        }
        return parent;
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }
}
