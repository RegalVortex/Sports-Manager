package com.sportsmanager.ui.console.components;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for TableRenderer helper methods.
 */
class TableRendererTest {

    @Test
    void padTruncatesLongText() {
        String result = TableRenderer.pad("Very Long Team Name Here", 10);
        assertEquals(10, result.length());
        assertTrue(result.endsWith("."));
    }

    @Test
    void padPadsShortText() {
        String result = TableRenderer.pad("Short", 10);
        assertEquals(10, result.length());
        assertTrue(result.startsWith("Short"));
    }

    @Test
    void padHandlesNullInput() {
        String result = TableRenderer.pad(null, 5);
        assertEquals(5, result.length());
        assertEquals("     ", result);
    }

    @Test
    void padExactLengthIsUnchanged() {
        String result = TableRenderer.pad("Exact", 5);
        assertEquals("Exact", result);
    }

    @Test
    void padRightAligns() {
        String result = TableRenderer.padRight("42", 5);
        assertEquals("   42", result);
    }

    @Test
    void renderDoesNotCrashWithNullRows() {
        assertDoesNotThrow(() ->
            TableRenderer.render(
                new String[]{"A", "B"},
                new int[]{5, 5},
                null
            )
        );
    }

    @Test
    void renderDoesNotCrashWithEmptyRows() {
        assertDoesNotThrow(() ->
            TableRenderer.render(
                new String[]{"Name", "OVR"},
                new int[]{10, 4},
                new ArrayList<>()
            )
        );
    }

    @Test
    void renderDoesNotCrashWithNullRowEntries() {
        List<String[]> rows = new ArrayList<>();
        rows.add(null);
        rows.add(new String[]{"Player", "75"});
        assertDoesNotThrow(() ->
            TableRenderer.render(
                new String[]{"Name", "OVR"},
                new int[]{10, 4},
                rows
            )
        );
    }

    @Test
    void renderWithMarkerDoesNotCrashForOutOfRangeIndex() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"1", "TeamA", "9"});
        rows.add(new String[]{"2", "TeamB", "6"});

        assertDoesNotThrow(() ->
            TableRenderer.renderWithMarker(
                new String[]{"Pos", "Team", "Pts"},
                new int[]{3, 10, 3},
                rows,
                -1,
                ">"
            )
        );
    }

    @Test
    void renderCanInferColumnWidths() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Kerem", "LW", "82"});

        assertDoesNotThrow(() ->
            TableRenderer.render(
                new String[]{"Name", "Pos", "OVR"},
                rows
            )
        );
    }
}
