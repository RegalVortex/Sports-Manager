package com.sportsmanager.ui.console;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConsoleInput helper methods.
 *
 * These exercise all the safe-parsing logic without touching System.in.
 */
class ConsoleInputTest {

    // ── parseChoice ──────────────────────────────────────────────

    @Test
    void parseChoiceReturnsMinusOneForNull() {
        assertEquals(-1, ConsoleInput.parseChoice(null));
    }

    @Test
    void parseChoiceReturnsMinusOneForBlank() {
        assertEquals(-1, ConsoleInput.parseChoice(""));
        assertEquals(-1, ConsoleInput.parseChoice("   "));
    }

    @Test
    void parseChoiceReturnsMinusOneForLetters() {
        assertEquals(-1, ConsoleInput.parseChoice("abc"));
        assertEquals(-1, ConsoleInput.parseChoice("one"));
        assertEquals(-1, ConsoleInput.parseChoice("H"));
    }

    @Test
    void parseChoiceReturnsMinusOneForMixedInput() {
        assertEquals(-1, ConsoleInput.parseChoice("1a"));
        assertEquals(-1, ConsoleInput.parseChoice("2.5"));
        assertEquals(-1, ConsoleInput.parseChoice("--3"));
    }

    @Test
    void parseChoiceReturnsMinusOneForOverflow() {
        // Value bigger than Integer.MAX_VALUE
        assertEquals(-1, ConsoleInput.parseChoice("99999999999999999999"));
    }

    @Test
    void parseChoiceReturnsCorrectValueForValidIntegers() {
        assertEquals(1,   ConsoleInput.parseChoice("1"));
        assertEquals(7,   ConsoleInput.parseChoice("7"));
        assertEquals(100, ConsoleInput.parseChoice("100"));
        assertEquals(0,   ConsoleInput.parseChoice("0"));
        assertEquals(-5,  ConsoleInput.parseChoice("-5"));
    }

    @Test
    void parseChoiceTrimsWhitespace() {
        assertEquals(3, ConsoleInput.parseChoice("  3  "));
    }

    // ── inRange ──────────────────────────────────────────────────

    @Test
    void inRangeReturnsTrueForBoundaryValues() {
        assertTrue(ConsoleInput.inRange(1,  1, 5));
        assertTrue(ConsoleInput.inRange(5,  1, 5));
        assertTrue(ConsoleInput.inRange(3,  1, 5));
    }

    @Test
    void inRangeReturnsFalseOutsideBounds() {
        assertFalse(ConsoleInput.inRange(0,  1, 5));
        assertFalse(ConsoleInput.inRange(6,  1, 5));
        assertFalse(ConsoleInput.inRange(-1, 1, 5));
    }

    @Test
    void inRangeReturnsTrueWhenMinEqualsMax() {
        assertTrue(ConsoleInput.inRange(3, 3, 3));
        assertFalse(ConsoleInput.inRange(2, 3, 3));
    }

    // ── isQuit ───────────────────────────────────────────────────

    @Test
    void isQuitRecognisesAllVariants() {
        assertTrue(ConsoleInput.isQuit("q"));
        assertTrue(ConsoleInput.isQuit("Q"));
        assertTrue(ConsoleInput.isQuit("quit"));
        assertTrue(ConsoleInput.isQuit("QUIT"));
    }

    @Test
    void isQuitReturnsFalseForOtherInput() {
        assertFalse(ConsoleInput.isQuit(null));
        assertFalse(ConsoleInput.isQuit(""));
        assertFalse(ConsoleInput.isQuit("0"));
        assertFalse(ConsoleInput.isQuit("back"));
        assertFalse(ConsoleInput.isQuit("quitter"));
    }

    // ── isBack ───────────────────────────────────────────────────

    @Test
    void isBackRecognisesAllVariants() {
        assertTrue(ConsoleInput.isBack("0"));
        assertTrue(ConsoleInput.isBack("b"));
        assertTrue(ConsoleInput.isBack("B"));
        assertTrue(ConsoleInput.isBack("back"));
        assertTrue(ConsoleInput.isBack("BACK"));
    }

    @Test
    void isBackReturnsFalseForOtherInput() {
        assertFalse(ConsoleInput.isBack(null));
        assertFalse(ConsoleInput.isBack(""));
        assertFalse(ConsoleInput.isBack("1"));
        assertFalse(ConsoleInput.isBack("q"));
    }

    // ── isHelp ───────────────────────────────────────────────────

    @Test
    void isHelpRecognisesAllVariants() {
        assertTrue(ConsoleInput.isHelp("h"));
        assertTrue(ConsoleInput.isHelp("H"));
        assertTrue(ConsoleInput.isHelp("help"));
        assertTrue(ConsoleInput.isHelp("HELP"));
    }

    @Test
    void isHelpReturnsFalseForOtherInput() {
        assertFalse(ConsoleInput.isHelp(null));
        assertFalse(ConsoleInput.isHelp(""));
        assertFalse(ConsoleInput.isHelp("0"));
        assertFalse(ConsoleInput.isHelp("helpful"));
    }
}
