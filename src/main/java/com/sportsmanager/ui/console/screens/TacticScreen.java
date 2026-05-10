package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.ui.console.ConsoleInput;
import com.sportsmanager.ui.console.ConsolePrinter;
import com.sportsmanager.ui.console.Screen;
import com.sportsmanager.ui.console.components.AlertRenderer;
import com.sportsmanager.ui.console.components.HeaderRenderer;
import com.sportsmanager.ui.console.components.TableRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets the manager choose a sport-specific tactic.
 */
public class TacticScreen implements Screen {

    private final Screen parent;
    private final SportFactory factory;
    private String message;

    public TacticScreen(Screen parent, SportFactory factory) {
        this.parent = parent;
        this.factory = factory;
    }

    @Override
    public void render() {
        ITeam team = GameContext.getInstance().getPlayerTeam();
        if (team == null) {
            ConsolePrinter.error("No active team.");
            ConsolePrinter.prompt();
            return;
        }

        String current = team.getTactic() == null ? "None" : team.getTactic().getName();
        HeaderRenderer.render("Tactics", team.getName() + " | Current: " + current);
        renderTactics(current);

        if (message != null) {
            ConsolePrinter.blank();
            AlertRenderer.info(message);
            message = null;
        }

        ConsolePrinter.blank();
        ConsolePrinter.navHint();
        ConsolePrinter.prompt();
    }

    @Override
    public Screen handleInput(String input) {
        if (ConsoleInput.isQuit(input)) {
            return null;
        }
        if (ConsoleInput.isBack(input)) {
            return parent;
        }
        if (ConsoleInput.isHelp(input)) {
            showHelp();
            return this;
        }

        ITeam team = GameContext.getInstance().getPlayerTeam();
        List<ITactic> tactics = factory.getAvailableTactics();
        int choice = ConsoleInput.parseChoice(input);
        if (!ConsoleInput.inRange(choice, 1, tactics.size())) {
            message = "Invalid tactic. Choose a listed number.";
            return this;
        }

        ITactic selected = tactics.get(choice - 1);
        team.setTactic(selected);
        message = "Tactic changed to " + selected.getName() + ".";
        return this;
    }

    private void renderTactics(String current) {
        String[] headers = {"#", "Tactic", "Attack", "Defence", "Status"};
        int[] widths = {3, 18, 8, 8, 10};
        List<String[]> rows = new ArrayList<>();
        List<ITactic> tactics = factory.getAvailableTactics();

        for (int i = 0; i < tactics.size(); i++) {
            ITactic tactic = tactics.get(i);
            rows.add(new String[]{
                String.valueOf(i + 1),
                tactic.getName(),
                String.format("%.2f", tactic.getAttackModifier()),
                String.format("%.2f", tactic.getDefenseModifier()),
                tactic.getName().equalsIgnoreCase(current) ? "Current" : ""
            });
        }
        ConsolePrinter.blank();
        TableRenderer.render(headers, widths, rows);
    }

    private void showHelp() {
        HeaderRenderer.section("Tactics Help");
        ConsolePrinter.line("  Higher attack improves scoring chances, higher defence reduces opponent chances.");
        ConsolePrinter.line("  Pick a tactic that fits your squad strength and next opponent.");
        ConsolePrinter.blank();
    }
}
