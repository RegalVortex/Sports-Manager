package com.sportsmanager.ui.console.components;

import com.sportsmanager.ui.console.ConsolePrinter;

import java.util.List;

/**
 * Higher-level console panels for dashboard-style screens.
 */
public final class PanelRenderer {

    private static final int CARD_WIDTH = 22;

    private PanelRenderer() {
    }

    public static void statCards(List<String[]> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (int i = 0; i < items.size(); i += 4) {
            int end = Math.min(i + 4, items.size());
            renderCardBorder(end - i, true);
            renderCardLine(items, i, end, 0);
            renderCardLine(items, i, end, 1);
            renderCardBorder(end - i, false);
        }
    }

    public static void progress(String label, int percent, String note) {
        int safePercent = Math.max(0, Math.min(100, percent));
        int blocks = (int) Math.round(safePercent / 10.0);
        String bar = "#".repeat(blocks) + ".".repeat(10 - blocks);
        ConsolePrinter.line(String.format("  %-18s [%s] %3d%%  %s",
            label + ":", bar, safePercent, note == null ? "" : note));
    }

    public static void actionGrid(List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        for (int i = 0; i < actions.size(); i += 2) {
            String left = command(i + 1, actions.get(i));
            String right = i + 1 < actions.size() ? command(i + 2, actions.get(i + 1)) : "";
            ConsolePrinter.line(String.format("  %-42s  %-42s", left, right));
        }
    }

    public static void note(String title, String body) {
        ConsolePrinter.line("  " + title);
        ConsolePrinter.line("  " + "-".repeat(Math.min(ConsolePrinter.WIDTH - 2, Math.max(12, title.length()))));
        ConsolePrinter.line("  " + (body == null ? "" : body));
    }

    private static void renderCardBorder(int count, boolean top) {
        StringBuilder line = new StringBuilder("  ");
        for (int i = 0; i < count; i++) {
            line.append(top ? "+" : "+");
            line.append("-".repeat(CARD_WIDTH));
            line.append("+");
            if (i < count - 1) {
                line.append("  ");
            }
        }
        ConsolePrinter.line(line.toString());
    }

    private static void renderCardLine(List<String[]> items, int start, int end, int row) {
        StringBuilder line = new StringBuilder("  ");
        for (int i = start; i < end; i++) {
            String[] item = items.get(i);
            String label = item != null && item.length > 0 ? item[0] : "";
            String value = item != null && item.length > 1 ? item[1] : "";
            String text = row == 0 ? label.toUpperCase() : value;
            line.append("| ");
            line.append(TableRenderer.pad(text, CARD_WIDTH - 2));
            line.append(" |");
            if (i < end - 1) {
                line.append("  ");
            }
        }
        ConsolePrinter.line(line.toString());
    }

    private static String command(int number, String label) {
        return "[" + number + "] " + (label == null ? "" : label);
    }
}
