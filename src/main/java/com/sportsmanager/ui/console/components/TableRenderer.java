package com.sportsmanager.ui.console.components;

import java.util.List;

/**
 * Renders fixed-width tables with predictable spacing.
 */
public final class TableRenderer {

    private TableRenderer() {
    }

    public static void render(String[] headers, List<String[]> rows) {
        if (headers == null) {
            return;
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = Math.max(3, length(headers[i]));
        }
        if (rows != null) {
            for (String[] row : rows) {
                if (row == null) {
                    continue;
                }
                for (int i = 0; i < headers.length && i < row.length; i++) {
                    widths[i] = Math.max(widths[i], length(row[i]));
                }
            }
        }
        render(headers, widths, rows);
    }

    public static void render(String[] headers, int[] widths, List<String[]> rows) {
        renderInternal(headers, widths, rows, -1, "");
    }

    public static void renderWithMarker(String[] headers, int[] widths,
                                        List<String[]> rows, int markedIndex, String marker) {
        renderInternal(headers, widths, rows, markedIndex, marker == null ? "" : marker);
    }

    private static void renderInternal(String[] headers, int[] widths, List<String[]> rows,
                                       int markedIndex, String marker) {
        if (headers == null || widths == null || headers.length != widths.length) {
            return;
        }

        String prefix = markedIndex >= 0 ? "    " : "  ";
        System.out.println(prefix + rowText(headers, widths));
        System.out.println(prefix + divider(widths));

        if (rows == null || rows.isEmpty()) {
            System.out.println(prefix + "(no entries)");
            return;
        }

        for (int i = 0; i < rows.size(); i++) {
            String rowPrefix = prefix;
            if (markedIndex >= 0) {
                rowPrefix = i == markedIndex ? String.format("%-2s  ", marker) : "    ";
            }
            System.out.println(rowPrefix + rowText(rows.get(i), widths));
        }
    }

    private static String rowText(String[] values, int[] widths) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < widths.length; i++) {
            String cell = (values != null && i < values.length) ? values[i] : "";
            line.append(pad(cell, widths[i]));
            if (i < widths.length - 1) {
                line.append("  ");
            }
        }
        return line.toString();
    }

    private static String divider(int[] widths) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < widths.length; i++) {
            line.append("-".repeat(widths[i]));
            if (i < widths.length - 1) {
                line.append("  ");
            }
        }
        return line.toString();
    }

    public static String pad(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (width <= 0) {
            return "";
        }
        if (text.length() > width) {
            return width == 1 ? "." : text.substring(0, width - 1) + ".";
        }
        return String.format("%-" + width + "s", text);
    }

    public static String padRight(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (width <= 0) {
            return "";
        }
        if (text.length() > width) {
            return width == 1 ? "." : text.substring(0, width - 1) + ".";
        }
        return String.format("%" + width + "s", text);
    }

    private static int length(String value) {
        return value == null ? 0 : value.length();
    }
}
