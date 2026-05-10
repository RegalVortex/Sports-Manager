package com.sportsmanager.ui.console;

import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.ui.console.screens.SetupScreen;

/**
 * Entry point for the console UI.
 *
 * Responsibilities:
 *  - Bootstrap the sport registry (football + volleyball)
 *  - Create the initial setup screen
 *  - Hand control to the ConsoleRouter
 */
public class ConsoleApp {

    public void start() {
        // SportRegistry auto-registers football and volleyball in its constructor
        SportRegistry registry = new SportRegistry();
        GameSetupService setupService = new GameSetupService(registry);

        Screen initialScreen = new SetupScreen(registry, setupService);
        ConsoleRouter router = new ConsoleRouter(initialScreen);
        router.run();
    }
}
