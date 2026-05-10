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
            ConsolePrinter.error("Aktif takim yok.");
            ConsolePrinter.prompt();
            return;
        }

        String current = team.getTactic() == null ? "Yok" : team.getTactic().getName();
        HeaderRenderer.render("Taktikler", team.getName() + " | Aktif: " + UiStats.tacticLabel(current));
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
            message = "Gecersiz taktik. Listedeki numaralardan birini sec.";
            return this;
        }

        ITactic selected = tactics.get(choice - 1);
        team.setTactic(selected);
        message = "Taktik " + UiStats.tacticLabel(selected.getName()) + " olarak degisti.";
        return this;
    }

    private void renderTactics(String current) {
        String[] headers = {"#", "Taktik", "Hucum", "Defans", "Durum"};
        int[] widths = {3, 18, 8, 8, 10};
        List<String[]> rows = new ArrayList<>();
        List<ITactic> tactics = factory.getAvailableTactics();

        for (int i = 0; i < tactics.size(); i++) {
            ITactic tactic = tactics.get(i);
            rows.add(new String[]{
                String.valueOf(i + 1),
                UiStats.tacticLabel(tactic.getName()),
                String.format("%.2f", tactic.getAttackModifier()),
                String.format("%.2f", tactic.getDefenseModifier()),
                tactic.getName().equalsIgnoreCase(current) ? "Aktif" : ""
            });
        }
        ConsolePrinter.blank();
        TableRenderer.render(headers, widths, rows);
    }

    private void showHelp() {
        HeaderRenderer.section("Taktik Yardimi");
        ConsolePrinter.line("  Yuksek hucum skor sansini artirir, yuksek defans rakip sansini azaltir.");
        ConsolePrinter.line("  Kadro gucune ve siradaki rakibe uygun taktigi sec.");
        ConsolePrinter.blank();
    }

}
