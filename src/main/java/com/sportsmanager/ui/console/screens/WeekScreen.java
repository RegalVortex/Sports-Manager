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
import java.util.List;

/**
 * Simulates the current week and shows results.
 *
 * Phases:
 *  0 = pre-match preview (press Enter to simulate)
 *  1 = post-match results (press Enter to return)
 */
public class WeekScreen implements Screen {

    private final MainDashboardScreen parent;
    private int phase = 0;

    // Populated after simulation
    private MatchResult playerResult = null;
    private final List<String> commentary = new ArrayList<>();
    private int newInjuries = 0;
    private IMatch playerMatch = null;

    public WeekScreen(MainDashboardScreen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        if (phase == 0) {
            renderPreview();
        } else {
            renderResults();
        }
        ConsolePrinter.blank();
        if (phase == 0) {
            ConsolePrinter.inline("  Press ENTER to simulate, or 0 to cancel: ");
        } else {
            ConsolePrinter.inline("  Press ENTER to return to dashboard: ");
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
            simulateWeek();
            phase = 1;
            return this;
        } else {
            // Phase 1: return to dashboard after showing results
            return parent;
        }
    }

    // ── Phase 0: Preview ─────────────────────────────────────────

    private void renderPreview() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();

        int week = league.getCurrentWeek();
        HeaderRenderer.render("Week " + week + " Preview");

        // Find player's match this week
        ITeam opponent = null;
        String venue = "";
        for (IMatch match : league.getFixturesForWeek(week)) {
            if (match.getHomeTeam().equals(team)) {
                opponent = match.getAwayTeam();
                venue = "HOME";
                playerMatch = match;
                break;
            } else if (match.getAwayTeam().equals(team)) {
                opponent = match.getHomeTeam();
                venue = "AWAY";
                playerMatch = match;
                break;
            }
        }

        if (opponent != null) {
            ConsolePrinter.blank();
            ConsolePrinter.line(String.format("  YOUR MATCH  [%s]", venue));
            ConsolePrinter.line(String.format("  %-24s  vs  %-24s", team.getName(), opponent.getName()));
            ConsolePrinter.line(String.format("  OVR: %-5d               OVR: %-5d",
                team.getTeamOverallRating(), opponent.getTeamOverallRating()));
            ConsolePrinter.line("  Your Tactic:     " + (team.getTactic() != null ? team.getTactic().getName() : "—"));
            ConsolePrinter.line("  Opponent Tactic: " + (opponent.getTactic() != null ? opponent.getTactic().getName() : "—"));

            // Lineup warnings
            List<String> warnings = LineupWarnings.check(team);
            if (!warnings.isEmpty()) {
                ConsolePrinter.blank();
                ConsolePrinter.line("  Lineup Issues:");
                for (String w : warnings) {
                    AlertRenderer.warn(w);
                }
            }

            ConsolePrinter.blank();
            ConsolePrinter.line("  Injury report:  " + countInjured(team) + " of your players are injured.");
        } else {
            ConsolePrinter.blank();
            ConsolePrinter.info("Your team has no fixture in week " + week + ".");
        }

        // All fixtures this week
        List<IMatch> allMatches = league.getFixturesForWeek(week);
        if (allMatches.size() > 1) {
            HeaderRenderer.section("ALL WEEK " + week + " FIXTURES");
            for (IMatch m : allMatches) {
                ConsolePrinter.line(String.format("  %-24s  vs  %s",
                    m.getHomeTeam().getName(), m.getAwayTeam().getName()));
            }
        }
    }

    // ── Phase 1: Results ─────────────────────────────────────────

    private void renderResults() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        int week = league.getCurrentWeek() - 1; // week was already advanced

        HeaderRenderer.render("Week " + week + " Results");

        // Player's result
        if (playerResult != null) {
            ConsolePrinter.blank();
            ConsolePrinter.line("  YOUR RESULT:");
            ConsolePrinter.line("  " + playerResult);

            ITeam winner = playerResult.getWinner();
            if (winner == null) {
                AlertRenderer.info("Draw! You held your ground.");
            } else if (winner.equals(team)) {
                AlertRenderer.success("Victory! Well done!");
            } else {
                AlertRenderer.warn("Defeat. Regroup and come back stronger.");
            }
        } else {
            ConsolePrinter.blank();
            AlertRenderer.info("Your team had no fixture this week.");
        }

        // Match commentary
        if (!commentary.isEmpty()) {
            HeaderRenderer.section("MATCH EVENTS");
            for (String line : commentary) {
                ConsolePrinter.line("  > " + line);
            }
        }

        // All results
        HeaderRenderer.section("ALL RESULTS");
        List<IMatch> allMatches = league.getFixturesForWeek(week);
        for (IMatch m : allMatches) {
            MatchResult r = m.getResult();
            if (r != null) {
                ConsolePrinter.line("  " + r);
            }
        }

        // Injuries
        if (newInjuries > 0) {
            HeaderRenderer.section("INJURY NEWS");
            AlertRenderer.warn(newInjuries + " new player(s) injured this week.");
            for (IPlayer p : team.getSquad()) {
                if (p.isInjured()) {
                    ConsolePrinter.line(String.format(
                        "  [~] %-22s  out %d week%s",
                        p.getName(), p.getInjuryGamesRemaining(),
                        p.getInjuryGamesRemaining() == 1 ? "" : "s"));
                }
            }
        }

        // Updated standings (compact)
        HeaderRenderer.section("STANDINGS");
        renderCompactStandings(league, team);
    }

    private void renderCompactStandings(ILeague league, ITeam playerTeam) {
        String[] headers = {"Pos", "Team", "W", "D", "L", "Pts"};
        int[] widths = {3, 22, 3, 3, 3, 4};
        List<String[]> rows = new ArrayList<>();
        int rank = 1;
        int playerRankIndex = -1;

        for (ITeam t : league.getStandings().getTeams()) {
            if (t.equals(playerTeam)) {
                playerRankIndex = rank - 1;
            }
            rows.add(new String[]{
                String.valueOf(rank++),
                t.getName(),
                String.valueOf(league.getWins(t)),
                String.valueOf(league.getDraws(t)),
                String.valueOf(league.getLosses(t)),
                String.valueOf(t.getPoints())
            });
        }
        TableRenderer.renderWithMarker(headers, widths, rows, playerRankIndex, ">");
    }

    // ── Simulation ────────────────────────────────────────────────

    private void simulateWeek() {
        GameContext ctx = GameContext.getInstance();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();

        int injuredBefore = countTotalInjured(league);
        int week = league.getCurrentWeek();

        // Capture commentary before advancing
        if (playerMatch != null) {
            // will be available after simulate() inside advanceWeek
        }

        league.advanceWeek();

        // Gather commentary from player's match
        if (playerMatch != null) {
            commentary.addAll(playerMatch.getCommentary());
            playerResult = playerMatch.getResult();
        }

        int injuredAfter = countTotalInjured(league);
        newInjuries = Math.max(0, injuredAfter - injuredBefore);

        // Update news feed
        generateNews(ctx, league, team, week);
    }

    private void generateNews(GameContext ctx, ILeague league, ITeam team, int week) {
        List<ITeam> standings = league.getStandings().getTeams();

        if (!standings.isEmpty()) {
            ITeam leader = standings.get(0);
            ctx.addNews("Week " + week + ": " + leader.getName()
                + " leads with " + leader.getPoints() + " pts.");
        }

        if (playerResult != null) {
            ITeam winner = playerResult.getWinner();
            if (winner == null) {
                ctx.addNews(team.getName() + " drew in week " + week + ".");
            } else if (winner.equals(team)) {
                ctx.addNews(team.getName() + " won in week " + week + "!");
            } else {
                ctx.addNews(team.getName() + " lost in week " + week + ".");
            }
        }

        if (newInjuries > 0) {
            ctx.addNews("Injury alert: " + newInjuries + " player(s) got injured in week " + week + ".");
        }

        int rank = league.getStandings().getRankOf(team);
        ctx.addNews(team.getName() + " is " + rank + ordinal(rank) + " in the table.");
    }

    // ── Helpers ───────────────────────────────────────────────────

    private static int countInjured(ITeam team) {
        int n = 0;
        for (IPlayer p : team.getSquad()) {
            if (p.isInjured()) {
                n++;
            }
        }
        return n;
    }

    private static int countTotalInjured(ILeague league) {
        int n = 0;
        for (ITeam t : league.getTeams()) {
            n += countInjured(t);
        }
        return n;
    }

    private static String ordinal(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}
