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
            ConsolePrinter.error("Aktif takim yok.");
            ConsolePrinter.prompt();
            return;
        }

        HeaderRenderer.render("Oyuncular - " + team.getName(),
            "Guc " + team.getTeamOverallRating() + " | Filtre " + filterLabel() + " | Siralama " + sortLabel());
        renderTable(buildPlayers(team), team);

        if (message != null) {
            ConsolePrinter.blank();
            ConsolePrinter.info(message);
            message = null;
        }

        if (pickingPosition) {
            renderPositionPicker(team);
        } else {
            HeaderRenderer.section("Aksiyonlar");
            MenuRenderer.render(Arrays.asList(
                "Tum Oyuncular",
                "Pozisyona Gore Filtrele",
                "Guce Gore Sirala",
                "Forma Gore Sirala",
                "Sakat Oyuncular"
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
                message = "Tum oyuncular gosteriliyor.";
                return this;
            case 2:
                pickingPosition = true;
                return this;
            case 3:
                sortMode = 0;
                injuredOnly = false;
                message = "Guce gore siralandi.";
                return this;
            case 4:
                sortMode = 1;
                injuredOnly = false;
                message = "Forma gore siralandi.";
                return this;
            case 5:
                injuredOnly = true;
                message = "Sadece sakat oyuncular gosteriliyor.";
                return this;
            default:
                ConsolePrinter.error("Gecersiz secim. 0-5 arasinda bir sayi gir.");
                return this;
        }
    }

    private void renderTable(List<IPlayer> players, ITeam team) {
        String[] headers = {"Ad", "Pos", "Guc", "Form", "Kond.", "Durum"};
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
        HeaderRenderer.section("Pozisyon Sec");
        ConsolePrinter.line("  0. Geri");
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
            message = positionFilter + " pozisyonuna gore filtrelendi.";
            return this;
        }
        ConsolePrinter.error("Gecersiz pozisyon. Listedeki numaralardan birini sec veya 0 ile geri don.");
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
            return "Sakat " + player.getInjuryGamesRemaining() + "h";
        }
        if (team.getStartingLineup().contains(player)) {
            return "Ilk kadro";
        }
        return "Hazir";
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
            return "Sakat";
        }
        return positionFilter;
    }

    private String sortLabel() {
        return sortMode == 1 ? "Form" : "Guc";
    }

    private void showHelp() {
        ConsolePrinter.blank();
        ConsolePrinter.line("  Oyuncu Yardimi");
        ConsolePrinter.line("  Tum oyuncular filtreleri sifirlar.");
        ConsolePrinter.line("  Belirli yedek ararken pozisyona gore filtrele.");
        ConsolePrinter.line("  Mac oncesi forma gore sirala ve degisiklikten once sakatlari kontrol et.");
        ConsolePrinter.blank();
    }
}
