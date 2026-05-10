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
        HeaderRenderer.render("Sports Manager", "Console career mode");
        HeaderRenderer.section("Manager Office");
        PanelRenderer.note("Career Command Center",
            "Create a new club story, restore a saved career, or exit when you are done.");
        HeaderRenderer.section("Start");
        PanelRenderer.actionGrid(List.of(
            "New Career",
            "Load Career",
            "Quit"
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
                message = "Invalid choice. Please enter 1, 2, 3, H, or Q.";
                return this;
        }
    }

    private void renderSportSelect() {
        HeaderRenderer.render("New Career", "Step 1/3 - choose sport");
        MenuRenderer.render(availableSports(), true);
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
            message = "Invalid sport. Choose a listed number.";
        }
        return this;
    }

    private void renderLeagueSelect() {
        HeaderRenderer.render("New Career", "Step 2/3 - choose league | Sport: " + selectedSport);
        List<String> options = new ArrayList<>();
        for (LeaguePreset preset : PresetData.getLeaguesForSport(selectedSport)) {
            options.add(preset.getLeagueName() + " (" + preset.getTeamNames().size() + " teams)");
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
            message = "Invalid league. Choose a listed number.";
        }
        return this;
    }

    private void renderTeamSelect() {
        HeaderRenderer.render("New Career", "Step 3/3 - choose club | " + selectedLeague.getLeagueName());
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
            message = "Invalid team. Choose a listed number.";
            return this;
        }

        try {
            GameSetupService.SetupResult result =
                setupService.createGame(selectedSport, selectedLeague, teams.get(choice - 1));
            applyGame(result.getSport(), result.getLeague(), result.getPlayerTeam(), registry.getFactory(selectedSport));
            MainDashboardScreen dashboard = new MainDashboardScreen();
            dashboard.setMessage("Career started with " + result.getPlayerTeam().getName() + ".");
            return dashboard;
        } catch (Exception e) {
            message = "Setup failed: " + e.getMessage();
            return this;
        }
    }

    private void renderLoadSelect() {
        HeaderRenderer.render("Load Career", "Choose a save slot");
        List<String> options = new ArrayList<>();
        for (int i = 0; i < SLOT_FILES.length; i++) {
            File file = new File(SLOT_FILES[i]);
            options.add("Slot " + (i + 1) + " - " + (file.exists() ? "Saved" : "Empty"));
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
            message = "Invalid slot. Choose 1-" + SLOT_FILES.length + " or 0.";
            return this;
        }

        LoadedGame loaded = SaveLoadService.loadGame(SLOT_FILES[choice - 1], registry);
        if (loaded == null) {
            message = "That slot is empty or cannot be loaded.";
            return this;
        }

        String sportName = loaded.getSport().getSportName().toLowerCase();
        applyGame(loaded.getSport(), loaded.getLeague(), loaded.getPlayerTeam(), registry.getFactory(sportName));
        MainDashboardScreen dashboard = new MainDashboardScreen();
        dashboard.setMessage("Loaded Slot " + choice + ".");
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
        ConsolePrinter.line("  Help");
        ConsolePrinter.line("  New Career creates a clean season from preset teams.");
        ConsolePrinter.line("  Load Career restores one of the three local save slots.");
        ConsolePrinter.line("  Use 0 to go back, H for help, and Q to quit.");
        ConsolePrinter.blank();
    }
}
