package com.sportsmanager.ui.scenes;

import com.sportsmanager.ui.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainMenuScene {

    public static Scene create() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0D0D0D;");
        root.setPadding(new Insets(60, 30, 40, 30));

        // Logo alanı
        Label logo = new Label("SPORTS\nMANAGER");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        logo.setTextFill(Color.web("#F0A500"));
        logo.setAlignment(Pos.CENTER);
        logo.setStyle("-fx-text-alignment: center;");

        Label subtitle = new Label("Championship Edition");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#8A8A9A"));

        VBox logoBox = new VBox(8, logo, subtitle);
        logoBox.setAlignment(Pos.CENTER);
        VBox.setMargin(logoBox, new Insets(0, 0, 40, 0));

        // Menü butonları
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        HBox newGameBtn = createMenuButton("⚽  New Game", "Start a new career", true);
        HBox loadGameBtn = createMenuButton("💾  Load Game", "Continue saved game", false);
        HBox exitBtn = createMenuButton("✕  Exit", "Quit the game", false);

        newGameBtn.setOnMouseClicked(e ->
                SceneManager.getInstance().navigateTo("sportselect")
        );

        exitBtn.setOnMouseClicked(e ->
                javafx.application.Platform.exit()
        );

        menuBox.getChildren().addAll(newGameBtn, loadGameBtn, exitBtn);

        // Alt yazı
        Label footer = new Label("© 2025 RegalVortex");
        footer.setFont(Font.font("Arial", 12));
        footer.setTextFill(Color.web("#444455"));
        VBox.setMargin(footer, new Insets(40, 0, 0, 0));

        root.getChildren().addAll(logoBox, menuBox, footer);

        return new Scene(root, 480, 850);
    }

    private static HBox createMenuButton(String title, String subtitle, boolean highlighted) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: #1E2332;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        card.setMaxWidth(380);
        card.setMinWidth(380);

        VBox textBox = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(highlighted ? Color.web("#F0A500") : Color.WHITE);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#8A8A9A"));

        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label arrow = new Label("›");
        arrow.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        arrow.setTextFill(Color.web("#F0A500"));

        card.getChildren().addAll(textBox, arrow);

        // Hover efekti
        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: #252B3D;" +
                                "-fx-background-radius: 12;" +
                                "-fx-cursor: hand;"
                )
        );
        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: #1E2332;" +
                                "-fx-background-radius: 12;" +
                                "-fx-cursor: hand;"
                )
        );

        return card;
    }
}
