package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.*;
import com.sportsmanager.ui.SceneManager;
import javafx.geometry.Insets;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class SeasonEndScene {

    private static final String BG      = "#0D0D0D";
    private static final String CARD    = "#1E2332";
    private static final String GOLD    = "#F0A500";
    private static final String TEXT    = "#FFFFFF";
    private static final String SUBTEXT = "#8A8A9A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#F44336";
    private static final String DRAW    = "#FF9800";
    private static final int    MAX_W   = 600;

    public static Scene create() {
        SceneManager sm    = SceneManager.getInstance();
        ILeague league     = sm.getCurrentLeague();
        ITeam   playerTeam = sm.getCurrentPlayerTeam();
        ITeam   champion   = league != null ? league.getChampion() : null;
        boolean isChamp    = champion != null && playerTeam != null && champion.equals(playerTeam);

        StackPane outer = new StackPane();
        outer.setStyle("-fx-background-color: " + BG + ";");

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setMaxWidth(MAX_W);

        VBox body = new VBox(0);
        body.setStyle("-fx-background-color: " + BG + ";");
        body.getChildren().add(buildHero(champion, isChamp));
        body.getChildren().add(buildStandings(league, playerTeam));
        body.getChildren().add(buildTopPlayers(playerTeam));
        body.getChildren().add(buildActions(league));

        Region pad = new Region(); pad.setPrefHeight(30);
        body.getChildren().add(pad);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setCenter(scroll);

        StackPane.setAlignment(content, Pos.TOP_CENTER);
        outer.getChildren().add(content);

        return new Scene(outer, 480, 850);
    }

    private static VBox buildHero(ITeam champion, boolean isChamp) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #1A1500, #0D0D0D);");
        card.setPadding(new Insets(40, 24, 30, 24));

        Label trophy = new Label("🏆");
        trophy.setFont(Font.font(52));

        Label titleLbl = new Label("SEZON TAMAMLANDI");
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLbl.setTextFill(Color.web(SUBTEXT));

        Label champLbl = new Label(champion != null ? champion.getName().toUpperCase() : "—");
        champLbl.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        champLbl.setTextFill(Color.web(GOLD));
        champLbl.setWrapText(true);
        champLbl.setAlignment(Pos.CENTER);

        Label subLbl = new Label(isChamp
                ? "🎉  Tebrikler — ŞAMPİYON SİZSİNİZ!"
                : "Gelecek sezon daha iyisini yaparsınız.");
        subLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        subLbl.setTextFill(Color.web(isChamp ? GREEN : SUBTEXT));
        subLbl.setWrapText(true);
        subLbl.setAlignment(Pos.CENTER);

        card.getChildren().addAll(trophy, titleLbl, champLbl, subLbl);

        VBox wrap = new VBox(0, card);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        return wrap;
    }

    private static VBox buildStandings(ILeague league, ITeam playerTeam) {
        VBox section = new VBox(0);
        section.setStyle("-fx-background-color: " + BG + ";");

        HBox sh = new HBox();
        sh.setPadding(new Insets(16, 16, 8, 16));
        Label shLbl = new Label("FİNAL PUAN DURUMU");
        shLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        shLbl.setTextFill(Color.web(SUBTEXT));
        sh.getChildren().add(shLbl);
        section.getChildren().add(sh);

        if (league == null) return section;

        List<ITeam> teams = league.getStandings().getTeams();
        for (int i = 0; i < teams.size(); i++) {
            ITeam t   = teams.get(i);
            boolean me = t.equals(playerTeam);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: " + (me ? "#1A2040" : BG) + ";");

            Label icon = new Label(i == 0 ? "🏆" : i == 1 ? "🥈" : i == 2 ? "🥉" : "   ");
            icon.setFont(Font.font(16)); icon.setPrefWidth(28);

            Label rankLbl = new Label(String.valueOf(i + 1));
            rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            rankLbl.setTextFill(Color.web(me ? GOLD : SUBTEXT));
            rankLbl.setPrefWidth(24);

            Label nameLbl = new Label(t.getName());
            nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLbl.setTextFill(Color.web(me ? GOLD : (i == 0 ? GOLD : TEXT)));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            int w = league.getWins(t);
            int d = league.getDraws(t);
            int l = league.getLosses(t);
            Label wdl = new Label(w + "G " + d + "B " + l + "M");
            wdl.setFont(Font.font("Arial", 11));
            wdl.setTextFill(Color.web(SUBTEXT));

            Label ptsLbl = new Label(t.getPoints() + " P");
            ptsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            ptsLbl.setTextFill(Color.web(me ? GOLD : TEXT));

            row.getChildren().addAll(icon, rankLbl, nameLbl, spacer, wdl, ptsLbl);

            VBox wrap = new VBox(0, row);
            Region sep = new Region(); sep.setPrefHeight(1);
            sep.setStyle("-fx-background-color: #1A1F30;");
            wrap.getChildren().add(sep);
            section.getChildren().add(wrap);
        }
        return section;
    }

    private static VBox buildTopPlayers(ITeam playerTeam) {
        VBox section = new VBox(0);
        section.setStyle("-fx-background-color: " + BG + ";");

        HBox sh = new HBox();
        sh.setPadding(new Insets(16, 16, 8, 16));
        Label shLbl = new Label("EN İYİ OYUNCULARINIZ");
        shLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        shLbl.setTextFill(Color.web(SUBTEXT));
        sh.getChildren().add(shLbl);
        section.getChildren().add(sh);

        if (playerTeam == null) return section;

        List<IPlayer> squad = new ArrayList<>(playerTeam.getSquad());
        squad.sort((a, b) -> b.getOverallRating() - a.getOverallRating());
        int limit = Math.min(5, squad.size());

        for (int i = 0; i < limit; i++) {
            IPlayer p = squad.get(i);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16));
            row.setStyle("-fx-background-color: " + CARD + ";");

            StackPane badge = new StackPane();
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(36, 24);
            rect.setArcWidth(4); rect.setArcHeight(4);
            rect.setFill(Color.web(posColor(p.getPosition())));
            Label posLbl = new Label(p.getPosition().length() > 3
                    ? p.getPosition().substring(0, 3).toUpperCase() : p.getPosition().toUpperCase());
            posLbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            posLbl.setTextFill(Color.WHITE);
            badge.getChildren().addAll(rect, posLbl);

            VBox info = new VBox(2);
            Label nameLbl = new Label(p.getName());
            nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLbl.setTextFill(Color.web(TEXT));
            Label ageLbl = new Label("Yaş " + p.getAge() + " · Pot " + p.getPotential());
            ageLbl.setFont(Font.font("Arial", 10));
            ageLbl.setTextFill(Color.web(SUBTEXT));
            info.getChildren().addAll(nameLbl, ageLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            int ovr = p.getOverallRating();
            Label ovrLbl = new Label(String.valueOf(ovr));
            ovrLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            ovrLbl.setTextFill(Color.web(ovr >= 80 ? GOLD : ovr >= 70 ? GREEN : "#2196F3"));

            row.getChildren().addAll(badge, info, spacer, ovrLbl);

            VBox wrap = new VBox(0, row);
            Region sep = new Region(); sep.setPrefHeight(1);
            sep.setStyle("-fx-background-color: #1A1F30;");
            wrap.getChildren().add(sep);
            section.getChildren().add(wrap);
        }
        return section;
    }

    private static VBox buildActions(ILeague league) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(24, 24, 0, 24));

        Label newSeasonBtn = new Label("▶  YENİ SEZON BAŞLAT");
        newSeasonBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        newSeasonBtn.setTextFill(Color.web(BG));
        newSeasonBtn.setPrefWidth(Double.MAX_VALUE);
        newSeasonBtn.setMaxWidth(Double.MAX_VALUE);
        newSeasonBtn.setAlignment(Pos.CENTER);
        newSeasonBtn.setPadding(new Insets(16));
        newSeasonBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 10; -fx-cursor: hand;");
        newSeasonBtn.setOnMouseClicked(e -> {
            if (league != null) league.resetSeason();
            SceneManager.getInstance().navigateTo("dashboard");
        });

        Label mainMenuBtn = new Label("⌂  ANA MENÜ");
        mainMenuBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        mainMenuBtn.setTextFill(Color.web(GOLD));
        mainMenuBtn.setPrefWidth(Double.MAX_VALUE);
        mainMenuBtn.setMaxWidth(Double.MAX_VALUE);
        mainMenuBtn.setAlignment(Pos.CENTER);
        mainMenuBtn.setPadding(new Insets(14));
        mainMenuBtn.setStyle("-fx-background-color: #252A3D; -fx-background-radius: 10; -fx-cursor: hand;");
        mainMenuBtn.setOnMouseClicked(e -> SceneManager.getInstance().navigateTo("mainmenu"));

        box.getChildren().addAll(newSeasonBtn, mainMenuBtn);
        return box;
    }

    private static String posColor(String pos) {
        if (pos == null) return "#607D8B";
        switch (pos.toUpperCase()) {
            case "GK": return "#5C6BC0";
            case "CB": case "LB": case "RB": case "DEF": return "#1565C0";
            case "CDM": case "CM": case "CAM": case "MID": return "#2E7D32";
            case "LW": case "RW": case "ST": case "CF": case "FWD": return "#C62828";
            case "MB": case "OH": case "S": case "L": return "#E65100";
            default: return "#607D8B";
        }
    }
}