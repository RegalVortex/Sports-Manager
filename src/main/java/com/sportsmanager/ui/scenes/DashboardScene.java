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

    private static final int MAX_W = 600;

    public static Scene create() {
        SceneManager sm     = SceneManager.getInstance();
        ILeague      league = sm.getCurrentLeague();
        ITeam        team   = sm.getCurrentPlayerTeam();

        StackPane outer = new StackPane();
        outer.setStyle("-fx-background-color: " + BG + ";");

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setMaxWidth(MAX_W);

        VBox top = new VBox(0);
        top.getChildren().add(createTopBar(league, team));
        top.getChildren().add(createTeamHeader(team, league));
        top.getChildren().add(createTabBar());
        content.setTop(top);

        content.setCenter(createScrollContent(league, team));

        content.setMaxHeight(Double.MAX_VALUE);
        outer.getChildren().add(content);

        return new Scene(outer, 480, 850);
    }

    private static VBox createTopBar(ILeague league, ITeam team) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.setMaxWidth(Double.MAX_VALUE);

        int week = league != null ? league.getCurrentWeek() : 1;
        Label weekLbl = new Label("Hafta " + week);
        weekLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        weekLbl.setTextFill(Color.web(TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean hasMatch = findNextMatch(league, team) != null;
        boolean seasonOver = league != null && league.isSeasonOver();
        Label advBtn;
        if (seasonOver) {
            advBtn = new Label("🏆  SEZON SONU");
        } else if (hasMatch) {
            advBtn = new Label("⚽  MAÇI OYNA");
        } else {
            advBtn = new Label("▶  İLERLE");
        }
        advBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        advBtn.setTextFill(Color.web(BG));
        advBtn.setPadding(new Insets(8, 18, 8, 18));
        advBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 8; -fx-cursor: hand;");
        advBtn.setOnMouseClicked(e -> {
            if (league == null) return;
            if (league.isSeasonOver()) {
                SceneManager.getInstance().navigateTo("seasonend");
                return;
            }
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
        wrap.setMaxWidth(Double.MAX_VALUE);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        return wrap;
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

    private static VBox createTabBar() {
        HBox bar = new HBox(0);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setMaxWidth(Double.MAX_VALUE);

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
            HBox.setHgrow(cell, Priority.ALWAYS);

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
        wrap.setMaxWidth(Double.MAX_VALUE);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        return wrap;
    }

    private static ScrollPane createScrollContent(ILeague league, ITeam team) {
        VBox content = new VBox(14);
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setPadding(new Insets(14));

        content.getChildren().add(createInfoCard(league, team));
        content.getChildren().add(createNextMatchCard(league, team));

        VBox recentResults = createRecentResultsCard(league, team);
        if (recentResults != null) content.getChildren().add(recentResults);

        content.getChildren().add(createStandingsSummary(league, team));

        Region pad = new Region(); pad.setPrefHeight(20);
        content.getChildren().add(pad);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    private static HBox createInfoCard(ILeague league, ITeam team) {
        HBox card = new HBox(0);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        card.setMaxWidth(Double.MAX_VALUE);

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
            String msg = league.isSeasonOver()
                    ? "Sezon tamamlandı — fikstür bitti."
                    : "Bu hafta maçınız yok — ▶ İLERLE ile devam edebilirsiniz.";
            Label done = new Label(msg);
            done.setFont(Font.font("Arial", 13));
            done.setTextFill(Color.web(SUBTEXT));
            done.setWrapText(true);
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

    // ── Son maçlar kartı ──────────────────────────────────────────────────────
    private static VBox createRecentResultsCard(ILeague league, ITeam team) {
        if (league == null || team == null) return null;

        // Oynanan maçları topla (sonucu olan)
        List<IMatch> played = new java.util.ArrayList<>();
        for (IMatch m : league.getAllFixtures()) {
            if (m.getResult() != null &&
                (m.getHomeTeam().equals(team) || m.getAwayTeam().equals(team))) {
                played.add(m);
            }
        }
        if (played.isEmpty()) return null;

        // En son 5 maç
        int from = Math.max(0, played.size() - 5);
        List<IMatch> recent = played.subList(from, played.size());
        // Kronolojik sıraya çevir (listedeki sıra zaten kronolojik)

        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 10;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox hdr = new HBox();
        hdr.setPadding(new Insets(14, 16, 10, 16));
        hdr.setAlignment(Pos.CENTER_LEFT);
        Label hdrLbl = new Label("SON MAÇLAR");
        hdrLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        hdrLbl.setTextFill(Color.web(SUBTEXT));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Form çizgisi (5 kutu W/D/L)
        HBox formStrip = new HBox(5);
        formStrip.setAlignment(Pos.CENTER);
        for (IMatch m : recent) {
            MatchResult r = m.getResult();
            boolean isHome = m.getHomeTeam().equals(team);
            int myScore  = isHome ? r.getHomeScore() : r.getAwayScore();
            int oppScore = isHome ? r.getAwayScore() : r.getHomeScore();
            String letter; String color;
            if      (myScore > oppScore) { letter = "G"; color = GREEN; }
            else if (myScore < oppScore) { letter = "M"; color = RED;   }
            else                          { letter = "B"; color = "#FF9800"; }

            StackPane box = new StackPane();
            Rectangle bg = new Rectangle(22, 22);
            bg.setArcWidth(4); bg.setArcHeight(4);
            bg.setFill(Color.web(color + "33"));
            Label lbl = new Label(letter);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            lbl.setTextFill(Color.web(color));
            box.getChildren().addAll(bg, lbl);
            formStrip.getChildren().add(box);
        }
        hdr.getChildren().addAll(hdrLbl, sp, formStrip);
        card.getChildren().add(hdr);

        Region sep0 = new Region(); sep0.setPrefHeight(1);
        sep0.setStyle("-fx-background-color: #2A3050;");
        card.getChildren().add(sep0);

        // Her maç satırı
        for (int i = recent.size() - 1; i >= 0; i--) {
            IMatch m = recent.get(i);
            MatchResult r = m.getResult();
            boolean isHome = m.getHomeTeam().equals(team);
            int myScore  = isHome ? r.getHomeScore() : r.getAwayScore();
            int oppScore = isHome ? r.getAwayScore()  : r.getHomeScore();
            String oppName = isHome ? m.getAwayTeam().getName() : m.getHomeTeam().getName();

            String result; String resColor;
            if      (myScore > oppScore) { result = "G"; resColor = GREEN; }
            else if (myScore < oppScore) { result = "M"; resColor = RED;   }
            else                          { result = "B"; resColor = "#FF9800"; }

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16));

            // G/M/B rozeti
            StackPane badge = new StackPane();
            Rectangle badgeBg = new Rectangle(26, 20);
            badgeBg.setArcWidth(4); badgeBg.setArcHeight(4);
            badgeBg.setFill(Color.web(resColor + "33"));
            Label badgeLbl = new Label(result);
            badgeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            badgeLbl.setTextFill(Color.web(resColor));
            badge.getChildren().addAll(badgeBg, badgeLbl);

            // Ev/Deplasman
            Label venueLbl = new Label(isHome ? "İÇ" : "DEP");
            venueLbl.setFont(Font.font("Arial", 10));
            venueLbl.setTextFill(Color.web(isHome ? GREEN : SUBTEXT));
            venueLbl.setPrefWidth(26);

            // Rakip
            Label oppLbl = new Label(oppName);
            oppLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            oppLbl.setTextFill(Color.web(TEXT));
            HBox.setHgrow(oppLbl, Priority.ALWAYS);

            // Skor
            Label scoreLbl = new Label(myScore + " - " + oppScore);
            scoreLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            scoreLbl.setTextFill(Color.web(myScore > oppScore ? GREEN : myScore < oppScore ? RED : SUBTEXT));

            row.getChildren().addAll(badge, venueLbl, oppLbl, scoreLbl);

            VBox rowWrap = new VBox(0, row);
            Region sep = new Region(); sep.setPrefHeight(1);
            sep.setStyle("-fx-background-color: #1A1F30;");
            rowWrap.getChildren().add(sep);
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