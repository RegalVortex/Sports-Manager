package com.sportsmanager.ui.console.components;

import java.util.List;

/**
 * Renders readable numbered menus.
 */
public final class MenuRenderer {

    private MenuRenderer() {
    }

    public static void render(List<String> options, boolean showBackHelpQuit) {
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  [%d] %-24s%n", i + 1, options.get(i));
            }
        }
        if (showBackHelpQuit) {
            System.out.println("  " + "-".repeat(42));
            System.out.println("  [0] Back    [H] Help    [Q] Quit");
        }
    }
}
