package com.sportsmanager.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.sportsmanager.core.SportRegistry;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, Supplier<Scene>> sceneRegistry = new HashMap<>();
    private String selectedSport;
    private String selectedLeague;
    private String selectedTeam;
    private SportRegistry registry;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setRegistry(SportRegistry registry) { this.registry = registry; }
    public SportRegistry getRegistry() { return registry; }

    public void initialize(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Sports Manager");
        this.primaryStage.setWidth(480);
        this.primaryStage.setHeight(850);
        this.primaryStage.setMinWidth(360);
        this.primaryStage.setMinHeight(600);
        this.primaryStage.setResizable(true);
        // Büyük ekranlarda da düzgün görünmesi için maksimum genişliği serbest bırak
        // İçerik zaten maxWidth=600 ile ortalanmış; arka plan rengi doldurur
        this.primaryStage.setMaxWidth(Double.MAX_VALUE);
        this.primaryStage.setMaxHeight(Double.MAX_VALUE);
    }

    public void register(String name, Supplier<Scene> sceneSupplier) {
        sceneRegistry.put(name, sceneSupplier);
    }

    public void navigateTo(String name) {
        Supplier<Scene> supplier = sceneRegistry.get(name);
        if (supplier == null) {
            System.out.println("Scene not found: " + name);
            return;
        }
        // Mevcut boyut/durum kaydet — setScene sonrası geri yükle
        boolean isShowing    = primaryStage.isShowing();
        boolean wasMaximized = isShowing && primaryStage.isMaximized();
        double  currentW     = isShowing ? primaryStage.getWidth()  : 480;
        double  currentH     = isShowing ? primaryStage.getHeight() : 850;

        Scene scene = supplier.get();
        primaryStage.setScene(scene);

        if (wasMaximized) {
            // Maximize durumunu koru (setScene bunu sıfırlayabilir)
            primaryStage.setMaximized(true);
        } else {
            primaryStage.setWidth(currentW);
            primaryStage.setHeight(currentH);
        }
        primaryStage.show();
    }

    public Stage getStage() { return primaryStage; }

    public void setSelectedSport(String sport) { this.selectedSport = sport; }
    public String getSelectedSport() { return selectedSport; }

    public void setSelectedLeague(String league) { this.selectedLeague = league; }
    public String getSelectedLeague() { return selectedLeague; }

    private com.sportsmanager.core.ISport currentSport;
    private com.sportsmanager.core.ILeague currentLeague;
    private com.sportsmanager.core.ITeam currentPlayerTeam;

    public void setCurrentSport(com.sportsmanager.core.ISport sport) { this.currentSport = sport; }
    public com.sportsmanager.core.ISport getCurrentSport() { return currentSport; }

    public void setCurrentLeague(com.sportsmanager.core.ILeague league) { this.currentLeague = league; }
    public com.sportsmanager.core.ILeague getCurrentLeague() { return currentLeague; }

    public void setCurrentPlayerTeam(com.sportsmanager.core.ITeam team) { this.currentPlayerTeam = team; }
    public com.sportsmanager.core.ITeam getCurrentPlayerTeam() { return currentPlayerTeam; }

    public void setSelectedTeam(String team) { this.selectedTeam = team; }
    public String getSelectedTeam() { return selectedTeam; }
}