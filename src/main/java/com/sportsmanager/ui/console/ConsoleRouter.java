package com.sportsmanager.ui.console;

/**
 * Drives the screen-based console UI.
 *
 * Main loop:
 *  1. Render the current screen.
 *  2. Read one line of input.
 *  3. Dispatch to the screen; get the next screen.
 *  4. Repeat until the screen returns null (quit signal).
 */
public class ConsoleRouter {

    private Screen current;

    public ConsoleRouter(Screen initialScreen) {
        this.current = initialScreen;
    }

    /** Run the game loop until the player quits. */
    public void run() {
        while (current != null) {
            try {
                current.render();
                String input = ConsoleInput.readLine();
                current = current.handleInput(input);
            } catch (Exception e) {
                System.out.println();
                ConsolePrinter.error("Something went wrong on this screen. You can try again or press Q to quit.");
                ConsolePrinter.info(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("Thanks for playing Sports Manager. Goodbye.");
    }
}
