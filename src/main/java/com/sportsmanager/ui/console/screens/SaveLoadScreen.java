package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.save.LoadedGame;
import com.sportsmanager.save.SaveLoadService;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.MenuRenderer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Local save slot management.
 */
public class SaveLoadScreen implements Screen {

    static final String[] SLOT_FILES = {
        "save_slot1.dat", "save_slot2.dat", "save_slot3.dat"
    };

    private final Screen parent;
    private final SportRegistry registry = new SportRegistry();
    private int phase;
    private String message;

    public SaveLoadScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        HeaderRenderer.render(title(), subtitle());
        renderSlotStatus();
        HeaderRenderer.section("Actions");
        if (phase == 0) {
            MenuRenderer.render(List.of("Save Game", "Load Game", "Delete Save"), true);
        } else {
            ConsolePrinter.line("  Choose slot 1-" + SLOT_FILES.length + ".");
            ConsolePrinter.navHint();
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
        if (ConsoleInput.isBack(input)) {
            if (phase == 0) {
                return parent;
            }
            phase = 0;
            return this;
        }

        if (phase == 0) {
            return handleAction(input);
        }
        if (phase == 1) {
            return handleSave(input);
        }
        if (phase == 2) {
            return handleLoad(input);
        }
        if (phase == 3) {
            return handleDelete(input);
        }
        return this;
    }

    private Screen handleAction(String input) {
        int choice = ConsoleInput.parseChoice(input);
        if (ConsoleInput.inRange(choice, 1, 3)) {
            phase = choice;
        } else {
            message = "Invalid choice. Enter 1-3, 0, H, or Q.";
        }
        return this;
    }

    private Screen handleSave(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Invalid slot. Choose 1-" + SLOT_FILES.length + ".";
            return this;
        }

        GameContext ctx = GameContext.getInstance();
        ISport sport = ctx.getSport();
        ILeague league = ctx.getLeague();
        ITeam team = ctx.getPlayerTeam();
        if (sport == null || league == null || team == null) {
            message = "No active game to save.";
            phase = 0;
            return this;
        }

        File file = new File(SLOT_FILES[slot - 1]);
        if (file.exists() && !confirm("Slot " + slot + " already has data. Overwrite? [Y/N]")) {
            message = "Save cancelled.";
            phase = 0;
            return this;
        }

        SaveLoadService.saveGame(SLOT_FILES[slot - 1], sport, league, team);
        message = "Game saved. Season " + league.getCurrentSeason()
            + ", Week " + league.getCurrentWeek() + ", " + team.getName() + ".";
        phase = 0;
        return this;
    }

    private Screen handleLoad(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Invalid slot. Choose 1-" + SLOT_FILES.length + ".";
            return this;
        }
        File file = new File(SLOT_FILES[slot - 1]);
        if (!file.exists()) {
            message = "Slot " + slot + " is empty.";
            return this;
        }
        if (!confirm("Loading will replace current progress. Continue? [Y/N]")) {
            message = "Load cancelled.";
            phase = 0;
            return this;
        }

        LoadedGame loaded = SaveLoadService.loadGame(SLOT_FILES[slot - 1], registry);
        if (loaded == null) {
            message = "Load failed. The save may be corrupted.";
            phase = 0;
            return this;
        }

        GameContext ctx = GameContext.getInstance();
        ctx.startNewGame(loaded.getSport());
        ctx.setLeague(loaded.getLeague());
        ctx.setPlayerTeam(loaded.getPlayerTeam());
        String sportName = loaded.getSport().getSportName().toLowerCase();
        SportFactory factory = registry.getFactory(sportName);
        ctx.setSportFactory(factory);

        MainDashboardScreen dashboard = new MainDashboardScreen();
        dashboard.setMessage("Loaded Slot " + slot + ".");
        return dashboard;
    }

    private Screen handleDelete(String input) {
        int slot = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(slot, 1, SLOT_FILES.length)) {
            message = "Invalid slot. Choose 1-" + SLOT_FILES.length + ".";
            return this;
        }
        File file = new File(SLOT_FILES[slot - 1]);
        if (!file.exists()) {
            message = "Slot " + slot + " is already empty.";
            phase = 0;
            return this;
        }
        if (!confirm("Delete Slot " + slot + " permanently? [Y/N]")) {
            message = "Delete cancelled.";
            phase = 0;
            return this;
        }
        message = file.delete() ? "Slot " + slot + " deleted." : "Could not delete Slot " + slot + ".";
        phase = 0;
        return this;
    }

    private boolean confirm(String prompt) {
        AlertRenderer.confirmPrompt(prompt);
        ConsolePrinter.blank();
        ConsolePrinter.prompt();
        return AlertRenderer.isYes(ConsoleInput.readLine());
    }

    private void renderSlotStatus() {
        for (int i = 0; i < SLOT_FILES.length; i++) {
            File file = new File(SLOT_FILES[i]);
            String status = "Empty";
            if (file.exists()) {
                status = "Saved " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(file.lastModified()));
            }
            ConsolePrinter.keyValue("Slot " + (i + 1), status);
        }
    }

    private String title() {
        switch (phase) {
            case 1:
                return "Save Game";
            case 2:
                return "Load Game";
            case 3:
                return "Delete Save";
            default:
                return "Save / Load";
        }
    }

    private String subtitle() {
        switch (phase) {
            case 1:
                return "Pick where the current career should be saved";
            case 2:
                return "Pick a save to restore";
            case 3:
                return "Pick a save to remove";
            default:
                return "Three local slots, stored beside the project";
        }
    }

    private void showHelp() {
        HeaderRenderer.section("Save / Load Help");
        ConsolePrinter.line("  Save writes the current season, week, team, squad, points, and fixtures.");
        ConsolePrinter.line("  Load replaces the active game after confirmation.");
        ConsolePrinter.line("  Delete only removes the selected local save slot.");
        ConsolePrinter.blank();
    }
}
