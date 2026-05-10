package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.save.LoadedGame;
import com.sportsmanager.save.SaveLoadService;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.setup.PresetData;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.MenuRenderer;
import com.sportsmanager.ui.console.components.PanelRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * First-run flow: new career, load career, or quit.
 */
public class SetupScreen implements Screen {

    private static final String[] SLOT_FILES = {
        "save_slot1.dat", "save_slot2.dat", "save_slot3.dat"
    };

    private final SportRegistry registry;
    private final GameSetupService setupService;
    private int phase;
    private String selectedSport;
    private LeaguePreset selectedLeague;
    private String message;

    public SetupScreen(SportRegistry registry, GameSetupService setupService) {
        this.registry = registry;
        this.setupService = setupService;
    }

    @Override
    public void render() {
        switch (phase) {
            case 1:
                renderSportSelect();
                break;
            case 2:
                renderLeagueSelect();
                break;
            case 3:
                renderTeamSelect();
                break;
            case 4:
                renderLoadSelect();
                break;
            default:
                renderMainMenu();
                break;
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
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }
        switch (phase) {
            case 1:
                return handleSportSelect(input);
            case 2:
                return handleLeagueSelect(input);
            case 3:
                return handleTeamSelect(input);
            case 4:
                return handleLoadSelect(input);
            default:
                return handleMainMenu(input);
        }
    }

    private void renderMainMenu() {
        HeaderRenderer.render("Sports Manager", "Konsol kariyer modu");
        HeaderRenderer.section("Menajer Ofisi");
        PanelRenderer.note("Kariyer Komuta Merkezi",
            "Yeni kariyer kur, kayitli oyunu yukle veya cikis yap.");
        HeaderRenderer.section("Baslangic");
        PanelRenderer.actionGrid(List.of(
            "Yeni Kariyer",
            "Kariyer Yukle",
            "Cikis"
        ));
    }

    private Screen handleMainMenu(String input) {
        int choice = ConsoleInput.parseChoice(input);
        switch (choice) {
            case 1:
                phase = 1;
                return this;
            case 2:
                phase = 4;
                return this;
            case 3:
            case 0:
                return null;
            default:
                message = "Gecersiz secim. 1, 2, 3, H veya Q gir.";
                return this;
        }
    }

    private void renderSportSelect() {
        HeaderRenderer.render("Yeni Kariyer", "Adim 1/3 - spor sec");
        List<String> options = new ArrayList<>();
        for (String sport : availableSports()) {
            options.add(UiStats.sportLabel(sport));
        }
        MenuRenderer.render(options, true);
    }

    private Screen handleSportSelect(String input) {
        if (ConsoleInput.isBack(input)) {
            phase = 0;
            return this;
        }
        List<String> sports = availableSports();
        int choice = ConsoleInput.parseChoice(input);
        if (ConsoleInput.inRange(choice, 1, sports.size())) {
            selectedSport = sports.get(choice - 1);
            phase = 2;
        } else {
            message = "Gecersiz spor. Listedeki numaralardan birini sec.";
        }
        return this;
    }

    private void renderLeagueSelect() {
        HeaderRenderer.render("Yeni Kariyer", "Adim 2/3 - lig sec | Spor: " + UiStats.sportLabel(selectedSport));
        List<String> options = new ArrayList<>();
        for (LeaguePreset preset : PresetData.getLeaguesForSport(selectedSport)) {
            options.add(preset.getLeagueName() + " (" + preset.getTeamNames().size() + " takim)");
        }
        MenuRenderer.render(options, true);
    }

    private Screen handleLeagueSelect(String input) {
        if (ConsoleInput.isBack(input)) {
            phase = 1;
            return this;
        }
        List<LeaguePreset> leagues = PresetData.getLeaguesForSport(selectedSport);
        int choice = ConsoleInput.parseChoice(input);
        if (ConsoleInput.inRange(choice, 1, leagues.size())) {
            selectedLeague = leagues.get(choice - 1);
            phase = 3;
        } else {
            message = "Gecersiz lig. Listedeki numaralardan birini sec.";
        }
        return this;
    }

    private void renderTeamSelect() {
        HeaderRenderer.render("Yeni Kariyer", "Adim 3/3 - takim sec | " + selectedLeague.getLeagueName());
        MenuRenderer.render(new ArrayList<>(selectedLeague.getTeamNames()), true);
    }

    private Screen handleTeamSelect(String input) {
        if (ConsoleInput.isBack(input)) {
            phase = 2;
            return this;
        }
        List<String> teams = selectedLeague.getTeamNames();
        int choice = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(choice, 1, teams.size())) {
            message = "Gecersiz takim. Listedeki numaralardan birini sec.";
            return this;
        }

        try {
            GameSetupService.SetupResult result =
                setupService.createGame(selectedSport, selectedLeague, teams.get(choice - 1));
            applyGame(result.getSport(), result.getLeague(), result.getPlayerTeam(), registry.getFactory(selectedSport));
            MainDashboardScreen dashboard = new MainDashboardScreen(registry);
            dashboard.setMessage(result.getPlayerTeam().getName() + " ile kariyer basladi.");
            return dashboard;
        } catch (Exception e) {
            message = "Kurulum basarisiz: " + e.getMessage();
            return this;
        }
    }

    private void renderLoadSelect() {
        HeaderRenderer.render("Kariyer Yukle", "Kayit slotu sec");
        List<String> options = new ArrayList<>();
        for (int i = 0; i < SLOT_FILES.length; i++) {
            File file = SaveLoadService.resolveSaveFile(SLOT_FILES[i]);
            options.add("Slot " + (i + 1) + " - " + (file.exists() ? "Kayitli" : "Bos"));
        }
        MenuRenderer.render(options, true);
    }

    private Screen handleLoadSelect(String input) {
        if (ConsoleInput.isBack(input)) {
            phase = 0;
            return this;
        }
        int choice = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(choice, 1, SLOT_FILES.length)) {
            message = "Gecersiz slot. 1-" + SLOT_FILES.length + " veya 0 sec.";
            return this;
        }

        LoadedGame loaded = SaveLoadService.loadGame(SLOT_FILES[choice - 1], registry);
        if (loaded == null) {
            message = "Bu slot bos veya yuklenemiyor.";
            return this;
        }

        String sportName = loaded.getSport().getSportName().toLowerCase();
        applyGame(loaded.getSport(), loaded.getLeague(), loaded.getPlayerTeam(), registry.getFactory(sportName));
        MainDashboardScreen dashboard = new MainDashboardScreen(registry);
        dashboard.setMessage("Slot " + choice + " yuklendi.");
        return dashboard;
    }

    private void applyGame(ISport sport, ILeague league, ITeam playerTeam, SportFactory factory) {
        GameContext ctx = GameContext.getInstance();
        ctx.startNewGame(sport);
        ctx.setLeague(league);
        ctx.setPlayerTeam(playerTeam);
        ctx.setSportFactory(factory);
    }

    private List<String> availableSports() {
        List<String> sports = registry.getAvailableSports();
        sports.sort(Comparator.naturalOrder());
        return sports;
    }

    private void showHelp() {
        ConsolePrinter.blank();
        ConsolePrinter.line("  Yardim");
        ConsolePrinter.line("  Yeni Kariyer hazir takimlardan temiz bir sezon olusturur.");
        ConsolePrinter.line("  Kariyer Yukle uc yerel kayit slotundan birini geri getirir.");
        ConsolePrinter.line("  0 geri doner, H yardim acar, Q cikis yapar.");
        ConsolePrinter.blank();
    }
}
