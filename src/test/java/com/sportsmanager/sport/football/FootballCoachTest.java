package com.sportsmanager.sport.football;

import com.sportsmanager.core.IPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FootballCoachTest {

    @Test
    void shouldReturnNameCorrectly() {
        FootballCoach coach = new FootballCoach("Pep", "ATTACKING");
        assertEquals("Pep", coach.getName());
    }

    @Test
    void shouldReturnSpecialtyCorrectly() {
        FootballCoach coach = new FootballCoach("Mourinho", "DEFENSIVE");
        assertEquals("DEFENSIVE", coach.getSpecialty());
    }

    @Test
    void qualityShouldBeInValidRange() {
        // Quality artık rastgele; 1-10 arasında olduğu doğrulanır.
        for (int i = 0; i < 20; i++) {
            FootballCoach coach = new FootballCoach("TestCoach", "ATTACKING");
            int q = coach.getQuality();
            assertTrue(q >= 1 && q <= 10,
                    "Quality must be 1-10, was: " + q);
        }
    }

    @Test
    void attackingCoachShouldImproveShootingAndPace() {
        FootballPlayer player = new FootballPlayer("Player1", "ST");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int shootingBefore = player.getAttributes().get("shooting");
        int paceBefore     = player.getAttributes().get("pace");

        FootballCoach coach = new FootballCoach("CoachA", "ATTACKING");
        coach.trainPlayers(players);

        // Bonus = quality/5 (0-2); minimum artış shooting+2, pace+1
        assertTrue(player.getAttributes().get("shooting") >= shootingBefore + 2,
                "Shooting should increase by at least 2");
        assertTrue(player.getAttributes().get("pace") >= paceBefore + 1,
                "Pace should increase by at least 1");
    }

    @Test
    void defensiveCoachShouldImproveDefendingAndHeading() {
        FootballPlayer player = new FootballPlayer("Player2", "CB");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int defendingBefore = player.getAttributes().get("defending");
        int headingBefore   = player.getAttributes().get("heading");

        FootballCoach coach = new FootballCoach("CoachB", "DEFENSIVE");
        coach.trainPlayers(players);

        assertTrue(player.getAttributes().get("defending") >= defendingBefore + 2,
                "Defending should increase by at least 2");
        assertTrue(player.getAttributes().get("heading") >= headingBefore + 1,
                "Heading should increase by at least 1");
    }

    @Test
    void fitnessCoachShouldImproveStamina() {
        FootballPlayer player = new FootballPlayer("Player3", "CM");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int staminaBefore = player.getAttributes().get("stamina");

        FootballCoach coach = new FootballCoach("CoachC", "FITNESS");
        coach.trainPlayers(players);

        assertTrue(player.getAttributes().get("stamina") >= staminaBefore + 2,
                "Stamina should increase by at least 2");
    }

    @Test
    void injuredPlayersShouldNotBeTrained() {
        FootballPlayer player = new FootballPlayer("Player4", "LW");
        player.setInjured(3);

        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int shootingBefore = player.getAttributes().get("shooting");
        int paceBefore     = player.getAttributes().get("pace");

        FootballCoach coach = new FootballCoach("CoachD", "ATTACKING");
        coach.trainPlayers(players);

        // Sakatlar antrenman almaz — nitelikler değişmemeli
        assertEquals(shootingBefore, player.getAttributes().get("shooting"));
        assertEquals(paceBefore,     player.getAttributes().get("pace"));
    }
}
