package com.sportsmanager.ui.console.components;

import java.util.List;

/**
 * Renders short feedback lines in a consistent style.
 */
public final class AlertRenderer {

    private AlertRenderer() {
    }

    public static void warn(String message) {
        System.out.println("  !  " + message);
    }

    public static void error(String message) {
        System.out.println("  X  " + message);
    }

    public static void success(String message) {
        System.out.println("  OK " + message);
    }

    public static void info(String message) {
        System.out.println("  i  " + message);
    }

    public static void warnAll(List<String> warnings) {
        if (warnings == null) {
            return;
        }
        for (String warning : warnings) {
            warn(warning);
        }
    }

    public static void confirmPrompt(String message) {
        System.out.print("  ?  " + message + " ");
    }

    public static boolean isYes(String input) {
        if (input == null) {
            return false;
        }
        String value = input.trim().toLowerCase();
        return value.equals("y") || value.equals("yes");
    }
}
