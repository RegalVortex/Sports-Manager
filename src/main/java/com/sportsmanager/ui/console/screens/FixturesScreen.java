package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the full fixture list with played results.
 */
public class FixturesScreen implements Screen {

    private final Screen parent;

    public FixturesScreen(Screen parent) {
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

        HeaderRenderer.render("Fikstur", league.getName()
            + " | Sezon " + ctx.getCurrentSeason() + " | Mevcut hafta " + league.getCurrentWeek());

        String[] headers = {"Hafta", "Ev", "Dep", "Skor", "Durum"};
        int[] widths = {5, 22, 22, 9, 10};
        List<String[]> rows = new ArrayList<>();
        int marker = -1;

        List<IMatch> fixtures = league.getAllFixtures();
        for (int i = 0; i < fixtures.size(); i++) {
            IMatch match = fixtures.get(i);
            if (marker < 0 && (match.getHomeTeam().equals(playerTeam) || match.getAwayTeam().equals(playerTeam))
                    && !match.isPlayed()) {
                marker = i;
            }
            MatchResult result = match.getResult();
            rows.add(new String[]{
                String.valueOf(match.getWeek()),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                result == null ? "-" : result.getHomeScore() + "-" + result.getAwayScore(),
                match.isPlayed() ? "Oynandi" : "Yaklasiyor"
            });
        }

        ConsolePrinter.blank();
        TableRenderer.renderWithMarker(headers, widths, rows, marker, ">");
        ConsolePrinter.blank();
        ConsolePrinter.line("  > = siradaki macin");
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
            ConsolePrinter.line("  Fikstur Yardimi: oynanan maclarda skor, yaklasan maclarda '-' gorunur.");
            ConsolePrinter.blank();
            return this;
        }
        return parent;
    }
}
