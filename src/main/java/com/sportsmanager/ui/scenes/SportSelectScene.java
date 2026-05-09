package com.sportsmanager.ui.scenes;

import com.sportsmanager.ui.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SportSelectScene {

    private static final int MAX_W = 600;

    public static Scene create() {
        // Header
        VBox header = createHeader("SPOR SEÇİMİ", "Yönetmek istediğin sporu seç");

        // İçerik
        VBox body = new VBox(20);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(40, 30, 30, 30));

        HBox footballBtn = createSportCard("⚽", "Futbol", "11'e karşı 11 klasik futbol", "football");
        HBox volleyballBtn = createSportCard("🏐", "Voleybol", "6'ya karşı 6 voleybol", "volleyball");

        body.getChildren().addAll(footballBtn, volleyballBtn);

        VBox root = new VBox(0, header, body);
        root.setStyle("-fx-background-color: #0D0D0D;");
        VBox.setVgrow(body, Priority.ALWAYS);

        BorderPane content = new BorderPane(root);
        content.setStyle("-fx-background-color: #0D0D0D;");
        content.setMaxWidth(MAX_W);

        StackPane outer = new StackPane(content);
        outer.setStyle("-fx-background-color: #0D0D0D;");
        StackPane.setAlignment(content, Pos.TOP_CENTER);

        return new Scene(outer, 480, 850);
    }

    private static VBox createHeader(String title, String subtitle) {
        VBox header = new VBox(6);
        header.setPadding(new Insets(50, 30, 20, 30));
        header.setStyle("-fx-background-color: #0D0D0D;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label backBtn = new Label("←");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        backBtn.setTextFill(Color.web("#F0A500"));
        backBtn.setStyle("-fx-cursor: hand;");
        backBtn.setOnMouseClicked(e ->
            SceneManager.getInstance().navigateTo("mainmenu")
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#F0A500"));

        topRow.getChildren().addAll(backBtn, titleLabel);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#8A8A9A"));
        subtitleLabel.setPadding(new Insets(0, 0, 0, 32));

        // Separator çizgisi
        Region separator = new Region();
        separator.setMinHeight(1);
        separator.setStyle("-fx-background-color: #F0A500;");
        VBox.setMargin(separator, new Insets(10, 0, 0, 0));

        header.getChildren().addAll(topRow, subtitleLabel, separator);
        return header;
    }

    private static HBox createSportCard(String icon, String title, String subtitle, String sportKey) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(22, 20, 22, 20));
        card.setStyle(
            "-fx-background-color: #1E2332;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        );
        card.setMaxWidth(Double.MAX_VALUE);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(28));
        iconLabel.setStyle(
            "-fx-background-color: #2A2F45;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10;"
        );

        VBox textBox = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        titleLabel.setTextFill(Color.web("#F0A500"));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setTextFill(Color.web("#8A8A9A"));

        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label arrow = new Label("›");
        arrow.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        arrow.setTextFill(Color.web("#F0A500"));

        card.getChildren().addAll(iconLabel, textBox, arrow);

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #252B3D;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: #1E2332;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;"
        ));

        card.setOnMouseClicked(e -> {
            SceneManager.getInstance().setSelectedSport(sportKey);
            SceneManager.getInstance().navigateTo("leagueselect");
        });

        return card;
    }
}
