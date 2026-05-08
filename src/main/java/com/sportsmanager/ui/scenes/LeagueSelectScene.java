package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.SportRegistry;
import com.sportsmanager.setup.LeaguePreset;
import com.sportsmanager.setup.PresetData;
import com.sportsmanager.ui.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class LeagueSelectScene {

    public static Scene create() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0D0D0D;");

        VBox header = createHeader();

        String sport = SceneManager.getInstance().getSelectedSport();
        List<LeaguePreset> leagues = PresetData.getLeaguesForSport(sport);

        VBox listBox = new VBox(15);
        listBox.setPadding(new Insets(20, 30, 30, 30));

        for (LeaguePreset league : leagues) {
            HBox card = createLeagueCard(league);
            listBox.getChildren().add(card);
        }

        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0D0D0D; -fx-background-color: #0D0D0D;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, scrollPane);
        return new Scene(root);
    }

    private static VBox createHeader() {
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
                SceneManager.getInstance().navigateTo("sportselect")
        );

        Label titleLabel = new Label("LEAGUE SELECTION");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#F0A500"));

        topRow.getChildren().addAll(backBtn, titleLabel);

        Label subtitleLabel = new Label("Choose a league to start your career");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#8A8A9A"));
        subtitleLabel.setPadding(new Insets(0, 0, 0, 32));

        Region separator = new Region();
        separator.setMinHeight(1);
        separator.setStyle("-fx-background-color: #F0A500;");
        VBox.setMargin(separator, new Insets(10, 0, 0, 0));

        header.getChildren().addAll(topRow, subtitleLabel, separator);
        return header;
    }

    private static HBox createLeagueCard(LeaguePreset league) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: #1E2332;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        card.setMaxWidth(Double.MAX_VALUE);

        Label iconLabel = new Label("🏆");
        iconLabel.setFont(Font.font(24));
        iconLabel.setStyle(
                "-fx-background-color: #2A2F45;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;"
        );

        VBox textBox = new VBox(4);
        Label nameLabel = new Label(league.getLeagueName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.web("#F0A500"));

        Label countLabel = new Label(league.getTeamNames().size() + " Teams");
        countLabel.setFont(Font.font("Arial", 12));
        countLabel.setTextFill(Color.web("#8A8A9A"));

        textBox.getChildren().addAll(nameLabel, countLabel);
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
            SceneManager.getInstance().setSelectedLeague(league.getLeagueName());
            SceneManager.getInstance().navigateTo("teamselect");
        });

        return card;
    }
}