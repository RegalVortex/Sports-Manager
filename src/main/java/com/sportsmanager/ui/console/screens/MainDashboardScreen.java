package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IMatch;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LineupWarnings;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
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

    private final SportRegistry registry;
    private String pendingMessage;

    public MainDashboardScreen() {
        this(new SportRegistry());
    }

    public MainDashboardScreen(SportRegistry registry) {
        this.registry = registry;
    }

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
            ConsolePrinter.error("Aktif oyun yok. Once yeni kariyer baslat.");
            ConsolePrinter.prompt();
            return;
        }

        int rank = league.getStandings().getRankOf(team);
        HeaderRenderer.render("Menajer Paneli",
            "Sezon " + ctx.getCurrentSeason() + " | Hafta " + league.getCurrentWeek()
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
                return new SaveLoadScreen(this, registry);
            default:
                ConsolePrinter.error("Gecersiz secim. 1-8, H, 0 veya Q gir.");
                return this;
        }
    }

    private void renderCommandSummary(ISport sport, ILeague league, ITeam team, int rank) {
        HeaderRenderer.section("Kulup Ozeti");
        PanelRenderer.statCards(Arrays.asList(
            new String[]{"Spor", UiStats.sportLabel(sport.getSportName())},
            new String[]{"Lig", league.getName()},
            new String[]{"Sira", rank + "."},
            new String[]{"Puan", String.valueOf(team.getPoints())},
            new String[]{"Karne", league.getWins(team) + "G "
                + league.getDraws(team) + "B " + league.getLosses(team) + "M"},
            new String[]{"Takim Gucu", String.valueOf(team.getTeamOverallRating())},
            new String[]{"Taktik", team.getTactic() == null ? "Secilmedi" : UiStats.tacticLabel(team.getTactic().getName())},
            new String[]{"Moral", UiStats.morale(team) + "%"}
        ));
    }

    private void renderNextMatch(ILeague league, ITeam team) {
        HeaderRenderer.section("Siradaki Mac");
        IMatch match = nextMatch(league, team);
        if (match == null) {
            ConsolePrinter.info(league.isSeasonOver() ? "Sezon tamamlandi." : "Bu hafta mac yok.");
            return;
        }
        ITeam opponent = match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
        String venue = match.getHomeTeam().equals(team) ? "Ev" : "Deplasman";
        PanelRenderer.statCards(Arrays.asList(
            new String[]{"Mac", match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName()},
            new String[]{"Saha", venue},
            new String[]{"Rakip", opponent.getName()},
            new String[]{"Rakip Gucu", String.valueOf(opponent.getTeamOverallRating())}
        ));
    }

    private void renderReadiness(ITeam team) {
        HeaderRenderer.section("Hazirlik");
        PanelRenderer.progress("Takim Morali", UiStats.morale(team), moraleNote(UiStats.morale(team)));
        PanelRenderer.progress("Kadro Sagligi", healthPercent(team),
            UiStats.injuredCount(team) + " sakat");
        ConsolePrinter.metric("Ilk Kadro", team.getStartingLineup().size()
            + "/" + LineupWarnings.expectedLineupSize(team), "secili");
    }

    private void renderLineupWarnings(ITeam team) {
        List<String> warnings = LineupWarnings.check(team);
        if (warnings.isEmpty()) {
            return;
        }
        HeaderRenderer.section("Dikkat Gerekenler");
        AlertRenderer.warnAll(warnings);
    }

    private void renderNews(GameContext ctx) {
        HeaderRenderer.section("Son Haberler");
        List<String> news = ctx.getRecentNews(3);
        if (news.isEmpty()) {
            ConsolePrinter.line("  - Henuz haber yok. Haber uretmek icin bir hafta oyna.");
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
        HeaderRenderer.section("Sezon Tamamlandi");
        ITeam champion = league.getChampion();
        ConsolePrinter.success("Sampiyon: " + (champion == null ? "Bilinmiyor" : champion.getName()));
    }

    private void renderPendingMessage() {
        if (pendingMessage != null) {
            ConsolePrinter.blank();
            AlertRenderer.success(pendingMessage);
            pendingMessage = null;
        }
    }

    private void renderActions(ILeague league) {
        HeaderRenderer.section("Aksiyonlar");
        if (league.isSeasonOver()) {
            PanelRenderer.actionGrid(List.of(
                "Yeni Sezona Basla",
                "Lig Tablosu",
                "Kadro",
                "Kaydet / Yukle"
            ));
            ConsolePrinter.line("  [0] Cikis    [H] Yardim    [Q] Cikis");
            return;
        }
        PanelRenderer.actionGrid(List.of(
            "Sonraki Haftayi Oyna",
            "Oyuncular",
            "Kadro",
            "Taktikler",
            "Lig Tablosu",
            "Fikstur",
            "Haberler",
            "Kaydet / Yukle"
        ));
        ConsolePrinter.line("  [0] Cikis    [H] Yardim    [Q] Cikis");
    }

    private Screen handleSeasonOverInput(String input, GameContext ctx, ILeague league) {
        int choice = ConsoleInput.parseChoice(input);
        switch (choice) {
            case 1:
                league.resetSeason();
                ctx.clearNews();
                ctx.addNews("[Sezon " + ctx.getCurrentSeason() + "] Yeni sezon basladi.");
                setMessage("Sezon " + ctx.getCurrentSeason() + " basladi.");
                return this;
            case 2:
                return new LeagueTableScreen(this);
            case 3:
                return new SquadScreen(this);
            case 4:
                return new SaveLoadScreen(this, registry);
            default:
                ConsolePrinter.error("Gecersiz secim. 1-4, H, 0 veya Q gir.");
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
            return "guvenli";
        }
        if (morale >= 45) {
            return "dengeli";
        }
        return "dikkat istiyor";
    }

    private int healthPercent(ITeam team) {
        if (team.getSquad().isEmpty()) {
            return 100;
        }
        int healthy = team.getSquad().size() - UiStats.injuredCount(team);
        return (int) Math.round((healthy * 100.0) / team.getSquad().size());
    }

    private void showHelp() {
        HeaderRenderer.section("Panel Yardimi");
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Hafta Oyna", "fiksturu simule eder ve mac olaylarini gosterir"});
        rows.add(new String[]{"Oyuncular", "oyunculari filtrele, sirala ve incele"});
        rows.add(new String[]{"Kadro", "sakat veya gecersiz ilk kadroyu duzelt"});
        rows.add(new String[]{"Taktik", "hucum/savunma dengesini degistir"});
        rows.add(new String[]{"Kaydet", "yerel kayit slotlarini yonet"});
        TableRenderer.render(new String[]{"Aksiyon", "Kullanim"}, new int[]{18, 52}, rows);
        ConsolePrinter.blank();
    }
}
