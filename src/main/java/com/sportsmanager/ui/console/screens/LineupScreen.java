package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LineupWarnings;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.MenuRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Starting lineup management with warnings, bench view, and safe replacements.
 */
public class LineupScreen implements Screen {

    private final Screen parent;
    private int subPhase;
    private int outIndex = -1;
    private String message;

    public LineupScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        ITeam team = GameContext.getInstance().getPlayerTeam();
        if (team == null) {
            ConsolePrinter.error("Aktif takim yok.");
            ConsolePrinter.prompt();
            return;
        }

        if (subPhase == 1) {
            renderPickOut(team);
        } else if (subPhase == 2) {
            renderPickIn(team);
        } else {
            renderMain(team);
        }

        if (message != null) {
            ConsolePrinter.blank();
            AlertRenderer.info(message);
            message = null;
        }
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        ITeam team = GameContext.getInstance().getPlayerTeam();
        if (team == null) {
            return parent;
        }
        if (subPhase == 1) {
            return handlePickOut(input, team);
        }
        if (subPhase == 2) {
            return handlePickIn(input, team);
        }
        return handleMain(input, team);
    }

    private void renderMain(ITeam team) {
        HeaderRenderer.render("Kadro - " + team.getName(),
            "Ilk kadro: " + team.getStartingLineup().size()
                + " / " + LineupWarnings.expectedLineupSize(team));

        List<String> warnings = LineupWarnings.check(team);
        HeaderRenderer.section("Uyarilar");
        if (warnings.isEmpty()) {
            AlertRenderer.success("Kadro gecerli.");
        } else {
            AlertRenderer.warnAll(warnings);
        }

        HeaderRenderer.section("Mevcut Ilk Kadro");
        renderPlayerTable(team.getStartingLineup(), true);

        HeaderRenderer.section("Yedekler");
        renderPlayerTable(getBench(team), false);

        HeaderRenderer.section("Aksiyonlar");
        MenuRenderer.render(Arrays.asList(
            "Kadroyu otomatik duzelt",
            "Oyuncu degistir",
            "Yedekleri gor",
            "Kadroyu kontrol et"
        ), true);
    }

    private Screen handleMain(String input, ITeam team) {
        if (ConsoleInput.isBack(input)) {
            return parent;
        }
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }

        int choice = ConsoleInput.parseChoice(input);
        switch (choice) {
            case 1:
                autoFix(team);
                return this;
            case 2:
                if (team.getStartingLineup().isEmpty()) {
                    message = "Kadro bos - degistirilecek oyuncu yok.";
                    return this;
                }
                if (getBench(team).isEmpty()) {
                    message = "Yedekler bos - uygun degisim yok.";
                    return this;
                }
                subPhase = 1;
                return this;
            case 3:
                message = getBench(team).isEmpty()
                    ? "Yedekler bos."
                    : "Yedekler mevcut kadronun altinda gosteriliyor.";
                return this;
            case 4:
                List<String> warnings = LineupWarnings.check(team);
                message = warnings.isEmpty()
                    ? "Kadro gecerli ve hazir."
                    : "Kadroda " + warnings.size() + " sorun var.";
                return this;
            default:
                ConsolePrinter.error("Gecersiz secim. 0-4 arasinda bir sayi gir.");
                return this;
        }
    }

    private void renderPickOut(ITeam team) {
        HeaderRenderer.render("Oyuncu Degistir", "Adim 1/2 - cikacak oyuncuyu sec");
        renderNumberedPlayerList(team.getStartingLineup());
        ConsolePrinter.blank();
        ConsolePrinter.line("  0. Iptal");
    }

    private Screen handlePickOut(String input, ITeam team) {
        if (ConsoleInput.isBack(input)) {
            subPhase = 0;
            return this;
        }
        int choice = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(choice, 1, team.getStartingLineup().size())) {
            message = "Gecersiz secim. Ilk kadro oyuncusu sec veya 0 ile iptal et.";
            return this;
        }
        outIndex = choice - 1;
        subPhase = 2;
        return this;
    }

    private void renderPickIn(ITeam team) {
        IPlayer out = team.getStartingLineup().get(outIndex);
        HeaderRenderer.render("Oyuncu Degistir", "Adim 2/2 - " + out.getName() + " yerine oyuncu sec");
        renderNumberedPlayerList(getBench(team));
        ConsolePrinter.blank();
        ConsolePrinter.line("  0. Iptal");
    }

    private Screen handlePickIn(String input, ITeam team) {
        if (ConsoleInput.isBack(input)) {
            subPhase = 1;
            return this;
        }
        List<IPlayer> bench = getBench(team);
        int choice = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(choice, 1, bench.size())) {
            message = "Gecersiz secim. Yedek oyuncu sec veya 0 ile iptal et.";
            return this;
        }

        IPlayer outPlayer = team.getStartingLineup().get(outIndex);
        IPlayer inPlayer = bench.get(choice - 1);
        String outName = outPlayer.getName();
        team.substitutePlayer(outPlayer, inPlayer);

        boolean success = team.getStartingLineup().contains(inPlayer)
            && !team.getStartingLineup().contains(outPlayer);
        message = success
            ? "Degisiklik yapildi: " + outName + " cikti, " + inPlayer.getName() + " girdi."
            : "Degisiklik reddedildi. Sakatlik ve kadro kurallarini kontrol et.";

        subPhase = 0;
        outIndex = -1;
        return this;
    }

    private void autoFix(ITeam team) {
        if (!(team instanceof AbstractTeam)) {
            message = "Bu takim icin otomatik duzeltme kullanilamaz.";
            return;
        }
        List<IPlayer> before = new ArrayList<>(team.getStartingLineup());
        ((AbstractTeam) team).autoFixLineup();
        List<IPlayer> after = team.getStartingLineup();

        int replaced = 0;
        for (int i = 0; i < Math.min(before.size(), after.size()); i++) {
            if (!before.get(i).equals(after.get(i))) {
                replaced++;
            }
        }
        message = replaced > 0
            ? "Otomatik duzeltme " + replaced + " oyuncu degistirdi."
            : "Otomatik duzeltme gereken oyuncu bulmadi.";
    }

    private void renderPlayerTable(List<IPlayer> players, boolean lineup) {
        String[] headers = {"#", "Ad", "Pos", "Guc", "Form", "Durum"};
        int[] widths = {3, 22, 14, 4, 8, 14};
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            IPlayer player = players.get(i);
            rows.add(new String[]{
                String.valueOf(i + 1),
                player.getName(),
                player.getPosition(),
                String.valueOf(player.getOverallRating()),
                player.getFormLabel(),
                status(player, lineup)
            });
        }
        TableRenderer.render(headers, widths, rows);
    }

    private void renderNumberedPlayerList(List<IPlayer> players) {
        for (int i = 0; i < players.size(); i++) {
            IPlayer player = players.get(i);
            ConsolePrinter.line(String.format("  %2d. %-22s %-14s OVR:%-3d %s",
                i + 1,
                player.getName(),
                player.getPosition(),
                player.getOverallRating(),
                status(player, false)));
        }
    }

    private List<IPlayer> getBench(ITeam team) {
        List<IPlayer> bench = new ArrayList<>();
        for (IPlayer player : team.getSquad()) {
            if (!team.getStartingLineup().contains(player)) {
                bench.add(player);
            }
        }
        return bench;
    }

    private String status(IPlayer player, boolean lineup) {
        if (player.isInjured()) {
            return "Sakat " + player.getInjuryGamesRemaining() + "h";
        }
        return lineup ? "Ilk kadro" : "Hazir";
    }

    private void showHelp() {
        ConsolePrinter.blank();
        ConsolePrinter.line("  Kadro Yardimi");
        ConsolePrinter.line("  Otomatik duzeltme uygun saglikli yedek varsa sakat ilk kadro oyuncusunu degistirir.");
        ConsolePrinter.line("  Oyuncu degistir bir ilk kadro oyuncusunu bir yedekle degistirmeni saglar.");
        ConsolePrinter.line("  Kadro kontrolu sakatlik veya dizilis kurali sorunlarini aciklar.");
        ConsolePrinter.blank();
    }
}
