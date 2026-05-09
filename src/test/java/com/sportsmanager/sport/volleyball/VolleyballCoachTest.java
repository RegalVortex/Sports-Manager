package com.sportsmanager.sport.volleyball;

import com.sportsmanager.core.IPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballCoachTest {

    @Test
    void shouldReturnNameCorrectly() {
        VolleyballCoach coach = new VolleyballCoach("Daniele Santarelli", "ATTACKING");
        assertEquals("Daniele Santarelli", coach.getName());
    }

    @Test
    void shouldReturnSpecialtyCorrectly() {
        VolleyballCoach coach = new VolleyballCoach("Giovanni Guidetti", "DEFENSIVE");
        assertEquals("DEFENSIVE", coach.getSpecialty());
    }

    @Test
    void nullSpecialtyShouldDefaultToBalanced() {
        VolleyballCoach coach = new VolleyballCoach("Coach", null);
        assertEquals("BALANCED", coach.getSpecialty());
    }

    @Test
    void qualityShouldBeInValidRange() {
        for (int i = 0; i < 20; i++) {
            VolleyballCoach coach = new VolleyballCoach("TestCoach", "BALANCED");
            int q = coach.getQuality();
            assertTrue(q >= 1 && q <= 10,
                    "Quality must be 1-10, was: " + q);
        }
    }

    @Test
    void attackingCoachShouldImproveSpikeAndServe() {
        VolleyballPlayer player = new VolleyballPlayer("Player1", "OUTSIDE_HITTER");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int spikeBefore = player.getAttributes().get("spike");
        int serveBefore = player.getAttributes().get("serve");

        VolleyballCoach coach = new VolleyballCoach("CoachA", "ATTACKING");
        coach.trainPlayers(players);

        // Bonus = quality/5 (0-2); minimum artış spike+2, serve+1
        assertTrue(player.getAttributes().get("spike") >= spikeBefore + 2,
                "Spike should increase by at least 2");
        assertTrue(player.getAttributes().get("serve") >= serveBefore + 1,
                "Serve should increase by at least 1");
    }

    @Test
    void defensiveCoachShouldImproveBlockAndReceive() {
        VolleyballPlayer player = new VolleyballPlayer("Player2", "MIDDLE_BLOCKER");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int blockBefore   = player.getAttributes().get("block");
        int receiveBefore = player.getAttributes().get("receive");

        VolleyballCoach coach = new VolleyballCoach("CoachB", "DEFENSIVE");
        coach.trainPlayers(players);

        assertTrue(player.getAttributes().get("block") >= blockBefore + 2,
                "Block should increase by at least 2");
        assertTrue(player.getAttributes().get("receive") >= receiveBefore + 1,
                "Receive should increase by at least 1");
    }

    @Test
    void balancedCoachShouldImproveServeAndReceive() {
        VolleyballPlayer player = new VolleyballPlayer("Player3", "LIBERO");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int serveBefore   = player.getAttributes().get("serve");
        int receiveBefore = player.getAttributes().get("receive");

        VolleyballCoach coach = new VolleyballCoach("CoachC", "BALANCED");
        coach.trainPlayers(players);

        assertTrue(player.getAttributes().get("serve") >= serveBefore + 1,
                "Serve should increase by at least 1");
        assertTrue(player.getAttributes().get("receive") >= receiveBefore + 1,
                "Receive should increase by at least 1");
    }

    @Test
    void injuredPlayersShouldNotBeTrained() {
        VolleyballPlayer player = new VolleyballPlayer("Player4", "SETTER");
        player.setInjured(3);

        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int spikeBefore = player.getAttributes().get("spike");
        int serveBefore = player.getAttributes().get("serve");

        VolleyballCoach coach = new VolleyballCoach("CoachD", "ATTACKING");
        coach.trainPlayers(players);

        // Sakatlar antrenman almaz — nitelikler değişmemeli
        assertEquals(spikeBefore, player.getAttributes().get("spike"));
        assertEquals(serveBefore, player.getAttributes().get("serve"));
    }

    @Test
    void nullPlayerListShouldNotThrow() {
        VolleyballCoach coach = new VolleyballCoach("CoachE", "BALANCED");
        assertDoesNotThrow(() -> coach.trainPlayers(null));
    }
}
