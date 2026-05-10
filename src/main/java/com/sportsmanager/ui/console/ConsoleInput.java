package com.sportsmanager.ui.console;

import java.util.Scanner;
import java.util.Locale;

/**
 * Thread-safe, crash-proof console input helper.
 *
 * All methods are static; this class is never instantiated.
 * The shared Scanner reads from System.in; it is intentionally
 * never closed so that System.in stays open for the whole session.
 */
public final class ConsoleInput {

    /* Package-private so tests can inject a different Scanner. */
    static Scanner scanner = new Scanner(System.in, "UTF-8");

    private ConsoleInput() {
    }

    /**
     * Read one line from the user.
     * Trims whitespace; returns {@code ""} on EOF or any error.
     */
    public static String readLine() {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
        } catch (Exception ignored) {
            // defensive: IOError, NoSuchElementException, etc.
        }
        return "";
    }

    /**
     * Parse a string as an integer choice.
     * Returns {@code -1} for blank, non-numeric, or overflow input.
     */
    public static int parseChoice(String input) {
        if (input == null || input.isBlank()) {
            return -1;
        }
        try {
            // Long.parseLong to detect overflow before int cast
            long value = Long.parseLong(input.trim());
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                return -1;
            }
            return (int) value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Returns true when {@code value} is in the inclusive range [min, max]. */
    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Returns true if the input is a standard "quit" command.
     * Accepts: q, Q, quit, QUIT (case-insensitive).
     */
    public static boolean isQuit(String input) {
        if (input == null) {
            return false;
        }
        String s = input.trim().toLowerCase(Locale.ROOT);
        return s.equals("q") || s.equals("quit");
    }

    /**
     * Returns true if the input is a standard "back" command.
     * Accepts: 0, b, B, back, BACK.
     */
    public static boolean isBack(String input) {
        if (input == null) {
            return false;
        }
        String s = input.trim().toLowerCase(Locale.ROOT);
        return s.equals("0") || s.equals("b") || s.equals("back");
    }

    /**
     * Returns true if the input is a standard "help" command.
     * Accepts: h, H, help, HELP.
     */
    public static boolean isHelp(String input) {
        if (input == null) {
            return false;
        }
        String s = input.trim().toLowerCase(Locale.ROOT);
        return s.equals("h") || s.equals("help");
    }
}
