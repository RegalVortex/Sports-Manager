package com.sportsmanager.save;

import com.sportsmanager.core.ILeague;
import com.sportsmanager.core.IPlayer;
import com.sportsmanager.core.ISport;
import com.sportsmanager.core.ITeam;
import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.sport.football.FootballSportFactory;
import com.sportsmanager.sport.football.FootballTactic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveLoadServiceTest {

    @TempDir
    File tempDir;

    /**
     * Ana test: taktik adı kaydedilip yüklenince aynı kalmalı.
     * Hata: Eski SaveLoadService her zaman factory.createDefaultTactic() atıyordu.
     * Düzeltme: resolveTactic() adlı yardımcı, kayıtlı adı factory listesinde arar.
     */
    @Test
    void tacticNameShouldBeRestoredAfterSaveAndLoad() {
        FootballSportFactory factory = new FootballSportFactory();
        ISport sport = factory.createSport();

        // İki takım oluştur (lig en az 2 takım gerektirir)
        ITeam teamA = factory.createTeam("Galatasaray", "gala.png");
        ITeam teamB = factory.createTeam("Fenerbahçe",  "fb.png");

        // Takım A'ya özel taktik ata
        teamA.setTactic(new FootballTactic("4-3-3"));

        List<ITeam> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        ILeague league = factory.createLeague("Süper Lig", teams);

        String savePath = tempDir.getAbsolutePath() + File.separator + "test_save.dat";

        // Kaydet
        SaveLoadService.saveGame(savePath, sport, league, teamA);
        assertTrue(new File(savePath).exists(), "Save file should have been created");

        // Yükle
        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded, "LoadedGame should not be null");
        assertNotNull(loaded.getPlayerTeam(), "Player team should not be null");

        // Taktik adı korunmuş olmalı
        String restoredTactic = loaded.getPlayerTeam().getTactic().getName();
        assertEquals("4-3-3", restoredTactic,
                "Tactic name should be restored to '4-3-3' after loading, but was: " + restoredTactic);
    }

    @Test
    void defaultTacticShouldBeUsedWhenTacticNameIsEmpty() {
        FootballSportFactory factory = new FootballSportFactory();
        ISport sport = factory.createSport();

        ITeam teamA = factory.createTeam("Beşiktaş",    "bjk.png");
        ITeam teamB = factory.createTeam("Trabzonspor", "ts.png");

        // Taktik ayarlanmadan bırak (varsayılan 4-4-2 kullanılır)
        List<ITeam> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        ILeague league = factory.createLeague("Test Lig", teams);

        String savePath = tempDir.getAbsolutePath() + File.separator + "test_default.dat";

        SaveLoadService.saveGame(savePath, sport, league, teamA);

        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded);
        assertNotNull(loaded.getPlayerTeam().getTactic(),
                "A default tactic should always be set after loading");
    }

    @Test
    void loadedLeagueShouldContainSameNumberOfTeams() {
        FootballSportFactory factory = new FootballSportFactory();
        ISport sport = factory.createSport();

        List<ITeam> teams = new ArrayList<>();
        teams.add(factory.createTeam("Team1", "t1.png"));
        teams.add(factory.createTeam("Team2", "t2.png"));
        teams.add(factory.createTeam("Team3", "t3.png"));
        teams.add(factory.createTeam("Team4", "t4.png"));

        ILeague league = factory.createLeague("Four Team League", teams);

        String savePath = tempDir.getAbsolutePath() + File.separator + "test_teams.dat";
        SaveLoadService.saveGame(savePath, sport, league, teams.get(0));

        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded);
        assertEquals(4, loaded.getLeague().getTeams().size(),
                "Loaded league should have the same number of teams");
    }

    @Test
    void loadFromNonExistentFileShouldReturnNull() {
        SportRegistry registry = new SportRegistry();
        LoadedGame result = SaveLoadService.loadGame("/nonexistent/path/game.dat", registry);
        assertNull(result, "Loading from a missing file should return null");
    }

    @Test
    void volleyballTacticShouldAlsoBeRestoredCorrectly() {
        com.sportsmanager.sport.volleyball.VolleyballSportFactory factory =
                new com.sportsmanager.sport.volleyball.VolleyballSportFactory();
        ISport sport = factory.createSport();

        ITeam teamA = factory.createTeam("VakıfBank", "vakif.png");
        ITeam teamB = factory.createTeam("Eczacıbaşı", "ecza.png");

        teamA.setTactic(new com.sportsmanager.sport.volleyball.VolleyballTactic("DEFENSIVE"));

        List<ITeam> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        ILeague league = factory.createLeague("Efeler Ligi", teams);

        String savePath = tempDir.getAbsolutePath() + File.separator + "test_vball.dat";
        SaveLoadService.saveGame(savePath, sport, league, teamA);

        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded);
        String restoredTactic = loaded.getPlayerTeam().getTactic().getName();
        assertEquals("DEFENSIVE", restoredTactic,
                "Volleyball tactic DEFENSIVE should be restored, but was: " + restoredTactic);
    }

    @Test
    void matchesPlayedShouldBeRestoredForPlayers() {
        FootballSportFactory factory = new FootballSportFactory();
        ISport sport = factory.createSport();

        ITeam teamA = factory.createTeam("Galatasaray", "gala.png");
        ITeam teamB = factory.createTeam("Fenerbahçe",  "fb.png");

        // Manually advance matchesPlayed on all players of teamA
        for (IPlayer p : teamA.getSquad()) {
            p.incrementMatchesPlayed();
            p.incrementMatchesPlayed();  // simulate 2 matches
        }

        List<ITeam> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        ILeague league = factory.createLeague("Test Lig", teams);
        String savePath = tempDir.getAbsolutePath() + File.separator + "test_matches_played.dat";
        SaveLoadService.saveGame(savePath, sport, league, teamA);

        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded);
        ITeam restoredTeam = loaded.getPlayerTeam();
        for (IPlayer p : restoredTeam.getSquad()) {
            assertEquals(2, p.getMatchesPlayed(),
                    "matchesPlayed should be 2 after restore for player: " + p.getName());
        }
    }

    @Test
    void currentSeasonShouldBeRestoredAfterSaveAndLoad() {
        FootballSportFactory factory = new FootballSportFactory();
        ISport sport = factory.createSport();

        ITeam teamA = factory.createTeam("Beşiktaş",    "bjk.png");
        ITeam teamB = factory.createTeam("Trabzonspor", "ts.png");

        List<ITeam> teams = new ArrayList<>();
        teams.add(teamA);
        teams.add(teamB);

        ILeague league = factory.createLeague("Test Lig", teams);

        // Simulate being in season 3
        league.resetSeason(); // season 2
        league.resetSeason(); // season 3

        String savePath = tempDir.getAbsolutePath() + File.separator + "test_season.dat";
        SaveLoadService.saveGame(savePath, sport, league, teamA);

        SportRegistry registry = new SportRegistry();
        LoadedGame loaded = SaveLoadService.loadGame(savePath, registry);

        assertNotNull(loaded);
        assertEquals(3, loaded.getLeague().getCurrentSeason(),
                "currentSeason should be 3 after restore");
    }
}
