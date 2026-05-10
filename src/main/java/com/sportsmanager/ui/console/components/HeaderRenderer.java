package com.sportsmanager.ui.console.components;

import com.sportsmanager.ui.console.ConsolePrinter;

import java.util.Locale;

/**
 * Renders consistent title and section blocks.
 */
public final class HeaderRenderer {

    private HeaderRenderer() {
    }

    public static void render(String title) {
        render(title, null);
    }

    public static void render(String title, String subtitle) {
        ConsolePrinter.blank();
        ConsolePrinter.frameTop();
        ConsolePrinter.framedLine(" " + safe(title).toUpperCase(Locale.ROOT));
        if (subtitle != null && !subtitle.isBlank()) {
            ConsolePrinter.frameMiddle();
            ConsolePrinter.framedLine(" " + subtitle);
        }
        ConsolePrinter.frameBottom();
    }

    public static void section(String title) {
        ConsolePrinter.blank();
        String label = " " + safe(title).toUpperCase(Locale.ROOT) + " ";
        int remaining = Math.max(4, ConsolePrinter.WIDTH - label.length() - 2);
        ConsolePrinter.line("  " + label + "-".repeat(remaining));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
