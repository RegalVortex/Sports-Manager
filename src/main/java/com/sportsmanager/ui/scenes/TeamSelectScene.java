package com.sportsmanager.ui.scenes;

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

public class TeamSelectScene {

    public static Scene create() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0D0D0D;");

        VBox header = createHeader();

        String sport = SceneManager.getInstance().getSelectedSport();
        String leagueName = SceneManager.getInstance().getSelectedLeague();

        List<LeaguePreset> leagues = PresetData.getLeaguesForSport(sport);
        LeaguePreset selectedLeague = null;
        for (LeaguePreset lp : leagues) {
            if (lp.getLeagueName().equals(leagueName)) {
                selectedLeague = lp;
                break;
            }
        }

        VBox listBox = new VBox(12);
        listBox.setPadding(new Insets(20, 30, 30, 30));

        Label selectLabel = new Label("Select Your Team:");
        selectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        selectLabel.setTextFill(Color.web("#F0A500"));
        listBox.getChildren().add(selectLabel);

        if (selectedLeague != null) {
            for (String teamName : selectedLeague.getTeamNames()) {
                HBox card = createTeamCard(teamName);
                listBox.getChildren().add(card);
            }
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
                SceneManager.getInstance().navigateTo("leagueselect")
        );

        Label titleLabel = new Label("TEAM SELECTION");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#F0A500"));

        topRow.getChildren().addAll(backBtn, titleLabel);

        Label subtitleLabel = new Label("Choose the team you want to manage");
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

    private static HBox createTeamCard(String teamName) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: #1E2332;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;"
        );
        card.setMaxWidth(Double.MAX_VALUE);

        // Takım baş harfi ikonu
        Label iconLabel = new Label(String.valueOf(teamName.charAt(0)));
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        iconLabel.setTextFill(Color.web("#F0A500"));
        iconLabel.setStyle(
                "-fx-background-color: #2A2F45;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;"
        );

        VBox textBox = new VBox(4);
        Label nameLabel = new Label(teamName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.WHITE);

        textBox.getChildren().add(nameLabel);
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
            SceneManager.getInstance().setSelectedTeam(teamName);
            SceneManager.getInstance().navigateTo("newgame");
        });

        return card;
    }
}