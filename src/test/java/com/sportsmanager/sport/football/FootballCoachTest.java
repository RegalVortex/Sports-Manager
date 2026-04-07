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
    void attackingCoachShouldImproveShootingAndPace() {
        FootballPlayer player = new FootballPlayer("Player1", "ST");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int shootingBefore = player.getAttributes().get("shooting");
        int paceBefore = player.getAttributes().get("pace");

        FootballCoach coach = new FootballCoach("CoachA", "ATTACKING");
        coach.trainPlayers(players);

        assertEquals(shootingBefore + 2, player.getAttributes().get("shooting"));
        assertEquals(paceBefore + 1, player.getAttributes().get("pace"));
    }

    @Test
    void defensiveCoachShouldImproveDefendingAndHeading() {
        FootballPlayer player = new FootballPlayer("Player2", "CB");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int defendingBefore = player.getAttributes().get("defending");
        int headingBefore = player.getAttributes().get("heading");

        FootballCoach coach = new FootballCoach("CoachB", "DEFENSIVE");
        coach.trainPlayers(players);

        assertEquals(defendingBefore + 2, player.getAttributes().get("defending"));
        assertEquals(headingBefore + 1, player.getAttributes().get("heading"));
    }

    @Test
    void fitnessCoachShouldImproveStamina() {
        FootballPlayer player = new FootballPlayer("Player3", "CM");
        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int staminaBefore = player.getAttributes().get("stamina");

        FootballCoach coach = new FootballCoach("CoachC", "FITNESS");
        coach.trainPlayers(players);

        assertEquals(staminaBefore + 2, player.getAttributes().get("stamina"));
    }
@Test
    void injuredPlayersShouldNotBeTrained() {
        FootballPlayer player = new FootballPlayer("Player4", "LW");
        player.setInjured(3);

        List<IPlayer> players = new ArrayList<>();
        players.add(player);

        int shootingBefore = player.getAttributes().get("shooting");
        int paceBefore = player.getAttributes().get("pace");

        FootballCoach coach = new FootballCoach("CoachD", "ATTACKING");
        coach.trainPlayers(players);

        assertEquals(shootingBefore, player.getAttributes().get("shooting"));
        assertEquals(paceBefore, player.getAttributes().get("pace"));
    }
}
