package com.sportsmanager.ui.scenes;

import com.sportsmanager.core.*;
import com.sportsmanager.ui.SceneManager;
import javafx.animation.*;
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
import javafx.util.Duration;

import java.util.*;

public class MatchScene {

    private static final String BG      = "#0D0D0D";
    private static final String CARD    = "#1E2332";
    private static final String CARD2   = "#252A3D";
    private static final String GOLD    = "#F0A500";
    private static final String TEXT    = "#FFFFFF";
    private static final String SUBTEXT = "#8A8A9A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#F44336";
    private static final String ORANGE  = "#FF9800";
    private static final int    MAX_W   = 600;

    private static IMatch  currentMatch;
    private static ILeague currentLeague;
    private static List<int[]>  periodScores  = new ArrayList<>();
    private static List<String> matchLog      = new ArrayList<>();
    private static int          currentPeriod = 0;
    private static int          totalPeriods  = 2;
    private static boolean      matchFinished = false;
    private static int          homeTotalScore = 0;
    private static int          awayTotalScore = 0;
    // Pre-simulated official result (set in prepareMatch)
    private static int          homeSimTotal  = 0;
    private static int          awaySimTotal  = 0;

    public static void prepareMatch(IMatch match, ILeague league) {
        currentMatch   = match;
        currentLeague  = league;
        periodScores   = new ArrayList<>();
        matchLog       = new ArrayList<>();
        currentPeriod  = 0;
        matchFinished  = false;
        homeTotalScore = 0;
        awayTotalScore = 0;
        String sport = SceneManager.getInstance().getSelectedSport();
        totalPeriods = (sport != null && sport.equalsIgnoreCase("volleyball")) ? 3 : 2;

        // Simulate officially NOW so the stored result is locked in.
        // We read back the official scores and use them to drive the display.
        if (match.getResult() == null) {
            match.simulate();
        }
        MatchResult r = match.getResult();
        homeSimTotal = (r != null) ? r.getHomeScore() : 0;
        awaySimTotal = (r != null) ? r.getAwayScore() : 0;
    }

    public static Scene create() {
        if (currentMatch == null) {
            SceneManager.getInstance().navigateTo("dashboard");
            return null;
        }

        StackPane outer = new StackPane();
        outer.setStyle("-fx-background-color: " + BG + ";");

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: " + BG + ";");
        content.setMaxWidth(MAX_W);
        content.setCenter(buildMainContent());

        StackPane.setAlignment(content, Pos.TOP_CENTER);
        outer.getChildren().add(content);

        if (currentPeriod == 0 && !matchFinished) {
            buildBreakOverlay(outer, "MAÇA HAZIRLIK", "Kadronuzu hazırlayın");
        }

        return new Scene(outer);
    }

    // ── Refresh helper ─────────────────────────────────────────────────────────
    private static void refreshMain(StackPane parent) {
        if (!parent.getChildren().isEmpty() && parent.getChildren().get(0) instanceof BorderPane) {
            ((BorderPane) parent.getChildren().get(0)).setCenter(buildMainContent());
        }
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    private static VBox buildMainContent() {
        VBox vbox = new VBox(0);
        vbox.setStyle("-fx-background-color: " + BG + ";");
        vbox.getChildren().add(buildMatchHeader());
        vbox.getChildren().add(buildScoreBoard());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox logBox = new VBox(0);
        logBox.setStyle("-fx-background-color: " + BG + ";");
        logBox.setPadding(new Insets(12, 16, 20, 16));

        if (matchFinished) {
            logBox.getChildren().add(buildFullTimeCard());
        }

        for (int i = 0; i < periodScores.size(); i++) {
            logBox.getChildren().add(buildPeriodHeader(i));
            String prefix = "P" + i + ":";
            for (String line : matchLog) {
                if (line.startsWith(prefix)) {
                    Label l = new Label(line.substring(prefix.length()).trim());
                    l.setFont(Font.font("Arial", 12));
                    l.setTextFill(Color.web(SUBTEXT));
                    l.setWrapText(true);
                    l.setPadding(new Insets(3, 0, 3, 0));
                    logBox.getChildren().add(l);
                }
            }
        }

        if (matchLog.isEmpty() && !matchFinished) {
            Label w = new Label("Maç henüz başlamadı. Kadronuzu onaylayın.");
            w.setFont(Font.font("Arial", 13));
            w.setTextFill(Color.web(SUBTEXT));
            w.setWrapText(true);
            logBox.getChildren().add(w);
        }

        scroll.setContent(logBox);
        vbox.getChildren().add(scroll);
        return vbox;
    }

    private static HBox buildMatchHeader() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + CARD + ";");
        bar.setPadding(new Insets(14, 16, 14, 16));

        Label back = new Label("←");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        back.setTextFill(Color.web(GOLD));
        back.setStyle("-fx-cursor: hand;");
        back.setOnMouseClicked(e -> SceneManager.getInstance().navigateTo("dashboard"));

        String sport = SceneManager.getInstance().getSelectedSport();
        String type  = (sport != null && sport.equalsIgnoreCase("volleyball")) ? "SET" : "DEVRE";

        Label title = new Label("MAÇ  ·  " + type + " " + Math.max(currentPeriod, 1) + "/" + totalPeriods);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web(TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String statusStr = matchFinished ? "MAÇ BİTTİ" : (currentPeriod == 0 ? "HAZIRLIK" : "DEVAM");
        String statusColor = matchFinished ? SUBTEXT : (currentPeriod == 0 ? ORANGE : GREEN);
        Label status = new Label(statusStr);
        status.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        status.setPadding(new Insets(3, 8, 3, 8));
        status.setStyle("-fx-background-color: " + statusColor + "33; -fx-background-radius: 4;");
        status.setTextFill(Color.web(statusColor));

        bar.getChildren().addAll(back, title, spacer, status);
        return bar;
    }

    private static HBox buildScoreBoard() {
        HBox board = new HBox(0);
        board.setAlignment(Pos.CENTER);
        board.setStyle("-fx-background-color: #151926;");
        board.setPadding(new Insets(20, 16, 20, 16));

        ITeam home = currentMatch.getHomeTeam();
        ITeam away = currentMatch.getAwayTeam();
        ITeam pt   = SceneManager.getInstance().getCurrentPlayerTeam();

        VBox homeBox = teamBox(home.getName(), home.getTeamOverallRating(), home.equals(pt));

        VBox scoreBox = new VBox(4);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setPrefWidth(100);
        Label scoreLbl = new Label(homeTotalScore + " – " + awayTotalScore);
        scoreLbl.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        scoreLbl.setTextFill(Color.web(TEXT));
        scoreBox.getChildren().add(scoreLbl);

        if (!periodScores.isEmpty()) {
            HBox chips = new HBox(4);
            chips.setAlignment(Pos.CENTER);
            for (int[] ps : periodScores) {
                Label chip = new Label(ps[0] + "-" + ps[1]);
                chip.setFont(Font.font("Arial", 9));
                chip.setTextFill(Color.web(SUBTEXT));
                chip.setPadding(new Insets(1, 4, 1, 4));
                chip.setStyle("-fx-background-color: #2A3050; -fx-background-radius: 3;");
                chips.getChildren().add(chip);
            }
            scoreBox.getChildren().add(chips);
        }

        VBox awayBox = teamBox(away.getName(), away.getTeamOverallRating(), away.equals(pt));

        board.getChildren().addAll(homeBox, scoreBox, awayBox);

        VBox wrap = new VBox(0, board);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static VBox teamBox(String name, int ovr, boolean isPlayer) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(160);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(isPlayer ? GOLD : TEXT));
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        Label ovrLbl = new Label("OVR " + ovr);
        ovrLbl.setFont(Font.font("Arial", 11));
        ovrLbl.setTextFill(Color.web(SUBTEXT));
        box.getChildren().addAll(nameLbl, ovrLbl);
        return box;
    }

    private static HBox buildPeriodHeader(int idx) {
        String sport = SceneManager.getInstance().getSelectedSport();
        String type  = (sport != null && sport.equalsIgnoreCase("volleyball")) ? "Set" : "Devre";
        int[]  ps    = periodScores.get(idx);
        HBox h = new HBox(8);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(14, 0, 6, 0));
        Label lbl = new Label(type + " " + (idx + 1));
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(GOLD));
        Label score = new Label(ps[0] + " – " + ps[1]);
        score.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        score.setTextFill(Color.web(TEXT));
        score.setPadding(new Insets(2, 8, 2, 8));
        score.setStyle("-fx-background-color: #2A3050; -fx-background-radius: 4;");
        h.getChildren().addAll(lbl, score);
        return h;
    }

    private static VBox buildFullTimeCard() {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 12;");
        card.setPadding(new Insets(24));
        VBox.setMargin(card, new Insets(16, 0, 16, 0));

        Label ft = new Label("MAÇ BİTTİ");
        ft.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        ft.setTextFill(Color.web(GOLD));

        Label score = new Label(homeTotalScore + " – " + awayTotalScore);
        score.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        score.setTextFill(Color.web(TEXT));

        ITeam home = currentMatch.getHomeTeam();
        ITeam away = currentMatch.getAwayTeam();
        String winner;
        if (homeTotalScore > awayTotalScore)      winner = home.getName() + " KAZANDI!";
        else if (awayTotalScore > homeTotalScore) winner = away.getName() + " KAZANDI!";
        else                                       winner = "BERABERE";

        Label winnerLbl = new Label(winner);
        winnerLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        winnerLbl.setTextFill(Color.web(TEXT));

        Label cont = new Label("PANOYA DÖN");
        cont.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        cont.setTextFill(Color.web(BG));
        cont.setPadding(new Insets(12, 30, 12, 30));
        cont.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 8; -fx-cursor: hand;");
        cont.setOnMouseClicked(e -> {
            resetState();
            SceneManager.getInstance().navigateTo("dashboard");
        });

        card.getChildren().addAll(ft, score, winnerLbl, cont);
        return card;
    }

    // ── Break Overlay (Devre/Set arası) ───────────────────────────────────────
    private static void buildBreakOverlay(StackPane parent, String breakTitle, String subtitle) {
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        dimmer.setMaxWidth(Double.MAX_VALUE);
        dimmer.setMaxHeight(Double.MAX_VALUE);
        parent.getChildren().add(dimmer);

        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + CARD + "; -fx-background-radius: 18 18 0 0;");
        panel.setMaxWidth(MAX_W);
        StackPane.setAlignment(panel, Pos.BOTTOM_CENTER);
        parent.getChildren().add(panel);

        HBox handleBar = new HBox();
        handleBar.setAlignment(Pos.CENTER);
        handleBar.setPadding(new Insets(10, 0, 6, 0));
        Rectangle handle = new Rectangle(40, 4);
        handle.setArcWidth(4); handle.setArcHeight(4);
        handle.setFill(Color.web("#444"));
        handleBar.getChildren().add(handle);

        VBox titleArea = new VBox(4);
        titleArea.setAlignment(Pos.CENTER);
        titleArea.setPadding(new Insets(0, 16, 12, 16));
        Label titleLbl = new Label(breakTitle);
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(GOLD));
        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("Arial", 12));
        subLbl.setTextFill(Color.web(SUBTEXT));
        titleArea.getChildren().addAll(titleLbl, subLbl);

        ScrollPane subScroll = buildSubstitutionPanel();
        subScroll.setPrefHeight(300);

        HBox actions = buildBreakActions(parent, dimmer, panel);

        panel.getChildren().addAll(handleBar, titleArea, subScroll, actions);

        panel.setTranslateY(600);
        TranslateTransition tt = new TranslateTransition(Duration.millis(320), panel);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();
    }

    private static ScrollPane buildSubstitutionPanel() {
        ITeam playerTeam = SceneManager.getInstance().getCurrentPlayerTeam();
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + CARD + "; -fx-background-color: " + CARD + "; -fx-border-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + CARD + ";");

        if (playerTeam == null) { scroll.setContent(content); return scroll; }

        final IPlayer[] selectedLineup = {null};

        List<IPlayer> lineup = playerTeam.getStartingLineup();
        List<IPlayer> squad  = playerTeam.getSquad();
        List<IPlayer> bench  = new ArrayList<>();
        for (IPlayer p : squad) { if (!lineup.contains(p)) bench.add(p); }

        Label instr = new Label("Başlangıç oyuncusuna, ardından yedek oyuncuya dokun → değişiklik yap.");
        instr.setFont(Font.font("Arial", 11));
        instr.setTextFill(Color.web(SUBTEXT));
        instr.setPadding(new Insets(8, 16, 8, 16));
        instr.setWrapText(true);
        content.getChildren().add(instr);

        content.getChildren().add(subHeader("BAŞLANGIÇ KADROSU"));
        for (IPlayer p : lineup) {
            HBox row = subRow(p, true);
            row.setOnMouseClicked(e -> {
                if (!p.isInjured()) selectedLineup[0] = p;
                row.setStyle("-fx-background-color: #2D3460;");
            });
            content.getChildren().add(row);
        }

        content.getChildren().add(subHeader("YEDEKLER"));
        for (IPlayer p : bench) {
            HBox row = subRow(p, false);
            row.setOnMouseClicked(e -> {
                if (selectedLineup[0] == null || p.isInjured()) return;
                List<IPlayer> lu = playerTeam.getStartingLineup();
                int idx = lu.indexOf(selectedLineup[0]);
                if (idx >= 0) lu.set(idx, p);
                selectedLineup[0] = null;
                SceneManager.getInstance().navigateTo("match");
            });
            content.getChildren().add(row);
        }

        scroll.setContent(content);
        return scroll;
    }

    private static HBox subRow(IPlayer p, boolean isStarter) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: " + CARD + ";");
        row.setPadding(new Insets(10, 16, 10, 16));
        if (!p.isInjured()) row.setStyle(row.getStyle() + "-fx-cursor: hand;");

        StackPane badge = new StackPane();
        Rectangle rect = new Rectangle(36, 24);
        rect.setArcWidth(4); rect.setArcHeight(4);
        rect.setFill(Color.web(posColor(p.getPosition())));
        Label posLbl = new Label(p.getPosition().length() > 3
                ? p.getPosition().substring(0, 3).toUpperCase() : p.getPosition().toUpperCase());
        posLbl.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        posLbl.setTextFill(Color.WHITE);
        badge.getChildren().addAll(rect, posLbl);

        Label name = new Label(p.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        name.setTextFill(Color.web(p.isInjured() ? RED : TEXT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        if (p.isInjured()) {
            Label inj = new Label("SAKATI");
            inj.setFont(Font.font("Arial", FontWeight.BOLD, 9));
            inj.setTextFill(Color.web(RED));
            row.getChildren().addAll(badge, name, spacer, inj);
        } else {
            Label ovrLbl = new Label(String.valueOf(p.getOverallRating()));
            ovrLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            ovrLbl.setTextFill(Color.web(GOLD));
            row.getChildren().addAll(badge, name, spacer, ovrLbl);
        }

        VBox wrap = new VBox(0, row);
        Region sep = new Region(); sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #2A3050;");
        wrap.getChildren().add(sep);
        HBox outer = new HBox(wrap);
        HBox.setHgrow(wrap, Priority.ALWAYS);
        return outer;
    }

    private static HBox subHeader(String text) {
        HBox h = new HBox();
        h.setPadding(new Insets(10, 16, 4, 16));
        h.setStyle("-fx-background-color: " + CARD2 + ";");
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(SUBTEXT));
        h.getChildren().add(l);
        return h;
    }

    private static HBox buildBreakActions(StackPane parent, StackPane dimmer, VBox panel) {
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(16));
        actions.setStyle("-fx-background-color: " + CARD + ";");

        Label continueBtn = new Label(currentPeriod == 0 ? "▶  BAŞLAT" : "▶  DEVAM ET");
        continueBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        continueBtn.setTextFill(Color.web(BG));
        continueBtn.setPadding(new Insets(14, 0, 14, 0));
        continueBtn.setPrefWidth(220);
        continueBtn.setAlignment(Pos.CENTER);
        continueBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-background-radius: 10; -fx-cursor: hand;");
        continueBtn.setOnMouseClicked(e -> {
            parent.getChildren().remove(dimmer);
            parent.getChildren().remove(panel);
            simulateNextPeriod(parent);
        });

        Label skipBtn = new Label("⏩  TÜMÜNÜ SİMÜLE ET");
        skipBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        skipBtn.setTextFill(Color.web(GOLD));
        skipBtn.setPadding(new Insets(14, 0, 14, 0));
        skipBtn.setPrefWidth(210);
        skipBtn.setAlignment(Pos.CENTER);
        skipBtn.setStyle("-fx-background-color: #2A3050; -fx-background-radius: 10; -fx-cursor: hand;");
        skipBtn.setOnMouseClicked(e -> {
            parent.getChildren().remove(dimmer);
            parent.getChildren().remove(panel);
            simulateAll(parent);
        });

        actions.getChildren().addAll(continueBtn, skipBtn);
        return actions;
    }

    // ── Simülasyon ────────────────────────────────────────────────────────────
    private static void simulateNextPeriod(StackPane parent) {
        ITeam home = currentMatch.getHomeTeam();
        ITeam away = currentMatch.getAwayTeam();

        int[] ps = simulatePeriod(home, away);
        periodScores.add(ps);
        homeTotalScore += ps[0];
        awayTotalScore += ps[1];

        String sport = SceneManager.getInstance().getSelectedSport();
        String type  = (sport != null && sport.equalsIgnoreCase("volleyball")) ? "Set" : "Devre";
        String pfx   = "P" + currentPeriod + ": ";

        matchLog.add(pfx + type + " " + (currentPeriod + 1) + " bitti: "
                + home.getName() + " " + ps[0] + " – " + ps[1] + " " + away.getName());
        addEvents(pfx, home, away, ps);

        currentPeriod++;

        refreshMain(parent);

        if (currentPeriod >= totalPeriods || isDecided()) {
            // Ensure display totals exactly match the official simulated result
            homeTotalScore = homeSimTotal;
            awayTotalScore = awaySimTotal;
            matchFinished = true;
            refreshMain(parent);
        } else {
            String bt = type + " " + currentPeriod + " BİTTİ";
            String bs = (currentPeriod + 1) + ". " + type + " başlamadan önce değişiklik yapabilirsiniz";
            buildBreakOverlay(parent, bt, bs);
        }
    }

    private static void simulateAll(StackPane parent) {
        while (currentPeriod < totalPeriods && !isDecided()) {
            ITeam home = currentMatch.getHomeTeam();
            ITeam away = currentMatch.getAwayTeam();
            int[] ps = simulatePeriod(home, away);
            periodScores.add(ps);
            homeTotalScore += ps[0];
            awayTotalScore += ps[1];
            String pfx = "P" + currentPeriod + ": ";
            String sport = SceneManager.getInstance().getSelectedSport();
            String type  = (sport != null && sport.equalsIgnoreCase("volleyball")) ? "Set" : "Devre";
            matchLog.add(pfx + type + " " + (currentPeriod + 1) + ": "
                    + home.getName() + " " + ps[0] + " – " + ps[1] + " " + away.getName());
            addEvents(pfx, home, away, ps);
            currentPeriod++;
        }
        // Ensure display totals exactly match the official simulated result
        homeTotalScore = homeSimTotal;
        awayTotalScore = awaySimTotal;
        matchFinished = true;
        refreshMain(parent);
    }

    /**
     * Distribute period scores so that they sum to the pre-simulated totals
     * (homeSimTotal / awaySimTotal).  This ensures the score shown in MatchScene
     * always matches what ends up in the fixture list.
     */
    private static int[] simulatePeriod(ITeam home, ITeam away) {
        String sport = SceneManager.getInstance().getSelectedSport();
        boolean isVb = sport != null && sport.equalsIgnoreCase("volleyball");
        Random rnd = new Random();

        int hRemaining = homeSimTotal - homeTotalScore;
        int aRemaining = awaySimTotal - awayTotalScore;
        int periodsLeft = totalPeriods - currentPeriod; // includes this period

        if (isVb) {
            // Each set: home wins (1-0) or away wins (0-1)
            if (periodsLeft <= 1) {
                // Final period — give the win to whoever still needs it
                return hRemaining > 0 ? new int[]{1, 0} : new int[]{0, 1};
            }
            if (hRemaining == 0) return new int[]{0, 1};
            if (aRemaining == 0) return new int[]{1, 0};
            // Weighted random based on remaining set needs
            double hChance = (double) hRemaining / (hRemaining + aRemaining);
            return rnd.nextDouble() < hChance ? new int[]{1, 0} : new int[]{0, 1};
        } else {
            // Football: distribute total goals across halves
            if (periodsLeft <= 1) {
                // Last half — all remaining goals go here
                return new int[]{Math.max(0, hRemaining), Math.max(0, aRemaining)};
            }
            // First half: random split (0 to all of the remaining goals)
            int hFirst = hRemaining > 0 ? rnd.nextInt(hRemaining + 1) : 0;
            int aFirst = aRemaining > 0 ? rnd.nextInt(aRemaining + 1) : 0;
            return new int[]{hFirst, aFirst};
        }
    }

    private static boolean isDecided() {
        String sport = SceneManager.getInstance().getSelectedSport();
        if (sport == null || !sport.equalsIgnoreCase("volleyball")) return false;
        // Best-of-N: decided when someone reaches (totalPeriods+1)/2 set wins
        int needed = (totalPeriods + 1) / 2;
        return homeTotalScore >= needed || awayTotalScore >= needed;
    }

    private static void addEvents(String pfx, ITeam home, ITeam away, int[] ps) {
        String sport = SceneManager.getInstance().getSelectedSport();
        if (sport != null && sport.equalsIgnoreCase("volleyball")) return;
        Random rnd = new Random();
        List<IPlayer> hl = home.getStartingLineup();
        List<IPlayer> al = away.getStartingLineup();
        for (int g = 0; g < ps[0]; g++) {
            String scorer = hl.isEmpty() ? home.getName() : hl.get(rnd.nextInt(hl.size())).getName();
            int min = 1 + rnd.nextInt(45);
            matchLog.add(pfx + "⚽ " + min + "' " + scorer + " (" + home.getName() + ")");
        }
        for (int g = 0; g < ps[1]; g++) {
            String scorer = al.isEmpty() ? away.getName() : al.get(rnd.nextInt(al.size())).getName();
            int min = 1 + rnd.nextInt(45);
            matchLog.add(pfx + "⚽ " + min + "' " + scorer + " (" + away.getName() + ")");
        }
    }

    private static void resetState() {
        currentMatch   = null;
        periodScores   = new ArrayList<>();
        matchLog       = new ArrayList<>();
        currentPeriod  = 0;
        matchFinished  = false;
        homeTotalScore = 0;
        awayTotalScore = 0;
        homeSimTotal   = 0;
        awaySimTotal   = 0;
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