package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LineupWarnings;
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
            ConsolePrinter.line("   1. Haftayi Simule Et");
            ConsolePrinter.line("   0. Geri    H. Yardim    Q. Cikis");
            ConsolePrinter.prompt();
        } else {
            ConsolePrinter.line("   0. Panele Don    Q. Cikis");
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
                ConsolePrinter.error("Gecersiz secim. Simulasyon icin 1, geri icin 0 veya cikis icin Q gir.");
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

        HeaderRenderer.render("Mac Merkezi", "Hafta " + week + " onizlemesi");
        playerMatch = findPlayerMatch(league, team, week);

        if (playerMatch == null) {
            AlertRenderer.info("Takiminin bu hafta maci yok. Diger maclar yine simule edilecek.");
        } else {
            ITeam opponent = playerMatch.getHomeTeam().equals(team)
                ? playerMatch.getAwayTeam() : playerMatch.getHomeTeam();
            String venue = playerMatch.getHomeTeam().equals(team) ? "EV" : "DEPLASMAN";
            ConsolePrinter.blank();
            ConsolePrinter.keyValue("Macin", playerMatch.getHomeTeam().getName()
                + " vs " + playerMatch.getAwayTeam().getName());
            ConsolePrinter.keyValue("Saha", venue);
            ConsolePrinter.keyValue("Takim Gucu", String.valueOf(team.getTeamOverallRating()));
            ConsolePrinter.keyValue("Rakip Gucu", String.valueOf(opponent.getTeamOverallRating()));
        ConsolePrinter.keyValue("Taktik", team.getTactic() != null ? UiStats.tacticLabel(team.getTactic().getName()) : "Yok");
        }

        List<String> warnings = LineupWarnings.check(team);
        if (!warnings.isEmpty()) {
            HeaderRenderer.section("Kadro Uyarilari");
            AlertRenderer.warnAll(warnings);
        }

        HeaderRenderer.section("Tum Fikstur");
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

        HeaderRenderer.render("Mac Raporu", "Hafta " + playedWeek + " sonuclari");

        if (playerResult == null) {
            AlertRenderer.info("Takimin bu hafta mac yapmadi.");
        } else {
            ConsolePrinter.blank();
            ConsolePrinter.line("  " + playerResult);
            ITeam winner = playerResult.getWinner();
            if (winner == null) {
                AlertRenderer.info("Beraberlik. Haneye bir puan yazildi.");
            } else if (winner.equals(team)) {
                AlertRenderer.success("Galibiyet. Soyunma odasi bunu sevecek.");
            } else {
                AlertRenderer.warn("Maglubiyet. Sonraki hafta oncesi kadro, form ve taktigi kontrol et.");
            }
        }

        HeaderRenderer.section("Mac Olaylari");
        if (commentary.isEmpty()) {
            ConsolePrinter.line("  - Kayda deger olay yok.");
        } else {
            for (int i = 0; i < commentary.size(); i++) {
                ConsolePrinter.line(String.format("  %02d' %s", eventMinute(i), commentary.get(i)));
            }
        }

        HeaderRenderer.section("Oyuncu Notlari");
        renderHighlights(team);

        HeaderRenderer.section("Tum Sonuclar");
        for (IMatch match : league.getFixturesForWeek(playedWeek)) {
            if (match.getResult() != null) {
                ConsolePrinter.line("  " + match.getResult());
            }
        }

        HeaderRenderer.section("Puan Durumu");
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
            ConsolePrinter.line(String.format("  - En iyi oyuncu: %s, not %.1f", best.getName(), rating));
        }

        boolean listedInjury = false;
        for (IPlayer player : team.getSquad()) {
            if (player.isInjured()) {
                ConsolePrinter.line("  - Sakatlik: " + player.getName()
                    + ", " + player.getInjuryGamesRemaining() + " hafta yok");
                listedInjury = true;
            }
        }
        if (!listedInjury) {
            ConsolePrinter.line("  - Sakatlik: yok");
        }
        if (newInjuries > 0) {
            ConsolePrinter.line("  - Bu haftaki yeni sakatlik: " + newInjuries);
        }
    }

    private void renderCompactStandings(ILeague league, ITeam playerTeam) {
        String[] headers = {"#", "Takim", "P", "G", "B", "M", "AV", "Pts"};
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
            ctx.addNews("[Hafta " + week + "] " + playerResult + ".");
        }
        if (newInjuries > 0) {
            ctx.addNews("[Hafta " + week + "] Sakatlik uyarisi: " + newInjuries + " oyuncu yok.");
        }
        List<ITeam> standings = league.getStandings().getTeams();
        if (!standings.isEmpty()) {
            ITeam leader = standings.get(0);
            ctx.addNews("[Hafta " + week + "] " + leader.getName()
                + " " + leader.getPoints() + " puanla lider.");
        }
        int rank = league.getStandings().getRankOf(team);
        ctx.addNews("[Hafta " + week + "] " + team.getName()
            + " tabloda " + rank + ". sirada.");
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
        HeaderRenderer.section("Mac Yardimi");
        ConsolePrinter.line("  Bu haftadaki tum maclari simule etmek icin 1 gir.");
        ConsolePrinter.line("  Rapor takim sonucunu, mac olaylarini, sakatliklari ve tablo hareketini gosterir.");
        ConsolePrinter.blank();
    }
}
