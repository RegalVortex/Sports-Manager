package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.MatchResult;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Simulates the next week and presents the player's match as an event report.
 */
public class MatchScreen implements Screen {

    private final MainDashboardScreen parent;
    private int phase;
    private IMatch playerMatch;
    private MatchResult playerResult;
    private final List<String> commentary = new ArrayList<>();
    private int newInjuries;

    public MatchScreen(MainDashboardScreen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        if (phase == 0) {
            renderPreview();
        } else {
            renderResult();
        }
        ConsolePrinter.blank();
        if (phase == 0) {
            ConsolePrinter.line("   1. Simulate Week");
            ConsolePrinter.line("   0. Back    H. Help    Q. Quit");
            ConsolePrinter.prompt();
        } else {
            ConsolePrinter.line("   0. Back to Dashboard    Q. Quit");
            ConsolePrinter.prompt();
        }
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (phase == 0) {
            if (ConsoleInput.isBack(input)) {
                return parent;
            }
            if (ConsoleInput.isHelp(input)) {
                showHelp();
                return this;
            }
            int choice = ConsoleInput.parseChoice(input);
            if (choice != 1 && !input.isBlank()) {
                ConsolePrinter.error("Invalid choice. Enter 1 to simulate, 0 to go back, or Q to quit.");
                return this;
            }
            simulateWeek();
            phase = 1;
            return this;
        }
        return parent;
    }

    private void renderPreview() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        int week = league.getCurrentWeek();

        HeaderRenderer.render("Match Center", "Week " + week + " preview");
        playerMatch = findPlayerMatch(league, team, week);

        if (playerMatch == null) {
            AlertRenderer.info("Your team has no fixture this week. Other matches will still be simulated.");
        } else {
            ITeam opponent = playerMatch.getHomeTeam().equals(team)
                ? playerMatch.getAwayTeam() : playerMatch.getHomeTeam();
            String venue = playerMatch.getHomeTeam().equals(team) ? "HOME" : "AWAY";
            ConsolePrinter.blank();
            ConsolePrinter.keyValue("Your Match", playerMatch.getHomeTeam().getName()
                + " vs " + playerMatch.getAwayTeam().getName());
            ConsolePrinter.keyValue("Venue", venue);
            ConsolePrinter.keyValue("Team OVR", String.valueOf(team.getTeamOverallRating()));
            ConsolePrinter.keyValue("Opponent OVR", String.valueOf(opponent.getTeamOverallRating()));
            ConsolePrinter.keyValue("Tactic", team.getTactic() != null ? team.getTactic().getName() : "None");
        }

        List<String> warnings = LineupWarnings.check(team);
        if (!warnings.isEmpty()) {
            HeaderRenderer.section("Lineup Warnings");
            AlertRenderer.warnAll(warnings);
        }

        HeaderRenderer.section("All Fixtures");
        for (IMatch match : league.getFixturesForWeek(week)) {
            ConsolePrinter.line("  " + match.getHomeTeam().getName()
                + " vs " + match.getAwayTeam().getName());
        }
    }

    private void renderResult() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        int playedWeek = Math.max(1, league.getCurrentWeek() - 1);

        HeaderRenderer.render("Match Report", "Week " + playedWeek + " results");

        if (playerResult == null) {
            AlertRenderer.info("Your team had no match this week.");
        } else {
            ConsolePrinter.blank();
            ConsolePrinter.line("  " + playerResult);
            ITeam winner = playerResult.getWinner();
            if (winner == null) {
                AlertRenderer.info("Draw. One point on the board.");
            } else if (winner.equals(team)) {
                AlertRenderer.success("Victory. The dressing room will like that.");
            } else {
                AlertRenderer.warn("Defeat. Check lineup, form, and tactics before the next week.");
            }
        }

        HeaderRenderer.section("Match Events");
        if (commentary.isEmpty()) {
            ConsolePrinter.line("  - No major events recorded.");
        } else {
            for (int i = 0; i < commentary.size(); i++) {
                ConsolePrinter.line(String.format("  %02d' %s", eventMinute(i), commentary.get(i)));
            }
        }

        HeaderRenderer.section("Player Highlights");
        renderHighlights(team);

        HeaderRenderer.section("All Results");
        for (IMatch match : league.getFixturesForWeek(playedWeek)) {
            if (match.getResult() != null) {
                ConsolePrinter.line("  " + match.getResult());
            }
        }

        HeaderRenderer.section("Standings");
        renderCompactStandings(league, team);
    }

    private void simulateWeek() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        int week = league.getCurrentWeek();
        int injuriesBefore = countTotalInjured(league);

        playerMatch = findPlayerMatch(league, team, week);
        league.advanceWeek();

        if (playerMatch != null) {
            playerResult = playerMatch.getResult();
            commentary.addAll(playerMatch.getCommentary());
        }

        newInjuries = Math.max(0, countTotalInjured(league) - injuriesBefore);
        generateNews(ctx, league, team, week);
    }

    private void renderHighlights(ITeam team) {
        IPlayer best = team.getStartingLineup().stream()
            .max(Comparator.comparingInt(IPlayer::getOverallRating))
            .orElse(null);
        if (best != null) {
            double rating = 6.0 + (best.getOverallRating() / 100.0) * 3.0 + best.getForm() * 0.2;
            ConsolePrinter.line(String.format("  - Best Player: %s, rating %.1f", best.getName(), rating));
        }

        boolean listedInjury = false;
        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                ConsolePrinter.line("  - Injury: " + player.getName()
                    + ", out for " + player.getInjuryGamesRemaining() + " week(s)");
                listedInjury = true;
            }
        }
        if (!listedInjury) {
            ConsolePrinter.line("  - Injury: none");
        }
        if (newInjuries > 0) {
            ConsolePrinter.line("  - New injuries this week: " + newInjuries);
        }
    }

    private void renderCompactStandings(ILeague league, ITeam playerTeam) {
        String[] headers = {"#", "Team", "P", "W", "D", "L", "GD", "Pts"};
        int[] widths = {3, 22, 3, 3, 3, 3, 4, 4};
        List<String[]> rows = new ArrayList<>();
        int rank = 1;
        int markedIndex = -1;
        for (ITeam team : league.getStandings().getTeams()) {
            if (team.equals(playerTeam)) {
                markedIndex = rank - 1;
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
                signed(gd),
                String.valueOf(team.getPoints())
            });
        }
        TableRenderer.renderWithMarker(headers, widths, rows, markedIndex, ">");
    }

    private void generateNews(GameContext ctx, ILeague league, ITeam team, int week) {
        if (playerResult != null) {
            ctx.addNews("[Week " + week + "] " + playerResult + ".");
        }
        if (newInjuries > 0) {
            ctx.addNews("[Week " + week + "] Injury alert: " + newInjuries + " player(s) unavailable.");
        }
        List<ITeam> standings = league.getStandings().getTeams();
        if (!standings.isEmpty()) {
            ITeam leader = standings.get(0);
            ctx.addNews("[Week " + week + "] " + leader.getName()
                + " leads with " + leader.getPoints() + " pts.");
        }
        int rank = league.getStandings().getRankOf(team);
        ctx.addNews("[Week " + week + "] " + team.getName()
            + " is " + rank + UiStats.ordinal(rank) + " in the table.");
    }

    private IMatch findPlayerMatch(ILeague league, ITeam team, int week) {
        for (IMatch match : league.getFixturesForWeek(week)) {
            if (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team)) {
                return match;
            }
        }
        return null;
    }

    private int countTotalInjured(ILeague league) {
        int count = 0;
        for (ITeam team : league.getTeams()) {
            count += UiStats.injuredCount(team);
        }
        return count;
    }

    private int eventMinute(int index) {
        return Math.min(90, 8 + index * 11);
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private void showHelp() {
        HeaderRenderer.section("Match Help");
        ConsolePrinter.line("  Enter 1 to simulate every fixture in the current week.");
        ConsolePrinter.line("  The report includes your result, match events, injuries, and table movement.");
        ConsolePrinter.blank();
    }
}
