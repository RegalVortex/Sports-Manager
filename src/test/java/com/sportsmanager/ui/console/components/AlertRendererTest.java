package com.sportsmanager.ui.console.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlertRenderer.isYes() — the confirmation parser.
 */
class AlertRendererTest {

    @Test
    void isYesReturnsTrueForYAndYes() {
        assertTrue(AlertRenderer.isYes("y"));
        assertTrue(AlertRenderer.isYes("Y"));
        assertTrue(AlertRenderer.isYes("yes"));
        assertTrue(AlertRenderer.isYes("YES"));
        assertTrue(AlertRenderer.isYes("Yes"));
    }

    @Test
    void isYesReturnsFalseForNo() {
        assertFalse(AlertRenderer.isYes("n"));
        assertFalse(AlertRenderer.isYes("no"));
        assertFalse(AlertRenderer.isYes("NO"));
    }

    @Test
    void isYesReturnsFalseForBlankAndNull() {
        assertFalse(AlertRenderer.isYes(null));
        assertFalse(AlertRenderer.isYes(""));
        assertFalse(AlertRenderer.isYes("  "));
    }

    @Test
    void isYesReturnsFalseForPartialMatch() {
        // "yeah" is not accepted — must be exactly y or yes
        assertFalse(AlertRenderer.isYes("yeah"));
        assertFalse(AlertRenderer.isYes("yep"));
        assertFalse(AlertRenderer.isYes("yes please"));
    }
}
