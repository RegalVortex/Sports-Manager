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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class LeagueScene {

    private static final String BG      = "#0D0D0D";
    private static final String CARD    = "#1E2332";
    private static final String CARD2   = "#252A3D";
    private static final String GOLD    = "#F0A500";
    private static final String TEXT    = "#FFFFFF";
    private static final String SUBTEXT = "#8A8A9A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#F44336";
    private static final String DRAW    = "#FF9800";

    private static final int    MAX_W        = 600;
    private static String activeTab     = "STANDINGS";
    private static String fixtureFilter = "ALL";

    public static Scene create() {
        SceneManager sm  = SceneManager.getInstance();
        ILeague league   = sm.getCurrentLeague();

        StackPane outer = new StackPane();
        outer.setStyle("-fx-background-color: " + BG + ";");

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setMaxWidth(MAX_W);
        content.setTop(createTopBar(league));

        VBox body = new VBox(0);
        body.setMaxHeight(Double.MAX_VALUE);
        body.getChildren().add(createSubTabBar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        if ("STANDINGS".equals(activeTab)) {
            scroll.setContent(createStandingsContent(league));
        } else {
            scroll.setContent(createFixturesContent(league));
        }

        body.getChildren().add(scroll);
        content.setCenter(body);

        content.setMaxHeight(Double.MAX_VALUE);
        outer.getChildren().add(content);

        return new Scene(outer, 480, 850);
    }

    private static HBox createTopBar(ILeague league) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setPadding(new Insets(14, 16, 14, 16));

        Label back = new Label("←");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        back.setTextFill(Color.web(GOLD));
        back.setStyle("-fx-cursor: hand;");
        back.setOnMouseClicked(e -> {
            activeTab = "STANDINGS";
            fixtureFilter = "ALL";
            SceneManager.getInstance().navigateTo("dashboard");
        });

        Label title = new Label(league != null ? league.getName().toUpperCase() : "LİG");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        title.setTextFill(Color.web(TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int week = league != null ? league.getCurrentWeek() : 0;
        Label weekLbl = new Label("Hafta " + week);
        weekLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        weekLbl.setTextFill(Color.web(SUBTEXT));

        bar.getChildren().addAll(back, title, spacer, weekLbl);
        return bar;
    }

    private static HBox createSubTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setMaxWidth(Double.MAX_VALUE);

        String[] tabs = {"STANDINGS", "FİKSTÜR"};
        String[] keys  = {"STANDINGS", "FIXTURES"};

        for (int i = 0; i < tabs.length; i++) {
            String label = tabs[i];
            String key   = keys[i];
            VBox cell = new VBox(0);
            cell.setAlignment(Pos.CENTER);
            HBox.setHgrow(cell, Priority.ALWAYS);
            cell.setPadding(new Insets(12, 0, 0, 0));
            cell.setStyle("-fx-cursor: hand;");

            boolean active = key.equals(activeTab);
            Label lbl = new Label(label);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setTextFill(Color.web(active ? GOLD : SUBTEXT));

            Region underline = new Region();
            underline.setPrefHeight(2);
            underline.setMaxWidth(Double.MAX_VALUE);
            underline.setStyle("-fx-background-color: " + (active ? GOLD : "transparent") + ";");
            VBox.setMargin(underline, new Insets(10, 0, 0, 0));

            cell.getChildren().addAll(lbl, underline);
            cell.setOnMouseClicked(e -> {
                activeTab = key;
                SceneManager.getInstance().navigateTo("league");
            });
            bar.getChildren().add(cell);
        }

        VBox wrap = new VBox(0);
        wrap.getChildren().add(bar);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox createStandingsContent(ILeague league) {
        VBox vbox = new VBox(0);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        if (league == null) return vbox;

        HBox header = new HBox();
        header.setPadding(new Insets(10, 16, 6, 16));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + BG + ";");
        Label rankH = colHeader("#");   rankH.setPrefWidth(30);
        Label nameH = colHeader("TAKIM");
        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        Label wH = colHeader("G");  wH.setPrefWidth(28); wH.setAlignment(Pos.CENTER);
        Label dH = colHeader("B");  dH.setPrefWidth(28); dH.setAlignment(Pos.CENTER);
        Label lH = colHeader("M");  lH.setPrefWidth(28); lH.setAlignment(Pos.CENTER);
        Label pH = colHeader("P");  pH.setPrefWidth(40); pH.setAlignment(Pos.CENTER_RIGHT);
        header.getChildren().addAll(rankH, nameH, s1, wH, dH, lH, pH);
        vbox.getChildren().add(header);

        Region sepH = new Region(); sepH.setPrefHeight(1);
        sepH.setStyle("-fx-background-color: #2A3050;");
        vbox.getChildren().add(sepH);

        LeagueStandings standings = league.getStandings();
        List<ITeam> teams = standings.getTeams();
        ITeam playerTeam = SceneManager.getInstance().getCurrentPlayerTeam();

        for (int i = 0; i < teams.size(); i++) {
            vbox.getChildren().add(standingRow(i + 1, teams.get(i), teams.get(i).equals(playerTeam), league));
        }

        Region pad = new Region(); pad.setPrefHeight(30);
        vbox.getChildren().add(pad);
        return vbox;
    }

    private static HBox standingRow(int rank, ITeam team, boolean mine, ILeague league) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: " + (mine ? "#1A2040" : BG) + ";");

        Rectangle bar = new Rectangle(3, 30);
        bar.setFill(Color.web(mine ? GOLD : rank <= 2 ? GREEN : SUBTEXT));
        HBox.setMargin(bar, new Insets(0, 10, 0, 0));

        Label rankLbl = new Label(String.valueOf(rank));
        rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        rankLbl.setTextFill(Color.web(mine ? GOLD : SUBTEXT));
        rankLbl.setPrefWidth(24);

        StackPane avatar = new StackPane();
        Rectangle bg2 = new Rectangle(32, 32);
        bg2.setArcWidth(6); bg2.setArcHeight(6);
        bg2.setFill(Color.web(mine ? "#2D3860" : CARD2));
        Label init = new Label(teamInit(team.getName()));
        init.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        init.setTextFill(Color.web(mine ? GOLD : TEXT));
        avatar.getChildren().addAll(bg2, init);
        HBox.setMargin(avatar, new Insets(0, 10, 0, 6));

        Label nameLbl = new Label(team.getName());
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(mine ? GOLD : TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        int w = league.getWins(team);
        int d = league.getDraws(team);
        int l = league.getLosses(team);

        Label wL = statLbl(String.valueOf(w), GREEN);
        Label dL = statLbl(String.valueOf(d), DRAW);
        Label lL = statLbl(String.valueOf(l), RED);

        Label ptsLbl = new Label(String.valueOf(team.getPoints()));
        ptsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        ptsLbl.setTextFill(Color.web(mine ? GOLD : TEXT));
        ptsLbl.setPrefWidth(40);
        ptsLbl.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(bar, rankLbl, avatar, nameLbl, spacer, wL, dL, lL, ptsLbl);

        VBox wrap = new VBox(0, row);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #1A1F30;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox createFixturesContent(ILeague league) {
        VBox vbox = new VBox(0);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        if (league == null) return vbox;

        vbox.getChildren().add(createFilterRow());

        List<IMatch> fixtures = league.getAllFixtures();
        int currentWeek = league.getCurrentWeek();
        ITeam playerTeam = SceneManager.getInstance().getCurrentPlayerTeam();

        List<IMatch> filtered = new ArrayList<>();
        for (IMatch m : fixtures) {
            boolean played = m.getResult() != null;
            switch (fixtureFilter) {
                case "UPCOMING": if (!played) filtered.add(m); break;
                case "PAST":     if (played)  filtered.add(m); break;
                default:         filtered.add(m); break;
            }
        }

        if (filtered.isEmpty()) {
            Label none = new Label("Gösterilecek maç yok.");
            none.setTextFill(Color.web(SUBTEXT));
            none.setPadding(new Insets(20));
            vbox.getChildren().add(none);
        } else {
            int shownWeek = -1;
            for (IMatch m : filtered) {
                if (m.getWeek() != shownWeek) {
                    shownWeek = m.getWeek();
                    vbox.getChildren().add(weekHeader(shownWeek, currentWeek));
                }
                boolean inv = playerTeam != null
                        && (m.getHomeTeam().equals(playerTeam) || m.getAwayTeam().equals(playerTeam));
                vbox.getChildren().add(matchRow(m, inv, playerTeam));
            }
        }

        Region pad = new Region(); pad.setPrefHeight(30);
        vbox.getChildren().add(pad);
        return vbox;
    }

    private static HBox createFilterRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: " + BG + ";");

        String[] labels  = {"Tümü", "Gelecek", "Geçmiş"};
        String[] filters = {"ALL", "UPCOMING", "PAST"};

        for (int i = 0; i < labels.length; i++) {
            String lbl = labels[i];
            String flt = filters[i];
            boolean active = flt.equals(fixtureFilter);
            Label btn = new Label(lbl);
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            btn.setPadding(new Insets(5, 12, 5, 12));
            btn.setStyle(active
                    ? "-fx-background-color: " + GOLD + "; -fx-background-radius: 12;"
                    : "-fx-background-color: " + CARD2 + "; -fx-background-radius: 12;");
            btn.setTextFill(Color.web(active ? BG : SUBTEXT));
            btn.setStyle(btn.getStyle());
            btn.setStyle("-fx-background-color: " + (active ? GOLD : CARD2) + "; -fx-background-radius: 12; -fx-cursor: hand;");
            btn.setTextFill(Color.web(active ? BG : SUBTEXT));
            btn.setOnMouseClicked(e -> {
                fixtureFilter = flt;
                SceneManager.getInstance().navigateTo("league");
            });
            row.getChildren().add(btn);
        }
        return row;
    }

    private static HBox weekHeader(int week, int current) {
        HBox h = new HBox();
        h.setPadding(new Insets(12, 16, 4, 16));
        h.setAlignment(Pos.CENTER_LEFT);
        String lbl = week == current ? "HAFTA " + week + "  ·  GÜNCEL" : "HAFTA " + week;
        Label l = new Label(lbl);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web(week == current ? GOLD : SUBTEXT));
        h.getChildren().add(l);
        return h;
    }

    private static HBox matchRow(IMatch match, boolean inv, ITeam playerTeam) {
        HBox card = new HBox(0);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + (inv ? "#1A2040" : CARD) + ";");
        card.setPadding(new Insets(12, 16, 12, 16));

        boolean played = match.getResult() != null;

        VBox homeBox = new VBox(3);
        homeBox.setAlignment(Pos.CENTER_RIGHT);
        homeBox.setPrefWidth(160);
        boolean homeIsP = playerTeam != null && match.getHomeTeam().equals(playerTeam);
        Label homeLbl = new Label(match.getHomeTeam().getName());
        homeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        homeLbl.setTextFill(Color.web(homeIsP ? GOLD : TEXT));
        homeBox.getChildren().add(homeLbl);

        VBox scoreBox = new VBox(2);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPrefWidth(80);

        if (played) {
            MatchResult r = match.getResult();
            Label score = new Label(r.getHomeScore() + " – " + r.getAwayScore());
            score.setFont(Font.font("Arial", FontWeight.BOLD, 17));
            score.setTextFill(Color.web(TEXT));
            if (playerTeam != null) {
                String res = outcome(r, playerTeam);
                Label resLbl = new Label(res);
                resLbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
                resLbl.setPadding(new Insets(1, 5, 1, 5));
                String c = "G".equals(res) ? GREEN : ("M".equals(res) ? RED : DRAW);
                resLbl.setStyle("-fx-background-color: " + c + "; -fx-background-radius: 3;");
                resLbl.setTextFill(Color.WHITE);
                scoreBox.getChildren().addAll(score, resLbl);
            } else {
                scoreBox.getChildren().add(score);
            }
        } else {
            Label vs = new Label("VS");
            vs.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            vs.setTextFill(Color.web(SUBTEXT));
            Label wk = new Label("Hf " + match.getWeek());
            wk.setFont(Font.font("Arial", 10));
            wk.setTextFill(Color.web(SUBTEXT));
            scoreBox.getChildren().addAll(vs, wk);
        }

        VBox awayBox = new VBox(3);
        awayBox.setAlignment(Pos.CENTER_LEFT);
        awayBox.setPrefWidth(160);
        boolean awayIsP = playerTeam != null && match.getAwayTeam().equals(playerTeam);
        Label awayLbl = new Label(match.getAwayTeam().getName());
        awayLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        awayLbl.setTextFill(Color.web(awayIsP ? GOLD : TEXT));
        awayBox.getChildren().add(awayLbl);

        card.getChildren().addAll(homeBox, scoreBox, awayBox);

        VBox wrap = new VBox(0, card);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #1A1F30;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static Label colHeader(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(SUBTEXT));
        return l;
    }
    private static Label statLbl(String val, String color) {
        Label l = new Label(val);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        l.setTextFill(Color.web(color));
        l.setPrefWidth(28);
        l.setAlignment(Pos.CENTER);
        return l;
    }
    private static String teamInit(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] p = name.trim().split("\\s+");
        return p.length == 1
                ? p[0].substring(0, Math.min(2, p[0].length())).toUpperCase()
                : ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase();
    }
    private static String outcome(MatchResult r, ITeam t) {
        if (r == null || t == null) return "";
        boolean home = r.getHomeTeam().equals(t);
        int my = home ? r.getHomeScore() : r.getAwayScore();
        int their = home ? r.getAwayScore() : r.getHomeScore();
        return my > their ? "G" : my < their ? "M" : "B";
    }
}