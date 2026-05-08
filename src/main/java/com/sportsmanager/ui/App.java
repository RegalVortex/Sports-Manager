package com.sportsmanager.ui;

import com.sportsmanager.core.*;
import com.sportsmanager.setup.GameSetupService;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.setup.PresetData;
import com.sportsmanager.ui.scenes.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager manager = SceneManager.getInstance();
        manager.initialize(primaryStage);

        SportRegistry registry = new SportRegistry();
        manager.setRegistry(registry);

        // Sahneleri kaydet
        manager.register("mainmenu",    MainMenuScene::create);
        manager.register("sportselect", SportSelectScene::create);
        manager.register("leagueselect", LeagueSelectScene::create);
        manager.register("teamselect",  TeamSelectScene::create);

        // "newgame" — sadece takım seçilince bir kez çalışır, oyunu kurar
        manager.register("newgame", () -> {
            setupGame(manager, registry);
            return DashboardScene.create();
        });

        // "dashboard" — haftayı ilerletince tekrar render eder, setupGame ÇALIŞMAZ
        manager.register("dashboard", DashboardScene::create);

        // Alt ekranlar
        manager.register("squad",     SquadScene::create);
        manager.register("league",    LeagueScene::create);
        manager.register("match",     MatchScene::create);
        manager.register("seasonend", SeasonEndScene::create);

        manager.navigateTo("mainmenu");
    }

    private void setupGame(SceneManager manager, SportRegistry registry) {
        String sport      = manager.getSelectedSport();
        String leagueName = manager.getSelectedLeague();
        String teamName   = manager.getSelectedTeam();

        GameSetupService setupService = new GameSetupService(registry);

        List<LeaguePreset> leagues = PresetData.getLeaguesForSport(sport);
        LeaguePreset selectedPreset = null;
        for (LeaguePreset lp : leagues) {
            if (lp.getLeagueName().equals(leagueName)) {
                selectedPreset = lp;
                break;
            }
        }

        if (selectedPreset == null) return;

        GameSetupService.SetupResult result =
                setupService.createGame(sport, selectedPreset, teamName);

        ISport  currentSport  = result.getSport();
        ILeague currentLeague = result.getLeague();
        ITeam   playerTeam    = result.getPlayerTeam();

        manager.setCurrentSport(currentSport);
        manager.setCurrentLeague(currentLeague);
        manager.setCurrentPlayerTeam(playerTeam);

        GameContext.getInstance().startNewGame(currentSport);
        GameContext.getInstance().setLeague(currentLeague);
        GameContext.getInstance().setPlayerTeam(playerTeam);
    }

    public static void main(String[] args) {
        launch(args);
    }
}