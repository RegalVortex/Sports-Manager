package com.sportsmanager.ui.console;

/**
 * Small output helper used by the console UI.
 */
public final class ConsolePrinter {

    public static final int WIDTH = 96;

    private ConsolePrinter() {
    }

    public static void blank() {
        System.out.println();
    }

    public static void line(String text) {
        System.out.println(text == null ? "" : text);
    }

    public static void inline(String text) {
        System.out.print(text == null ? "" : text);
    }

    public static void prompt() {
        System.out.print("  > ");
    }

    public static void rule() {
        System.out.println("  " + "-".repeat(WIDTH));
    }

    public static void heavyRule() {
        System.out.println("  " + "=".repeat(WIDTH));
    }

    public static void frameTop() {
        System.out.println("  +" + "=".repeat(WIDTH - 2) + "+");
    }

    public static void frameMiddle() {
        System.out.println("  +" + "-".repeat(WIDTH - 2) + "+");
    }

    public static void frameBottom() {
        System.out.println("  +" + "=".repeat(WIDTH - 2) + "+");
    }

    public static void framedLine(String text) {
        String value = text == null ? "" : text;
        int contentWidth = WIDTH - 4;
        if (value.length() > contentWidth) {
            value = value.substring(0, contentWidth - 1) + ".";
        }
        System.out.printf("  | %-"+ contentWidth + "s |%n", value);
    }

    public static void keyValue(String label, String value) {
        System.out.printf("  %-18s %s%n", label + ":", value == null ? "-" : value);
    }

    public static void metric(String label, String value, String note) {
        System.out.printf("  %-18s %-12s %s%n", label + ":", value == null ? "-" : value, note == null ? "" : note);
    }

    public static void error(String msg) {
        System.out.println("  [!] " + msg);
    }

    public static void success(String msg) {
        System.out.println("  [OK] " + msg);
    }

    public static void warn(String msg) {
        System.out.println("  [!] " + msg);
    }

    public static void info(String msg) {
        System.out.println("  [i] " + msg);
    }

    public static void navHint() {
        System.out.println("  [0] Back    [H] Help    [Q] Quit");
    }
}
