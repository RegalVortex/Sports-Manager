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

import java.util.List;

public class DashboardScene {

    private static final String BG      = "#0D0D0D";
    private static final String CARD    = "#1E2332";
    private static final String CARD2   = "#252A3D";
    private static final String GOLD    = "#F0A500";
    private static final String TEXT    = "#FFFFFF";
    private static final String SUBTEXT = "#8A8A9A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#F44336";

    public static Scene create() {
        SceneManager sm     = SceneManager.getInstance();
        ILeague      league = sm.getCurrentLeague();
        ITeam        team   = sm.getCurrentPlayerTeam();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox top = new VBox(0);
        top.getChildren().add(createTopBar(league, team));
        top.getChildren().add(createTeamHeader(team, league));
        top.getChildren().add(createTabBar());
        root.setTop(top);

        root.setCenter(createScrollContent(league, team));

        // Sabit boyut yok — stage'e bağlı
        return new Scene(root);
    }

    private static HBox createTopBar(ILeague league, ITeam team) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setPadding(new Insets(12, 16, 12, 16));

        int week = league != null ? league.getCurrentWeek() : 1;
        Label weekLbl = new Label("Hafta " + week);
        weekLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        weekLbl.setTextFill(Color.web(TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        IMatch nextMatch = findNextMatch(league, team);
        Label advBtn = new Label(nextMatch != null ? "⚽  MAÇI OYNA" : "▶  İLERLE");
        advBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        advBtn.setTextFill(Color.web(BG));
        advBtn.setPadding(new Insets(8, 18, 8, 18));
        advBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 8; -fx-cursor: hand;");
        advBtn.setOnMouseClicked(e -> {
            if (league == null) return;
            IMatch match = findNextMatch(league, team);
            if (match != null) {
                MatchScene.prepareMatch(match, league);
                SceneManager.getInstance().navigateTo("match");
            } else {
                league.advanceWeek();
                if (league.isSeasonOver()) SceneManager.getInstance().navigateTo("seasonend");
                else SceneManager.getInstance().navigateTo("dashboard");
            }
        });

        bar.getChildren().addAll(weekLbl, spacer, advBtn);

        VBox wrap = new VBox(0, bar);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox createTeamHeader(ITeam team, ILeague league) {
        VBox header = new VBox(6);
        header.setStyle("-fx-background-color: " + CARD + ";");
        header.setPadding(new Insets(16, 16, 14, 16));

        Label nameLbl = new Label(team != null ? team.getName() : "Takımın");
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        nameLbl.setTextFill(Color.web(TEXT));

        String rankText = "";
        if (league != null && team != null) {
            int rank = league.getStandings().getTeams().indexOf(team) + 1;
            rankText = rank + ". sırada — " + league.getName();
        }
        Label rankLbl = new Label(rankText);
        rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        rankLbl.setTextFill(Color.web(GOLD));

        header.getChildren().addAll(nameLbl, rankLbl);

        VBox wrap = new VBox(0, header);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        return wrap;
    }

    private static HBox createTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle("-fx-background-color: " + CARD + ";");

        String[][] tabs = {
                {"Ana Sayfa", "dashboard"},
                {"Kadro",     "squad"},
                {"Lig",       "league"}
        };

        for (String[] tab : tabs) {
            boolean active = tab[1].equals("dashboard");
            VBox cell = new VBox(0);
            cell.setAlignment(Pos.CENTER);
            cell.setPadding(new Insets(12, 0, 0, 0));
            cell.setStyle("-fx-cursor: hand;");
            HBox.setHgrow(cell, Priority.ALWAYS); // ← responsive

            Label lbl = new Label(tab[0]);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setTextFill(Color.web(active ? GOLD : SUBTEXT));

            Region underline = new Region();
            underline.setPrefHeight(2);
            underline.setMaxWidth(Double.MAX_VALUE);
            underline.setStyle("-fx-background-color: " + (active ? GOLD : "transparent") + ";");
            VBox.setMargin(underline, new Insets(10, 0, 0, 0));

            cell.getChildren().addAll(lbl, underline);
            String target = tab[1];
            cell.setOnMouseClicked(ev -> SceneManager.getInstance().navigateTo(target));
            bar.getChildren().add(cell);
        }

        VBox wrap = new VBox(0, bar);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static ScrollPane createScrollContent(ILeague league, ITeam team) {
        VBox content = new VBox(14);
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setPadding(new Insets(14));

        content.getChildren().add(createInfoCard(league, team));
        content.getChildren().add(createNextMatchCard(league, team));
        content.getChildren().add(createStandingsSummary(league, team));

        Region pad = new Region(); pad.setPrefHeight(20);
        content.getChildren().add(pad);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);  // ← genişliğe uyar
        scroll.setFitToHeight(false);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    private static HBox createInfoCard(ILeague league, ITeam team) {
        HBox card = new HBox(0);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");

        int week   = league != null ? league.getCurrentWeek() : 0;
        int season = league != null ? league.getCurrentSeason() : 1;
        int pts    = team   != null ? team.getPoints() : 0;
        int rank   = 0, total = 0;
        if (league != null && team != null) {
            rank  = league.getStandings().getTeams().indexOf(team) + 1;
            total = league.getTeams().size();
        }

        card.getChildren().addAll(
                infoStat("Sezon",  String.valueOf(season)),
                infoStat("Hafta",  String.valueOf(week)),
                infoStat("Puan",   String.valueOf(pts)),
                infoStat("Sıra",   rank + "/" + total)
        );
        return card;
    }

    private static VBox infoStat(String label, String value) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(14, 8, 14, 8));
        HBox.setHgrow(box, Priority.ALWAYS); // ← responsive: eşit pay

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valLbl.setTextFill(Color.web(TEXT));

        Label lblLbl = new Label(label.toUpperCase());
        lblLbl.setFont(Font.font("Arial", 10));
        lblLbl.setTextFill(Color.web(SUBTEXT));

        box.getChildren().addAll(valLbl, lblLbl);
        return box;
    }

    private static VBox createNextMatchCard(ILeague league, ITeam team) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        card.setPadding(new Insets(16));
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("SONRAKI MAÇ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web(SUBTEXT));

        if (league == null || team == null) {
            card.getChildren().addAll(title);
            return card;
        }

        IMatch next = findNextMatch(league, team);
        if (next == null) {
            Label done = new Label("Sezon tamamlandı — fikstür bitti.");
            done.setFont(Font.font("Arial", 13));
            done.setTextFill(Color.web(SUBTEXT));
            card.getChildren().addAll(title, done);
            return card;
        }

        boolean isHome = next.getHomeTeam().equals(team);
        Label venueLbl = new Label(isHome ? "İÇ SAHA" : "DEPLASMAN");
        venueLbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        venueLbl.setPadding(new Insets(2, 8, 2, 8));
        String vc = isHome ? GREEN : "#5C6BC0";
        venueLbl.setStyle("-fx-background-color: " + vc + "44; -fx-background-radius: 4;");
        venueLbl.setTextFill(Color.web(vc));

        HBox vsRow = new HBox(0);
        vsRow.setAlignment(Pos.CENTER);

        VBox homeBox = vsTeamBox(next.getHomeTeam().getName(),
                next.getHomeTeam().getTeamOverallRating(), next.getHomeTeam().equals(team));
        HBox.setHgrow(homeBox, Priority.ALWAYS);

        VBox scoreBox = new VBox(4);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPadding(new Insets(0, 16, 0, 16));
        Label vsLbl = new Label("VS");
        vsLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        vsLbl.setTextFill(Color.web(SUBTEXT));
        Label wkLbl = new Label("Hf " + next.getWeek());
        wkLbl.setFont(Font.font("Arial", 10));
        wkLbl.setTextFill(Color.web(SUBTEXT));
        scoreBox.getChildren().addAll(vsLbl, wkLbl);

        VBox awayBox = vsTeamBox(next.getAwayTeam().getName(),
                next.getAwayTeam().getTeamOverallRating(), next.getAwayTeam().equals(team));
        HBox.setHgrow(awayBox, Priority.ALWAYS);

        vsRow.getChildren().addAll(homeBox, scoreBox, awayBox);
        card.getChildren().addAll(title, venueLbl, vsRow);
        return card;
    }

    private static VBox vsTeamBox(String name, int ovr, boolean isPlayer) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        StackPane avatar = new StackPane();
        Rectangle bg2 = new Rectangle(50, 50);
        bg2.setArcWidth(10); bg2.setArcHeight(10);
        bg2.setFill(Color.web(isPlayer ? "#2D3860" : CARD2));
        Label init = new Label(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)));
        init.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        init.setTextFill(Color.web(isPlayer ? GOLD : TEXT));
        avatar.getChildren().addAll(bg2, init);

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(isPlayer ? GOLD : TEXT));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);

        Label ovrLbl = new Label("OVR " + ovr);
        ovrLbl.setFont(Font.font("Arial", 11));
        ovrLbl.setTextFill(Color.web(SUBTEXT));

        box.getChildren().addAll(avatar, nameLbl, ovrLbl);
        return box;
    }

    private static VBox createStandingsSummary(ILeague league, ITeam playerTeam) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(14, 16, 10, 16));
        hdr.setAlignment(Pos.CENTER_LEFT);
        Label hdrLbl = new Label("PUAN DURUMU");
        hdrLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        hdrLbl.setTextFill(Color.web(SUBTEXT));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label viewAll = new Label("Tümünü gör →");
        viewAll.setFont(Font.font("Arial", 11));
        viewAll.setTextFill(Color.web(GOLD));
        viewAll.setStyle("-fx-cursor: hand;");
        viewAll.setOnMouseClicked(e -> SceneManager.getInstance().navigateTo("league"));
        hdr.getChildren().addAll(hdrLbl, sp, viewAll);
        card.getChildren().add(hdr);

        Region sep0 = new Region(); sep0.setPrefHeight(1);
        sep0.setStyle("-fx-background-color: #2A3050;");
        card.getChildren().add(sep0);

        if (league == null) return card;

        List<ITeam> ranked = league.getStandings().getTeams();
        for (int i = 0; i < ranked.size(); i++) {
            ITeam t = ranked.get(i);
            boolean me = t.equals(playerTeam);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16));
            row.setMaxWidth(Double.MAX_VALUE);
            row.setStyle("-fx-background-color: " + (me ? "#1A2040" : "transparent") + ";");

            Label rankLbl = new Label(String.valueOf(i + 1));
            rankLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            rankLbl.setTextFill(Color.web(me ? GOLD : SUBTEXT));
            rankLbl.setPrefWidth(24);

            Label nameLbl = new Label(t.getName());
            nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLbl.setTextFill(Color.web(me ? GOLD : TEXT));
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            int w = league.getWins(t), d = league.getDraws(t), l = league.getLosses(t);
            Label wdl = new Label(w + "G " + d + "B " + l + "M");
            wdl.setFont(Font.font("Arial", 11));
            wdl.setTextFill(Color.web(SUBTEXT));

            Label pts = new Label(t.getPoints() + " P");
            pts.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            pts.setTextFill(Color.web(me ? GOLD : TEXT));

            row.getChildren().addAll(rankLbl, nameLbl, wdl, pts);

            VBox rowWrap = new VBox(0, row);
            if (i < ranked.size() - 1) {
                Region sep = new Region(); sep.setPrefHeight(1);
                sep.setStyle("-fx-background-color: #1A1F30;");
                rowWrap.getChildren().add(sep);
            }
            card.getChildren().add(rowWrap);
        }
        return card;
    }

    private static IMatch findNextMatch(ILeague league, ITeam team) {
        if (league == null || team == null) return null;
        int week = league.getCurrentWeek();
        for (IMatch m : league.getAllFixtures()) {
            if (m.getWeek() == week && m.getResult() == null) {
                if (m.getHomeTeam().equals(team) || m.getAwayTeam().equals(team)) return m;
            }
        }
        return null;
    }
}