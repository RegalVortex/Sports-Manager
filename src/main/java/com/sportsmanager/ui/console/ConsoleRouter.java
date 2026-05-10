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
                ConsolePrinter.error("Bu ekranda beklenmeyen bir hata oldu. Tekrar deneyebilir veya Q ile cikabilirsin.");
                ConsolePrinter.info(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        System.out.println();
        System.out.println("Sports Manager oynadigin icin tesekkurler. Gorusmek uzere.");
    }
}
