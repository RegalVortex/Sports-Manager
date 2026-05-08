package com.sportsmanager.ui;

import com.sportsmanager.ui.scenes.LeagueSelectScene;
import com.sportsmanager.ui.scenes.MainMenuScene;
import javafx.application.Application;
import javafx.stage.Stage;
import com.sportsmanager.ui.scenes.SportSelectScene;

public class App extends Application {


    @Override
    public void start(Stage primaryStage) {
        SceneManager manager = SceneManager.getInstance();
        manager.initialize(primaryStage);

       
        manager.register("mainmenu", MainMenuScene::create);

        manager.register("sportselect", SportSelectScene::create);

        manager.register("leagueselect", LeagueSelectScene::create);
        manager.navigateTo("mainmenu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
