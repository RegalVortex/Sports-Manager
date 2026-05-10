package com.sportsmanager.ui.console;

/**
 * A single screen in the console UI.
 */
public interface Screen {

    /** Draw the screen content to stdout. */
    void render();

    /**
     * React to one line of user input.
     *
     * @param input the trimmed line the user typed
     * @return next screen to display, or null to quit
     */
    Screen handleInput(String input);
}
