package com.sportsmanager.ui;

import com.sportsmanager.ui.modern.ModernSportsManagerUi;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        new ModernSportsManagerUi().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
