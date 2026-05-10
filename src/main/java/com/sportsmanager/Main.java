package com.sportsmanager;

import com.sportsmanager.ui.App;
import com.sportsmanager.ui.console.ConsoleApp;

/**
 * Application entry point.
 *
 * Delegates to the JavaFX manager desk UI.
 */
public class Main {

    public static void main(String[] args) {
        if (args != null && args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            new ConsoleApp().start();
            return;
        }
        App.main(args);
    }
}
