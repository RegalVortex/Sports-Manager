package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.PanelRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command center for the manager.
 */
public class MainDashboardScreen implements Screen {

    private String pendingMessage;

    public void setMessage(String msg) {
        this.pendingMessage = msg;
    }

    @Override
    public void render() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        ISport sport = ctx.getSport();

        if (league == null || team == null || sport == null) {
            ConsolePrinter.error("No active game. Start a new game first.");
            ConsolePrinter.prompt();
            return;
        }

        int rank = league.getStandings().getRankOf(team);
        HeaderRenderer.render("Manager Dashboard",
            "Season " + ctx.getCurrentSeason() + " | Week " + league.getCurrentWeek()
                + " | " + team.getName());

        renderCommandSummary(sport, league, team, rank);
        renderNextMatch(league, team);
        renderReadiness(team);
        renderLineupWarnings(team);
        renderNews(ctx);
        renderSeasonOverBanner(league);
        renderPendingMessage();
        renderActions(league);
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input) || ConsoleInput.isBack(input)) {
            return null;
        }
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }

        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        if (league == null) {
            return this;
        }

        if (league.isSeasonOver()) {
            return handleSeasonOverInput(input, ctx, league);
        }

        int choice = ConsoleInput.parseChoice(input);
        SportFactory factory = ctx.getSportFactory();

        switch (choice) {
            case 1:
                return new MatchScreen(this);
            case 2:
                return new SquadScreen(this);
            case 3:
                return new LineupScreen(this);
            case 4:
                return factory != null ? new TacticScreen(this, factory) : this;
            case 5:
                return new LeagueTableScreen(this);
            case 6:
                return new FixturesScreen(this);
            case 7:
                return new NewsScreen(this);
            case 8:
                return new SaveLoadScreen(this);
            default:
                ConsolePrinter.error("Invalid choice. Enter 1-8, H for help, or 0/Q to exit.");
                return this;
        }
    }

    private void renderCommandSummary(ISport sport, ILeague league, ITeam team, int rank) {
        HeaderRenderer.section("Club Snapshot");
        PanelRenderer.statCards(Arrays.asList(
            new String[]{"Sport", sport.getSportName()},
            new String[]{"League", league.getName()},
            new String[]{"Position", rank + UiStats.ordinal(rank)},
            new String[]{"Points", String.valueOf(team.getPoints())},
            new String[]{"Record", league.getWins(team) + "W "
                + league.getDraws(team) + "D " + league.getLosses(team) + "L"},
            new String[]{"Team OVR", String.valueOf(team.getTeamOverallRating())},
            new String[]{"Tactic", team.getTactic() == null ? "Not selected" : team.getTactic().getName()},
            new String[]{"Morale", UiStats.morale(team) + "%"}
        ));
    }

    private void renderNextMatch(ILeague league, ITeam team) {
        HeaderRenderer.section("Next Match");
        IMatch match = nextMatch(league, team);
        if (match == null) {
            ConsolePrinter.info(league.isSeasonOver() ? "Season complete." : "No fixture this week.");
            return;
        }
        ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
        String venue = match.getHomeTeam().equals(team) ? "Home" : "Away";
        PanelRenderer.statCards(Arrays.asList(
            new String[]{"Fixture", match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName()},
            new String[]{"Venue", venue},
            new String[]{"Opponent", opponent.getName()},
            new String[]{"Opponent OVR", String.valueOf(opponent.getTeamOverallRating())}
        ));
    }

    private void renderReadiness(ITeam team) {
        HeaderRenderer.section("Readiness");
        PanelRenderer.progress("Team Morale", UiStats.morale(team), moraleNote(UiStats.morale(team)));
        PanelRenderer.progress("Squad Health", healthPercent(team),
            UiStats.injuredCount(team) + " injured");
        ConsolePrinter.metric("Lineup", team.getStartingLineup().size()
            + "/" + LineupWarnings.expectedLineupSize(team), "selected");
    }

    private void renderLineupWarnings(ITeam team) {
        List<String> warnings = LineupWarnings.check(team);
        if (warnings.isEmpty()) {
            return;
        }
        HeaderRenderer.section("Action Needed");
        AlertRenderer.warnAll(warnings);
    }

    private void renderNews(GameContext ctx) {
        HeaderRenderer.section("Latest News");
        List<String> news = ctx.getRecentNews(3);
        if (news.isEmpty()) {
            ConsolePrinter.line("  - No headlines yet. Play a week to generate news.");
            return;
        }
        for (int i = news.size() - 1; i >= 0; i--) {
            ConsolePrinter.line("  - " + news.get(i));
        }
    }

    private void renderSeasonOverBanner(ILeague league) {
        if (!league.isSeasonOver()) {
            return;
        }
        HeaderRenderer.section("Season Complete");
        ITeam champion = league.getChampion();
        ConsolePrinter.success("Champion: " + (champion == null ? "Unknown" : champion.getName()));
    }

    private void renderPendingMessage() {
        if (pendingMessage != null) {
            ConsolePrinter.blank();
            AlertRenderer.success(pendingMessage);
            pendingMessage = null;
        }
    }

    private void renderActions(ILeague league) {
        HeaderRenderer.section("Actions");
        if (league.isSeasonOver()) {
            PanelRenderer.actionGrid(List.of(
                "Start New Season",
                "League Table",
                "Squad",
                "Save / Load"
            ));
            ConsolePrinter.line("  [0] Exit    [H] Help    [Q] Quit");
            return;
        }
        PanelRenderer.actionGrid(List.of(
            "Play Next Week",
            "Squad",
            "Lineup",
            "Tactics",
            "League Table",
            "Fixtures",
            "News",
            "Save / Load"
        ));
        ConsolePrinter.line("  [0] Exit    [H] Help    [Q] Quit");
    }

    private Screen handleSeasonOverInput(String input, GameContext ctx, ILeague league) {
        int choice = ConsoleInput.parseChoice(input);
        switch (choice) {
            case 1:
                league.resetSeason();
                ctx.clearNews();
                ctx.addNews("[Season " + ctx.getCurrentSeason() + "] New season started.");
                setMessage("Season " + ctx.getCurrentSeason() + " started.");
                return this;
            case 2:
                return new LeagueTableScreen(this);
            case 3:
                return new SquadScreen(this);
            case 4:
                return new SaveLoadScreen(this);
            default:
                ConsolePrinter.error("Invalid choice. Enter 1-4, H for help, or 0/Q to exit.");
                return this;
        }
    }

    private IMatch nextMatch(ILeague league, ITeam team) {
        if (league.isSeasonOver()) {
            return null;
        }
        for (IMatch match : league.getFixturesForWeek(league.getCurrentWeek())) {
            if (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team)) {
                return match;
            }
        }
        return null;
    }

    private String moraleNote(int morale) {
        if (morale >= 75) {
            return "confident";
        }
        if (morale >= 45) {
            return "stable";
        }
        return "needs attention";
    }

    private int healthPercent(ITeam team) {
        if (team.getSquad().isEmpty()) {
            return 100;
        }
        int healthy = team.getSquad().size() - UiStats.injuredCount(team);
        return (int) Math.round((healthy * 100.0) / team.getSquad().size());
    }

    private void showHelp() {
        HeaderRenderer.section("Dashboard Help");
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Play Next Week", "simulate fixtures and read match events"});
        rows.add(new String[]{"Squad", "filter, sort, and inspect all players"});
        rows.add(new String[]{"Lineup", "fix injured or invalid starters"});
        rows.add(new String[]{"Tactics", "change attack/defence modifiers"});
        rows.add(new String[]{"Save / Load", "manage local save slots"});
        TableRenderer.render(new String[]{"Action", "Use"}, new int[]{18, 52}, rows);
        ConsolePrinter.blank();
    }
}
