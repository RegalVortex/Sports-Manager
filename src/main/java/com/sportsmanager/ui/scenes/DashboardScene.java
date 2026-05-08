package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.*;
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

public class DashboardScene {

    public static Scene create() {
        ILeague league = SceneManager.getInstance().getCurrentLeague();
        ITeam playerTeam = SceneManager.getInstance().getCurrentPlayerTeam();

        VBox root = new VBox();
        root.setStyle("-fx-background-color: #0D0D0D;");

        root.getChildren().addAll(
                createTopBar(league, playerTeam),
                createTeamHeader(league, playerTeam),
                createTabBar(),
                createScrollContent(league, playerTeam)
        );

        return new Scene(root, 480, 850);
    }

    private static HBox createTopBar(ILeague league, ITeam playerTeam) {
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 15, 12, 15));
        topBar.setStyle("-fx-background-color: #161B2E;");

        Label weekLabel = new Label("Week " + league.getCurrentWeek());
        weekLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        weekLabel.setTextFill(Color.web("#F0A500"));
        weekLabel.setStyle(
                "-fx-background-color: #1E2332;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 12 6 12;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label advanceBtn = new Label("▶  Advance");
        advanceBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        advanceBtn.setTextFill(Color.web("#0D0D0D"));
        advanceBtn.setStyle(
                "-fx-background-color: #F0A500;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 14 6 14;" +
                        "-fx-cursor: hand;"
        );
        advanceBtn.setOnMouseClicked(e -> {
            if (!league.isSeasonOver()) {
                league.advanceWeek();
                SceneManager.getInstance().navigateTo("dashboard");
            } else {
                SceneManager.getInstance().navigateTo("seasonend");
            }
        });

        topBar.getChildren().addAll(weekLabel, spacer, advanceBtn);
        return topBar;
    }

    private static VBox createTeamHeader(ILeague league, ITeam playerTeam) {
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 15, 15, 15));
        header.setStyle("-fx-background-color: #161B2E;");

        Label teamName = new Label(playerTeam.getName());
        teamName.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        teamName.setTextFill(Color.web("#F0A500"));

        int rank = league.getStandings().getRankOf(playerTeam);
        Label rankLabel = new Label(rank + ". in " + league.getName());
        rankLabel.setFont(Font.font("Arial", 13));
        rankLabel.setTextFill(Color.WHITE);
        rankLabel.setStyle(
                "-fx-background-color: #2A2F45;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4 10 4 10;"
        );

        header.getChildren().addAll(teamName, rankLabel);
        return header;
    }

    private static HBox createTabBar() {
        HBox tabBar = new HBox();
        tabBar.setStyle("-fx-background-color: #161B2E;");

        String[] tabs = {"Home", "Squad", "League"};
        String[] keys = {"dashboard", "squad", "league"};

        for (int i = 0; i < tabs.length; i++) {
            final String key = keys[i];
            Label tab = new Label(tabs[i]);
            tab.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            tab.setPadding(new Insets(12, 0, 12, 0));
            HBox.setHgrow(tab, Priority.ALWAYS);
            tab.setMaxWidth(Double.MAX_VALUE);
            tab.setAlignment(Pos.CENTER);

            if (i == 0) {
                tab.setTextFill(Color.web("#F0A500"));
                tab.setStyle(
                        "-fx-border-color: transparent transparent #F0A500 transparent;" +
                                "-fx-border-width: 0 0 2 0;" +
                                "-fx-cursor: hand;"
                );
            } else {
                tab.setTextFill(Color.web("#8A8A9A"));
                tab.setStyle("-fx-cursor: hand;");
            }

            tab.setOnMouseClicked(e ->
                    SceneManager.getInstance().navigateTo(key)
            );

            tabBar.getChildren().add(tab);
        }

        return tabBar;
    }

    private static ScrollPane createScrollContent(ILeague league, ITeam playerTeam) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #0D0D0D;");

        content.getChildren().addAll(
                createInfoCard(league, playerTeam),
                createNextMatchCard(league, playerTeam),
                createStandingsSummary(league, playerTeam)
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0D0D0D; -fx-background-color: #0D0D0D;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }

    private static HBox createInfoCard(ILeague league, ITeam playerTeam) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1E2332; -fx-background-radius: 12;");

        VBox seasonBox = createStatBox("Season",
                String.valueOf(GameContext.getInstance().getCurrentSeason()));
        VBox weekBox = createStatBox("Week",
                String.valueOf(league.getCurrentWeek()));
        VBox pointsBox = createStatBox("Points",
                String.valueOf(playerTeam.getPoints()));
        VBox rankBox = createStatBox("Rank",
                league.getStandings().getRankOf(playerTeam) + "/" + league.getTeams().size());

        for (VBox box : new VBox[]{seasonBox, weekBox, pointsBox, rankBox}) {
            HBox.setHgrow(box, Priority.ALWAYS);
        }

        card.getChildren().addAll(seasonBox, weekBox, pointsBox, rankBox);
        return card;
    }

    private static VBox createStatBox(String label, String value) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web("#F0A500"));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 11));
        nameLabel.setTextFill(Color.web("#8A8A9A"));

        box.getChildren().addAll(valueLabel, nameLabel);
        return box;
    }

    private static VBox createNextMatchCard(ILeague league, ITeam playerTeam) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1E2332; -fx-background-radius: 12;");

        Label title = new Label("NEXT MATCH");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#8A8A9A"));

        List<IMatch> upcoming = league.getFixturesForWeek(league.getCurrentWeek());
        ITeam opponent = null;
        boolean isHome = false;

        for (IMatch match : upcoming) {
            if (match.getHomeTeam().equals(playerTeam)) {
                opponent = match.getAwayTeam();
                isHome = true;
                break;
            } else if (match.getAwayTeam().equals(playerTeam)) {
                opponent = match.getHomeTeam();
                break;
            }
        }

        if (opponent != null) {
            HBox matchRow = new HBox(10);
            matchRow.setAlignment(Pos.CENTER);

            Label homeLabel = new Label(isHome ? playerTeam.getName() : opponent.getName());
            homeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            homeLabel.setTextFill(Color.WHITE);
            HBox.setHgrow(homeLabel, Priority.ALWAYS);
            homeLabel.setAlignment(Pos.CENTER_RIGHT);

            Label vsLabel = new Label("VS");
            vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            vsLabel.setTextFill(Color.web("#F0A500"));

            Label awayLabel = new Label(isHome ? opponent.getName() : playerTeam.getName());
            awayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            awayLabel.setTextFill(Color.WHITE);
            HBox.setHgrow(awayLabel, Priority.ALWAYS);

            matchRow.getChildren().addAll(homeLabel, vsLabel, awayLabel);

            Label locationLabel = new Label(isHome ? "Home" : "Away");
            locationLabel.setFont(Font.font("Arial", 12));
            locationLabel.setTextFill(Color.web("#8A8A9A"));
            locationLabel.setMaxWidth(Double.MAX_VALUE);
            locationLabel.setAlignment(Pos.CENTER);

            HBox ovrRow = new HBox(10);
            ovrRow.setAlignment(Pos.CENTER);

            Label yourOvr = new Label("Your OVR: " + playerTeam.getTeamOverallRating());
            yourOvr.setTextFill(Color.web("#F0A500"));
            yourOvr.setFont(Font.font("Arial", FontWeight.BOLD, 13));

            Label oppOvr = new Label("Opp OVR: " + opponent.getTeamOverallRating());
            oppOvr.setTextFill(Color.web("#8A8A9A"));
            oppOvr.setFont(Font.font("Arial", FontWeight.BOLD, 13));

            ovrRow.getChildren().addAll(yourOvr, oppOvr);
            card.getChildren().addAll(title, matchRow, locationLabel, ovrRow);
        } else {
            Label noMatch = new Label("No match this week");
            noMatch.setTextFill(Color.web("#8A8A9A"));
            card.getChildren().addAll(title, noMatch);
        }

        return card;
    }

    private static VBox createStandingsSummary(ILeague league, ITeam playerTeam) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #1E2332; -fx-background-radius: 12;");

        Label title = new Label("STANDINGS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#8A8A9A"));
        card.getChildren().add(title);

        int rank = 1;
        for (ITeam team : league.getStandings().getTeams()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 8, 6, 8));

            boolean isPlayer = team.equals(playerTeam);
            if (isPlayer) {
                row.setStyle("-fx-background-color: #252B3D; -fx-background-radius: 8;");
            }

            Label rankLbl = new Label(rank + ".");
            rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            rankLbl.setTextFill(isPlayer ? Color.web("#F0A500") : Color.web("#8A8A9A"));
            rankLbl.setMinWidth(25);

            Label nameLbl = new Label(team.getName());
            nameLbl.setFont(Font.font("Arial",
                    isPlayer ? FontWeight.BOLD : FontWeight.NORMAL, 13));
            nameLbl.setTextFill(isPlayer ? Color.web("#F0A500") : Color.WHITE);
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            Label wdl = new Label(
                    league.getWins(team) + "W " +
                            league.getDraws(team) + "D " +
                            league.getLosses(team) + "L"
            );
            wdl.setFont(Font.font("Arial", 11));
            wdl.setTextFill(Color.web("#8A8A9A"));

            Label ptsLbl = new Label(team.getPoints() + " pts");
            ptsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            ptsLbl.setTextFill(isPlayer ? Color.web("#F0A500") : Color.WHITE);
            ptsLbl.setMinWidth(50);
            ptsLbl.setAlignment(Pos.CENTER_RIGHT);

            row.getChildren().addAll(rankLbl, nameLbl, wdl, ptsLbl);
            card.getChildren().add(row);
            rank++;
        }

        return card;
    }
}