package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.GameContext;
import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.ui.console.Screen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke tests for the dashboard-focused console UI.
 */
class ConsoleScreenSmokeTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() throws Exception {
        resetGameContext();
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        createGame();
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        resetGameContext();
    }

    @Test
    void dashboardShowsCurrentWeekAndTeam() {
        MainDashboardScreen screen = new MainDashboardScreen();

        assertDoesNotThrow(screen::render);
        String text = out.toString();

        assertTrue(text.contains("MANAGER DASHBOARD"));
        assertTrue(text.contains("Season 1 | Week 1 | Galatasaray"));
        assertTrue(text.contains("NEXT MATCH"));
    }

    @Test
    void invalidDashboardInputDoesNotCrashOrNavigate() {
        MainDashboardScreen screen = new MainDashboardScreen();

        Screen next = assertDoesNotThrow(() -> screen.handleInput("abc"));

        assertSame(screen, next);
    }

    @Test
    void squadScreenDisplaysPlayerTable() {
        SquadScreen screen = new SquadScreen(new MainDashboardScreen());

        assertDoesNotThrow(screen::render);
        String text = out.toString();

        assertTrue(text.contains("SQUAD - GALATASARAY"));
        assertTrue(text.contains("Name"));
        assertTrue(text.contains("Fitness"));
    }

    @Test
    void leagueTableMarksPlayerTeam() {
        LeagueTableScreen screen = new LeagueTableScreen(new MainDashboardScreen());

        assertDoesNotThrow(screen::render);
        String text = out.toString();

        assertTrue(text.contains(">"));
        assertTrue(text.contains("Pts"));
    }

    private void createGame() {
        SportRegistry registry = new SportRegistry();
        GameSetupService setup = new GameSetupService(registry);
        LeaguePreset preset = new LeaguePreset(
            "football",
            "Test League",
            Arrays.asList("Galatasaray", "Fenerbahce", "Besiktas", "Trabzonspor")
        );
        GameSetupService.SetupResult result = setup.createGame("football", preset, "Galatasaray");

        GameContext ctx = GameContext.getInstance();
        ISport sport = result.getSport();
        ILeague league = result.getLeague();
        ITeam team = result.getPlayerTeam();
        SportFactory factory = registry.getFactory("football");

        ctx.startNewGame(sport);
        ctx.setLeague(league);
        ctx.setPlayerTeam(team);
        ctx.setSportFactory(factory);
    }

    private void resetGameContext() throws Exception {
        Field instance = GameContext.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }
}
