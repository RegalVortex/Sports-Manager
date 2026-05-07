package com.sportsmanager.ui;

import com.sportsmanager.ui.scenes.MainMenuScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {


    @Override
    public void start(Stage primaryStage) {
        SceneManager manager = SceneManager.getInstance();
        manager.initialize(primaryStage);

        // Sahneleri kaydet
        manager.register("mainmenu", MainMenuScene::create);

        // Başlangıç sahnesi
        manager.navigateTo("mainmenu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
