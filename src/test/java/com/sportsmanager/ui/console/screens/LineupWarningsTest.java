package com.sportsmanager.ui.console.screens;

import com.sportsmanager.core.ICoach;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ITactic;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.LineupWarnings;
import com.sportsmanager.sport.football.FootballSportFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LineupWarnings.check() logic.
 */
class LineupWarningsTest {

    // ── Minimal stub implementations ─────────────────────────────

    private static class StubPlayer implements IPlayer {
        private final String name;
        private final String position;
        private boolean injured = false;
        private int injuryGames = 0;

        StubPlayer(String name, String position) {
            this.name = name;
            this.position = position;
        }

        void setInjuredState(int weeks) {
            this.injured = weeks > 0;
            this.injuryGames = weeks;
        }

        @Override public String getName()           { return name; }
        @Override public String getPosition()       { return position; }
        @Override public int getAge()               { return 25; }
        @Override public int getPotential()         { return 75; }
        @Override public int getOverallRating()     { return 70; }
        @Override public boolean isInjured()        { return injured; }
        @Override public int getInjuryGamesRemaining() { return injuryGames; }
        @Override public int getForm()              { return 1; }
        @Override public String getFormLabel()      { return "Normal"; }
        @Override public int getMatchesPlayed()     { return 0; }
        @Override public int getWeeksInjured()      { return 0; }
        @Override public Map<String, Integer> getAttributes() { return new java.util.HashMap<>(); }
        @Override public void growOlder()           {}
        @Override public void setInjured(int g)     { this.injured = g > 0; this.injuryGames = g; }
        @Override public void decrementInjury()     {}
        @Override public void train(String a, int n) {}
        @Override public void setForm(int f)        {}
        @Override public void incrementMatchesPlayed() {}
    }

    private static class StubTeam implements ITeam {
        private final List<IPlayer> squad = new ArrayList<>();
        private final List<IPlayer> lineup = new ArrayList<>();

        void addToSquad(IPlayer p)  { squad.add(p); }
        void addToLineup(IPlayer p) { lineup.add(p); }

        @Override public String getName()               { return "StubTeam"; }
        @Override public List<IPlayer> getSquad()       { return squad; }
        @Override public List<IPlayer> getStartingLineup() { return lineup; }
        @Override public ICoach getCoach()              { return null; }
        @Override public ITactic getTactic()            { return null; }
        @Override public void setTactic(ITactic t)      {}
        @Override public void substitutePlayer(IPlayer out, IPlayer in) {}
        @Override public void addPoints(int p)          {}
        @Override public int getPoints()                { return 0; }
        @Override public void setCoach(ICoach c)        {}
        @Override public void resetPoints()             {}
        @Override public int getTeamOverallRating()     { return 70; }
        @Override public int getExpectedLineupSize()    { return teamHasGk() ? 11 : 6; }

        private boolean teamHasGk() {
            return squad.stream().anyMatch(player -> "GK".equalsIgnoreCase(player.getPosition()));
        }
    }

    // ── Tests ─────────────────────────────────────────────────────

    @Test
    void emptyLineupReturnsOneWarning() {
        StubTeam team = new StubTeam();
        List<String> warnings = LineupWarnings.check(team);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).toLowerCase().contains("bos"));
    }

    @Test
    void noWarningsWhenLineupIsHealthyAndHasGk() {
        StubTeam team = new StubTeam();
        StubPlayer gk = new StubPlayer("Keeper Karl", "GK");
        team.addToSquad(gk);
        team.addToLineup(gk);

        for (int i = 0; i < 10; i++) {
            StubPlayer p = new StubPlayer("Player " + i, "DEF");
            team.addToSquad(p);
            team.addToLineup(p);
        }

        List<String> warnings = LineupWarnings.check(team);
        assertTrue(warnings.isEmpty(), "Expected no warnings but got: " + warnings);
    }

    @Test
    void injuredPlayerInLineupGeneratesWarning() {
        StubTeam team = new StubTeam();
        StubPlayer gk = new StubPlayer("Keeper", "GK");
        team.addToSquad(gk);
        team.addToLineup(gk);

        StubPlayer injured = new StubPlayer("Broken Leg Bob", "DEF");
        injured.setInjuredState(3);
        team.addToSquad(injured);
        team.addToLineup(injured);

        // Fill the rest
        for (int i = 0; i < 9; i++) {
            StubPlayer p = new StubPlayer("Player " + i, "MID");
            team.addToSquad(p);
            team.addToLineup(p);
        }

        List<String> warnings = LineupWarnings.check(team);
        assertTrue(warnings.stream().anyMatch(w -> w.contains("Broken Leg Bob")),
            "Expected warning about injured player");
    }

    @Test
    void missingGkGeneratesWarning() {
        StubTeam team = new StubTeam();

        // Add a GK to squad (so sport is detected as football) but NOT to lineup
        StubPlayer gk = new StubPlayer("Keeper", "GK");
        team.addToSquad(gk); // squad only, not lineup

        for (int i = 0; i < 11; i++) {
            StubPlayer p = new StubPlayer("Player " + i, "DEF");
            team.addToSquad(p);
            team.addToLineup(p);
        }

        List<String> warnings = LineupWarnings.check(team);
        assertTrue(warnings.stream().anyMatch(w -> w.toLowerCase().contains("kaleci")),
            "Expected goalkeeper warning");
    }

    @Test
    void incompleteLineupGeneratesWarning() {
        StubTeam team = new StubTeam();
        StubPlayer gk = new StubPlayer("Keeper", "GK");
        team.addToSquad(gk);
        team.addToLineup(gk);

        // Only 7 players instead of 11
        for (int i = 0; i < 6; i++) {
            StubPlayer p = new StubPlayer("Player " + i, "MID");
            team.addToSquad(p);
            team.addToLineup(p);
        }

        List<String> warnings = LineupWarnings.check(team);
        assertTrue(warnings.stream().anyMatch(w -> w.contains("7") && w.contains("11")),
            "Expected incomplete lineup warning");
    }

    @Test
    void noGkWarningWhenNoGkPositionInSquad() {
        // Volleyball team — no GK position at all
        StubTeam team = new StubTeam();
        for (int i = 0; i < 6; i++) {
            StubPlayer p = new StubPlayer("Player " + i, "MB");
            team.addToSquad(p);
            team.addToLineup(p);
        }

        List<String> warnings = LineupWarnings.check(team);
        // No GK warning since no player in squad has position GK
        assertFalse(warnings.stream().anyMatch(w -> w.toLowerCase().contains("kaleci")),
            "Should not warn about GK for a sport that doesn't use GK");
    }

    @Test
    void footballTeamLineupSizeIsEleven() {
        FootballSportFactory factory = new FootballSportFactory();
        ITeam team = factory.createTeam("Test FC", "test.png");
        assertEquals(11, LineupWarnings.expectedLineupSize(team));
    }

    @Test
    void teamHasGkPlayerReturnsTrueForFootball() {
        FootballSportFactory factory = new FootballSportFactory();
        ITeam team = factory.createTeam("Test FC", "test.png");
        assertTrue(LineupWarnings.teamHasGkPlayer(team));
    }

    @Test
    void volleyballLineupSizeDoesNotDependOnSquadSize() {
        StubTeam team = new StubTeam();
        for (int i = 0; i < 20; i++) {
            team.addToSquad(new StubPlayer("Volley " + i, "OUTSIDE_HITTER"));
        }

        assertEquals(6, LineupWarnings.expectedLineupSize(team));
    }
}
